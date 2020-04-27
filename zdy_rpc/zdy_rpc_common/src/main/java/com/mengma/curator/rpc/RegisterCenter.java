package com.mengma.curator.rpc;

/**
 * @author fgm
 * @description  服务注册中心
 * @date 2020-04-25
 ***/
public interface RegisterCenter {

    /**
     * 注册服务
     * @param serviceName
     * @param serviceAddress
     */
    void register(String serviceName,String serviceAddress);

}
