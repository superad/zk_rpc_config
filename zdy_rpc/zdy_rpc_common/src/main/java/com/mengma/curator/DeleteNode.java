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
public class DeleteNode {


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

        String path = "/curator-rpc";


        //删除节点  指定version为-1 表示删除最新节点
        client.delete().deletingChildrenIfNeeded().withVersion(-1).forPath(path);

        System.out.println("删除成功，删除节点"+path);


    }

}
