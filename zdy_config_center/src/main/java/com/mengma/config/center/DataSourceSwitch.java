package com.mengma.config.center;

import com.mengma.config.center.constants.ConfigConstants;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.util.StringUtils;

/**
 * @author fgm
 * @description   数据源切换管理工具
 * @date 2020-04-27
 ***/
public class DataSourceSwitch {

  private static CuratorFramework client;
    static {
        RetryPolicy retryPolicy=new ExponentialBackoffRetry(1000,3);
        client = CuratorFrameworkFactory.builder()
            .connectString(ConfigConstants.CONNECTION)
            .sessionTimeoutMs(3000)
            .connectionTimeoutMs(5000)
            .retryPolicy(retryPolicy)
            .namespace("mengma")
            .build();
        client.start();
    }

    public static void main(String[] args) throws Exception {
        String basePath = ConfigConstants.DATA_REGISTER_PATH;


        String urlPath = basePath + ConfigConstants.URL;
        String usernamePath = basePath + ConfigConstants.USERNAME;
        String passwordPath = basePath + ConfigConstants.PASSWORD;
        String driverClassNamePath = basePath + ConfigConstants.DRIVER_CLASS_NAME;

        String urlData = "jdbc:mysql://localhost:3306/test?characterEncoding=utf8&useSSL=false";
        String usernameData = "root";
        String passwordData = "root";
        String driverClassNameData = "com.mysql.jdbc.Driver";

        String updateUrlData ="jdbc:mysql://localhost:3306/mengma_db?characterEncoding=utf8&useSSL=false";


        //创建或更新节点
        createOrUpdatePath(urlPath,updateUrlData);
        createOrUpdatePath(usernamePath,usernameData);
        createOrUpdatePath(passwordPath,passwordData);
        createOrUpdatePath(driverClassNamePath,driverClassNameData);





    }

    /**
     * 创建或者更新路径
     * @param path
     * @param data
     */
    private static void createOrUpdatePath(String path, String data) throws Exception {
        if(StringUtils.isEmpty(data)){
            data = "0";
        }
        if(client.checkExists().forPath(path)==null){
           String nodePath= client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,data.getBytes());
            System.out.println("创建路径节点成功:"+nodePath);
        }else{
            client.setData().withVersion(-1).forPath(path,data.getBytes());
            byte[] bytes=client.getData().forPath(path);
            System.out.println("更新节点成功，更新后的数据为："+new String(bytes,"utf-8"));
        }

    }
}
