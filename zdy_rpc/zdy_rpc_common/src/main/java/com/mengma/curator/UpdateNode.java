package com.mengma.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

/**
 * @author fgm
 * @description   更新节点内容
 * @date 2020-04-25
 ***/
public class UpdateNode {


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
        byte[] bytes = client.getData().forPath(path);
        System.out.println("获取到的节点内容:"+new String(bytes,"utf-8"));

        // 状态信息
        Stat stat=new Stat();
        client.getData().storingStatIn(stat).forPath(path);
        System.out.println("获取到的节点状态信息"+stat);


        //更新节点
        int cversion = client.setData().withVersion(stat.getVersion()).forPath(path, "修改内容1".getBytes()).getVersion();
        System.out.println("当前的最新版本为："+cversion);
        byte[] bytes2 = client.getData().forPath(path);
        System.out.println("修改后的节点内容："+new String(bytes2,"utf-8"));

        //BadVersionException
        client.setData().withVersion(stat.getVersion()).forPath(path,"修改内容2".getBytes());
        byte[] bytes3=client.getData().forPath(path);
        System.out.println("修改后的内容节点:"+new String(bytes3,"utf-8"));





    }

}
