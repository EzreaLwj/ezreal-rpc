package com.ezreal.rpc.core.register.zookeeper;

import com.ezreal.rpc.core.common.event.ListenerLoader;
import com.ezreal.rpc.core.common.event.UpdateServiceEvent;
import com.ezreal.rpc.core.common.event.data.URLChangeWrapper;
import com.ezreal.rpc.core.register.RegistryService;
import com.ezreal.rpc.core.register.URL;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.List;

/**
 * @author Ezreal
 * @Date 2023/10/4
 */
public class ZookeeperRegister extends AbstractRegister implements RegistryService {

    private AbstractZookeeperClient zookeeperClient;

    private final String ROOT = "/ezreal-rpc";

    public ZookeeperRegister(String address) {
        this.zookeeperClient = new CuratorZookeeperClient(address);
    }

    private String getProviderPath(URL url) {
        return ROOT + "/" + url.getServiceName() + "/provider/" + url.getParams().get("host") + ":" + url.getParams().get("port");
    }

    private String getConsumerPath(URL url) {
        return ROOT + "/" + url.getServiceName() + "/consumer/" + url.getApplicationName() + ":" + url.getParams().get("host") + ":" + url.getParams().get("port");

    }

    @Override
    public void register(URL url) {
        if (!zookeeperClient.existNode(ROOT)) {
            zookeeperClient.createTemporaryData(ROOT, "");
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
        // 监听是否有新的服务注册上来
        String newServerNodePath = ROOT + "/" + url.getServiceName() + "/provider";
        watchNewServerNode(newServerNodePath);
    }

    private void watchNewServerNode(String newServerNodePath) {
        zookeeperClient.watchChildNodeData(newServerNodePath, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

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

}
