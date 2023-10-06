package com.ezreal.rpc.core.router;

import com.ezreal.rpc.core.common.ChannelFutureWrapper;
import com.ezreal.rpc.core.common.utils.CommonUtil;
import com.ezreal.rpc.core.register.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.ezreal.rpc.core.common.cache.ClientServiceCache.*;

/**
 * 随机路由算法
 *
 * @author Ezreal
 * @Date 2023/10/6
 */
public class RandomRouter implements IRouter {

    @Override
    public void refreshRouteArr(Selector selector) {
        String serviceName = selector.getServiceName();
        if (CommonUtil.isEmpty(serviceName)) {
            throw new RuntimeException("the serviceName should not be empty...");
        }
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(serviceName);
        int length = channelFutureWrappers.size();

        ChannelFutureWrapper[] channelRoutes = new ChannelFutureWrapper[length];
        int[] randomIndex = createRandomIndex(length);
        for (int i = 0; i < length; i++) {
            channelRoutes[i] = channelFutureWrappers.get(randomIndex[i]);
        }
        SERVICE_ROUTE_MAP.put(serviceName, channelRoutes);
    }

    @Override
    public ChannelFutureWrapper select(Selector selector) {
        return CHANNEL_FUTURE_POLLING_REF.getChannelFutureWrapper(selector.getServiceName());
    }

    @Override
    public void updateWeight(URL url) {
        String serviceName = url.getServiceName();

        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(serviceName);
        Integer[] weightArr = createWeightArr(channelFutureWrappers);
        Integer[] randomArr = createRandomWeight(weightArr);

        ChannelFutureWrapper[] channelRoutes = new ChannelFutureWrapper[randomArr.length];
        for (int i = 0; i < randomArr.length; i++) {
            channelRoutes[i] = channelFutureWrappers.get(randomArr[i]);
        }

    }

    /**
     * 让权重数组随机化
     *
     * @param weightArr 权重数组
     * @return 结果
     */
    private static Integer[] createRandomWeight(Integer[] weightArr) {
        int len = weightArr.length;

        Random random = new Random();
        for (int i = 0; i < len; i++) {
            int ra = random.nextInt(len);

            if (i == ra) {
                continue;
            }

            int temp = weightArr[ra];
            weightArr[ra] = weightArr[i];
            weightArr[i] = temp;
        }

        return weightArr;
    }

    public static void main(String[] args) {
        List<ChannelFutureWrapper> channelFutureWrappers = new ArrayList<>();
        channelFutureWrappers.add(new ChannelFutureWrapper(null, null, 100));
        channelFutureWrappers.add(new ChannelFutureWrapper(null, null, 200));
        channelFutureWrappers.add(new ChannelFutureWrapper(null, null, 9300));
        channelFutureWrappers.add(new ChannelFutureWrapper(null, null, 400));
        Integer[] r = createWeightArr(channelFutureWrappers);
        Integer[] randomWeight = createRandomWeight(r);
        System.out.println(randomWeight);
    }

    /**
     * 创建权重数组
     *
     * @param channelFutureWrappers channel 集合
     * @return 权重数组
     */
    private static Integer[] createWeightArr(List<ChannelFutureWrapper> channelFutureWrappers) {

        ArrayList<Integer> weightList = new ArrayList<>();
        for (int i = 0; i < channelFutureWrappers.size(); i++) {
            ChannelFutureWrapper channel = channelFutureWrappers.get(i);
            int weight = channel.getWeight();
            int val = weight / 100;

            // 共有val个i
            for (int j = 0; j < val; j++) {
                weightList.add(i);
            }
        }
        return weightList.toArray(new Integer[0]);
    }

    /**
     * 创建随机索引
     *
     * @param size 长度
     * @return 索引数组
     */
    private int[] createRandomIndex(int size) {
        int[] result = new int[size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            result[i] = -1;
        }
        for (int i = 0; i < size; i++) {
            int ra = random.nextInt(size);
            while (contains(result, ra)) {
                ra = random.nextInt(size);
            }
            result[i] = ra;
        }

        return result;
    }

    /**
     * 判断某个数是否存在于数组
     *
     * @param arr 数组
     * @param val 值
     * @return 结果
     */
    private boolean contains(int[] arr, int val) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == val) {
                return true;
            }
        }
        return false;
    }
}
