package com.mengma.curator.rpc.impl;

import com.mengma.curator.constants.ZkConstants;
import com.mengma.curator.rpc.DiscoveryCenter;
import com.mengma.utils.ExpiryMap;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;

/**
 * @author fgm
 * @description  服务发现
 * @date 2020-04-25
 ***/
public class DiscoveryCenterImpl implements DiscoveryCenter {

    private List<String> serverList;
    /**默认失效时间*/
    private static Long DEFAULT_EXPIRY=5 * 10000L;

    //初始化客户端
    private static CuratorFramework client;

    /**
     * 过期map
     */
    private ExpiryMap<String,Long> expiryMap = new ExpiryMap(DEFAULT_EXPIRY);

    static {
        RetryPolicy retryPolicy=new ExponentialBackoffRetry(1000,3);
        client = CuratorFrameworkFactory.builder()
            .connectString(ZkConstants.CONNECTION)
            .sessionTimeoutMs(3000)
            .connectionTimeoutMs(5000)
            .retryPolicy(retryPolicy)
            .namespace("mengma")
            .build();
        client.start();
    }

    @Override
    public String discover(String serviceName) {
        final String path = ZkConstants.RPC_REGISTER_PATH + "/" + serviceName;
        try {
            serverList=client.getChildren().forPath(path);
            //注册观察者
            registerWatcher(path);
            if(null==serverList||serverList.size()==0){
                return null;
            }
            if(serverList.size()==1){
                return serverList.get(0);
            }
            String  miniServerAddress = serverList.get(0);
            String addressKey=getServerAddressKey(serviceName,miniServerAddress);

            Long miniCostTime = expiryMap.get(addressKey);
            miniCostTime = null==miniCostTime?0:miniCostTime;
            for(String serverAddress:serverList){
                String key = getServerAddressKey(serviceName,serverAddress);
                Long costTime = expiryMap.get(key);
                //耗时为0，默认返回
                if(null==costTime){
                    miniServerAddress = serverAddress;
                    registerCostTime(serviceName,serverAddress,0L);
                    break;
                }
                if(costTime<=miniCostTime){
                    miniServerAddress = serverAddress;
                }
            }
            System.out.println(serviceName+"be discovered at server: "+miniServerAddress);
            return miniServerAddress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取服务地址的key
     * @param serviceName
     * @param serverAddress
     * @return
     */
    private String getServerAddressKey(String serviceName, String serverAddress) {
        return serviceName + "/" + serverAddress;
    }

    /**
     * 注册耗时时间
     * @param serviceName
     * @param serverAddress
     * @param costTime
     */
    @Override
    public void registerCostTime(String serviceName,String serverAddress,Long costTime) {
        String key =  getServerAddressKey(serviceName,serverAddress);
        expiryMap.put(key,costTime,DEFAULT_EXPIRY);
    }

    private void registerWatcher(final String path) throws Exception {
        PathChildrenCache childrenCache=new PathChildrenCache(client,path,true);
        PathChildrenCacheListener childrenCacheListener=new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent)
                throws Exception {
                serverList=curatorFramework.getChildren().forPath(path);
            }
        };
        childrenCache.getListenable().addListener(childrenCacheListener);
        childrenCache.start();
    }

}
