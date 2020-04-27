package com.mengma.config.center.config.impl;

import com.mengma.config.center.config.ConfigService;
import com.mengma.config.center.config.DataSourceDO;
import com.mengma.config.center.constants.ConfigConstants;
import com.mengma.config.center.datasource.MyDataSource;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author fgm
 * @description
 * @date 2020-04-25
 ***/
@Component
public class ConfigServiceImpl implements ConfigService, ApplicationContextAware {

    private static CuratorFramework client;

    private ApplicationContext applicationContext;


    private String url;
    private String username;
    private String password;
    private String driverClassName;

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




    @Override
    public DataSourceDO getConfig() {
        DataSourceDO dataSourceDO=new DataSourceDO();
        try {
            init();
            dataSourceDO.setUrl(url);
            dataSourceDO.setPassword(password);
            dataSourceDO.setUsername(username);
            dataSourceDO.setDriverClassName(driverClassName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSourceDO;
    }


    public void init() throws Exception {
        //获取客户端地址
        String path = ConfigConstants.DATA_REGISTER_PATH;
        initConfig(path);
        registerWatcher(path);
    }

    /**
     * 初始化客户端
     */
    private void initConfig(String path) {
        String urlPath = path + ConfigConstants.URL;
        String usernamePath = path + ConfigConstants.USERNAME;
        String passwordPath = path + ConfigConstants.PASSWORD;
        String driverClassNamePath = path + ConfigConstants.DRIVER_CLASS_NAME;

        try {
            byte[] urlBytes=client.getData().forPath(urlPath);
            byte[] usernameBytes=client.getData().forPath(usernamePath);
            byte[] passwordBytes=client.getData().forPath(passwordPath);
            byte[] driverClassNameBytes=client.getData().forPath(driverClassNamePath);
            this.url = new String(urlBytes,"utf-8");
            this.password =new String(passwordBytes,"utf-8");
            this.username =new String(usernameBytes,"utf-8");
            this.driverClassName =new String(driverClassNameBytes,"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册监听
     * @param path
     * @throws Exception
     */
    private void registerWatcher(final String path) throws Exception {
        PathChildrenCache childrenCache=new PathChildrenCache(client,path,true);
        PathChildrenCacheListener childrenCacheListener=new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent)
                throws Exception {
                //只有节点更新才会触发
                if(!PathChildrenCacheEvent.Type.CHILD_UPDATED.equals(pathChildrenCacheEvent.getType())){
                   return;
                }
                System.out.println("eventType:"+pathChildrenCacheEvent.getType());
                //重新初始化配置
                initConfig(path);
                DataSourceDO dataSourceDO=DataSourceDO.builder()
                    .url(getUrl())
                    .password(getPassword())
                    .username(getUsername())
                    .driverClassName(getDriverClassName());
                //刷新dataSource
                MyDataSource myDataSource=(MyDataSource)applicationContext.getBean(ConfigConstants.DATA_SOURCE_BEAN_NAME);
                System.out.println("开始刷新数据源....");
                myDataSource.refresh(dataSourceDO);
            }
        };
        childrenCache.getListenable().addListener(childrenCacheListener);
        childrenCache.start();
    }





    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
