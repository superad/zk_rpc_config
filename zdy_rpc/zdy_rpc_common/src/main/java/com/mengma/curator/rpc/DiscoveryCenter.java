package com.mengma.curator.rpc;

/**
 * @author fgm
 * @description  服务发现
 * @date 2020-04-25
 ***/
public interface DiscoveryCenter {

    /**
     * 服务发现
     * @param serviceName
     * @return
     */
    String discover(String serviceName);

    /**
     * 注册耗时时间
     * @param serviceName
     * @param serverAddress
     * @param costTime
     */
    void registerCostTime(String serviceName,String serverAddress,Long costTime);


}
