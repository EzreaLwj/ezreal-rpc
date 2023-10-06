package com.ezreal.rpc.core.register.zookeeper;

import com.alibaba.fastjson.JSON;
import com.ezreal.rpc.core.common.event.ListenerLoader;
import com.ezreal.rpc.core.common.event.UpdateServiceEvent;
import com.ezreal.rpc.core.common.event.WeightDataChangeEvent;
import com.ezreal.rpc.core.common.event.data.URLChangeWrapper;
import com.ezreal.rpc.core.register.RegistryService;
import com.ezreal.rpc.core.register.URL;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.ezreal.rpc.core.common.cache.ClientServiceCache.I_ROUTER;

/**
 * @author Ezreal
 * @Date 2023/10/4
 */
public class ZookeeperRegister extends AbstractRegister implements RegistryService {

    private Logger logger = LoggerFactory.getLogger(ZookeeperRegister.class);

    private AbstractZookeeperClient zookeeperClient;

    private final String ROOT = "/ezrealrpc";

    public ZookeeperRegister(String address) {
        this.zookeeperClient = new CuratorZookeeperClient(address);
    }

    private String getProviderPath(URL url) {
        return ROOT + "/" + url.getServiceName() + "/provider/" + url.getParams().get("host") + ":" + url.getParams().get("port");
    }

    private String getConsumerPath(URL url) {
        return ROOT + "/" + url.getServiceName() + "/consumer/" + url.getApplicationName() + ":" + url.getParams().get("host");

    }

    @Override
    public void register(URL url) {
        if (!zookeeperClient.existNode(ROOT)) {
            zookeeperClient.createPersistentData(ROOT, "");
        }
        String providerPath = getProviderPath(url);
        String providerPathValue = URL.buildProviderUrlStr(url);

        // 如果不存在就直接创建，存在就先删除再创建
        if (!zookeeperClient.existNode(providerPath)) {
            zookeeperClient.createTemporaryData(providerPath, providerPathValue);
        } else {
            zookeeperClient.deleteNode(providerPath);
            zookeeperClient.createTemporaryData(providerPath, providerPathValue);
        }
        super.register(url);
    }

    @Override
    public void unRegister(URL url) {
        zookeeperClient.deleteNode(getProviderPath(url));
        super.unRegister(url);
    }

    @Override
    public void subscribe(URL url) {
        if (!zookeeperClient.existNode(ROOT)) {
            zookeeperClient.createTemporaryData(ROOT, "");
        }
        String consumerPath = getConsumerPath(url);
        String consumerPathValue = URL.buildConsumerUrlStr(url);

        if (!zookeeperClient.existNode(consumerPath)) {
            zookeeperClient.createTemporaryData(consumerPath, consumerPathValue);
        } else {
            zookeeperClient.deleteNode(consumerPath);
            zookeeperClient.createTemporaryData(consumerPath, consumerPathValue);
        }
        super.subscribe(url);
    }

    @Override
    public void doUnScribe(URL url) {
        zookeeperClient.deleteNode(getConsumerPath(url));
        super.doUnScribe(url);
    }

    @Override
    public void doBeforeSubscribe(URL url) {

    }

    @Override
    public void doAfterSubscribe(URL url) {
        HashMap<String, String> params = url.getParams();
        // 监听是否有新的服务注册上来
        String newServerNodePath = ROOT + "/" + params.get("providerPath");
        logger.info("监听的子节点列表路径为：" + newServerNodePath);
        watchNewServerNode(newServerNodePath);

        // 监听服务的权值是否发生变化
        List<String> ips = JSON.parseObject(params.get("providerIps"), List.class);
        for (String ip : ips) {
            String watchPath = newServerNodePath + "/" + ip;
            logger.info("监听的节点值路径为：" + watchPath);
            watchDataChange(watchPath);
        }
    }

    /**
     * 监听服务节点的权值是否发生变化
     *
     * @param serverNodeDataPath 变化路径
     */
    private void watchDataChange(String serverNodeDataPath) {
        zookeeperClient.watchNodeData(serverNodeDataPath, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

                logger.info("节点的权值发生变化...");
                // 获取变化后节点的值
                String nodeData = zookeeperClient.getNodeData(serverNodeDataPath);
                nodeData = nodeData.replace(";", "/");

                // 解析节点的值 封装成一个 ProviderNodeInfo
                ProviderNodeInfo providerNodeInfo = URL.buildURLFromUrlStr(nodeData);
                WeightDataChangeEvent weightDataChangeEvent = new WeightDataChangeEvent();
                weightDataChangeEvent.setData(providerNodeInfo);
                ListenerLoader.send(weightDataChangeEvent);

                // 继续监听
                watchDataChange(serverNodeDataPath);
            }
        });
    }

    private void watchNewServerNode(String newServerNodePath) {
        zookeeperClient.watchChildNodeData(newServerNodePath, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                logger.info("服务提供方发生变化...");
                String path = watchedEvent.getPath();
                List<String> childrenData = zookeeperClient.getChildrenData(path);
                String serviceName = path.split("/")[2];

                URLChangeWrapper urlChangerWrapper = new URLChangeWrapper();
                urlChangerWrapper.setProviderUrl(childrenData);
                urlChangerWrapper.setServiceName(serviceName);

                // 异步解耦
                UpdateServiceEvent updateServiceEvent = new UpdateServiceEvent();
                updateServiceEvent.setData(urlChangerWrapper);
                ListenerLoader.send(updateServiceEvent);

                // 继续监听
                watchNewServerNode(newServerNodePath);
            }
        });
    }

    @Override
    public List<String> getProviderIps(String serviceName) {
        List<String> ips = zookeeperClient.getChildrenData(ROOT + "/" + serviceName + "/provider");
        return ips;
    }

    @Override
    public Map<String, String> getServiceWeightMap(String serviceName) {
        List<String> childrenDatas = zookeeperClient.getChildrenData(ROOT + "/" + serviceName + "/provider");
        Map<String, String> result = new HashMap<>();

        for (String childrenData : childrenDatas) {
            String nodeData = zookeeperClient.getNodeData(ROOT + "/" + serviceName + "/provider/" + childrenData);
            result.put(childrenData, nodeData);
        }
        return result;
    }

}
