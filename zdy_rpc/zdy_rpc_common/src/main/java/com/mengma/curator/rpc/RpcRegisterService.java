package com.mengma.curator.rpc;

import com.mengma.annotation.RPCService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fgm
 * @description RPC服务注册server
 * @date 2020-04-25
 ***/
public class RpcRegisterService {

    private static Map<String,Object> serviceMap=new HashMap();

    private String serviceAddress;
    private RegisterCenter registerCenter;

    public RpcRegisterService(RegisterCenter registerCenter, String serviceAddress){
        this.registerCenter = registerCenter;
        this.serviceAddress = serviceAddress;
    }

    /**
     * 绑定服务
     * @param services
     */
    public void bind(List<Object> services){
        for(Object object:services){
            if(object.getClass().isAnnotationPresent(RPCService.class)){
                RPCService rpcService=object.getClass().getAnnotation(RPCService.class);
                String serviceName = rpcService.value().getName();
                serviceMap.put(serviceName,object);
            }
        }
    }

    /**
     * 发布服务
     */
    public void publisher(){
        for(Map.Entry<String, Object> entry:serviceMap.entrySet()){
             String serviceName=entry.getKey();
            registerCenter.register(serviceName,serviceAddress);
            System.out.println("服务注册成功 :"+serviceName+">>"+serviceAddress);
        }
    }

}
