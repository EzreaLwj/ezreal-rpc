package com.ezreal.rpc.core.register.zookeeper;

import org.apache.zookeeper.Watcher;

import java.util.List;

/**
 * Zookeeper统一设计了一套模版抽象类
 * @author Ezreal
 * @Date 2023/10/4
 */
public abstract class AbstractZookeeperClient {

    private String zkAddress;

    private Integer maxRetryTimes;

    private Integer baseSleepTimes;

    public AbstractZookeeperClient(String zkAddress) {
        if (zkAddress == null) {
            throw new RuntimeException("The zookeeper's address must not be null.");
        }
        this.zkAddress = zkAddress;
        this.baseSleepTimes = 1000;
        this.maxRetryTimes = 3;
    }

    public AbstractZookeeperClient(String zkAddress, Integer maxRetryTimes, Integer baseSleepTimes) {
        if (zkAddress == null) {
            throw new RuntimeException("The zookeeper's address must not be null.");
        }
        this.zkAddress = zkAddress;
        this.maxRetryTimes = maxRetryTimes == null ? 1000 : maxRetryTimes;
        this.baseSleepTimes = baseSleepTimes == null ? 3 : baseSleepTimes;
    }

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public Integer getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public Integer getBaseSleepTimes() {
        return baseSleepTimes;
    }

    public void setBaseSleepTimes(int baseSleepTimes) {
        this.baseSleepTimes = baseSleepTimes;
    }

    public abstract Object getClient();

    public abstract void updateNodeData(String address, String data);

    /**
     * 获取指定节点的数据
     * @param path 路径
     * @return 数据
     */
    public abstract String getNodeData(String path);

    /**
     * 获取指定目录下所有节点的数据
     * @param path 指定目录
     * @return 数据
     */
    public abstract List<String> getChildrenData(String path);

    /**
     * 创建持久化节点的数据
     * @param address 地址
     * @param data 数据
     */
    public abstract void createPersistentData(String address, String data);

    /**
     * 创建持久化带序号节点的数据
     * @param address 地址
     * @param data 数据
     */
    public abstract void createPersistentSeqData(String address, String data);

    /**
     * 创建临时节点的数据
     * @param address 地址
     * @param data 数据
     */
    public abstract void createTemporaryData(String address, String data);

    /**
     * 创建临时带序号节点的数据
     * @param address 地址
     * @param data 数据
     */
    public abstract void createTemporarySeqData(String address, String data);

    /**
     * 设置某个节点的数值
     *
     * @param address
     * @param data
     */
    public abstract void setTemporaryData(String address, String data);

    /**
     * 断开客户端连接
     */
    public abstract void destroy();

    /**
     * 展示节点下边的数据
     *
     * @param address
     */
    public abstract List<String> listNode(String address);

    /**
     * 删除节点下边的数据
     *
     * @param address
     * @return
     */
    public abstract boolean deleteNode(String address);

    /**
     * 判断是否存在其他节点
     *
     * @param address
     * @return
     */
    public abstract boolean existNode(String address);


    /**
     * 监听path路径下某个节点的数据变化
     *
     * @param path
     */
    public abstract void watchNodeData(String path, Watcher watcher);

    /**
     * 监听子节点下的数据变化
     *
     * @param path
     * @param watcher
     */
    public abstract void watchChildNodeData(String path, Watcher watcher);
}
