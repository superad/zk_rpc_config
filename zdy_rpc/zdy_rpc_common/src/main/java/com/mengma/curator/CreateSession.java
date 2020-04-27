package com.mengma.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author fgm
 * @description   客户端
 * @date 2020-04-25
 ***/
public class CreateSession {


    public static void main(String[] args) {
        //重试策略
        RetryPolicy retryPolicy=new ExponentialBackoffRetry(1000,3);

       // CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181",retryPolicy);
        CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .sessionTimeoutMs(30000)
            .connectionTimeoutMs(50000)
            .retryPolicy(retryPolicy)
            .namespace("mengma")
            .build();

        client.start();
        System.out.println("会话建立了");



    }

}
