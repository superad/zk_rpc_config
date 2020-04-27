package com.mengma.curator.rpc.impl;

import com.mengma.curator.constants.ZkConstants;
import com.mengma.curator.rpc.RegisterCenter;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author fgm
 * @description  服务注册
 * @date 2020-04-25
 ***/
public class RegisterCenterImpl implements RegisterCenter {

    //初始化客户端
    private static  CuratorFramework client;
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


    public void register(String serviceName, String serviceAddress) {
        String servicePath = ZkConstants.RPC_REGISTER_PATH+"/"+serviceName;
        try {
          if(null==client.checkExists().creatingParentContainersIfNeeded().forPath(servicePath)){
              client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(servicePath,"0".getBytes());
          }
          //注册服务地址  使用临时节点
          String addressPath = servicePath+"/"+serviceAddress;
          String nodePath=client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(addressPath,"0".getBytes());
          System.out.println("发布节点:"+nodePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
