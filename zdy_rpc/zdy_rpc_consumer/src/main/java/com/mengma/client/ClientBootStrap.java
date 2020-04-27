package com.mengma.client;

import com.mengma.curator.rpc.DiscoveryCenter;
import com.mengma.curator.rpc.impl.DiscoveryCenterImpl;
import com.mengma.service.UserService;

public class ClientBootStrap {

    public static  final String providerName="UserService#sayHello#";

    public static void main(String[] args) throws InterruptedException {

        DiscoveryCenter discoveryCenter=new DiscoveryCenterImpl();

        RpcConsumer rpcConsumer = new RpcConsumer(discoveryCenter);

        UserService proxy = (UserService) rpcConsumer.createProxy(UserService.class);


        while (true){
            Thread.sleep(2000);
            System.out.println(proxy.sayHello("are you ok?"));
        }


    }




}
