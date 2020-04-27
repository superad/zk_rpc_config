package com.mengma.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * @author fgm
 * @description   获取节点的数据内容和状态信息
 * @date 2020-04-25
 ***/
public class GetNode {


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

        byte[] bytes = client.getData().forPath(path);
        System.out.println("获取到的节点内容:"+new String(bytes,"utf-8"));

        Stat stat=new Stat();
        client.getData().storingStatIn(stat).forPath(path);

        System.out.println("获取到的节点状态信息"+stat);

    }

}
