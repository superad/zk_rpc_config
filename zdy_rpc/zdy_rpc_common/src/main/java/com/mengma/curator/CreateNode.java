package com.mengma.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author fgm
 * @description   客户端
 * @date 2020-04-25
 ***/
public class CreateNode {


    public static void main(String[] args) throws Exception {
        //重试策略
        RetryPolicy retryPolicy=new ExponentialBackoffRetry(1000,3);

        CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .sessionTimeoutMs(30000)
            .connectionTimeoutMs(50000)
            .retryPolicy(retryPolicy)
            .namespace("mengma")
            .build();

        client.start();
        System.out.println("会话建立了");

        String path = "/curator-rpc/c1";

        //创建节点
        String s = client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,"init".getBytes());

        System.out.println("节点创建成功,该节点路径："+s);




    }

}
