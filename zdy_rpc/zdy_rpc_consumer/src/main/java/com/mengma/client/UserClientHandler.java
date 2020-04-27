package com.mengma.client;

import com.mengma.curator.rpc.DiscoveryCenter;
import com.mengma.pojo.RPCRequest;
import com.mengma.pojo.RPCResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class UserClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext context;
    private Object data;
    private RPCRequest  request;
    private DiscoveryCenter discoveryCenter;


    public  UserClientHandler(DiscoveryCenter discoveryCenter){
        this.discoveryCenter = discoveryCenter;
    }


    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("channelActive....");
        context = ctx;
    }

    /**
     * 收到服务端数据，唤醒等待线程
     */

    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) {
        RPCResponse response =(RPCResponse) msg;
        System.out.println("response Id : "+response.getResponseId());
        if(RPCResponse.SUCCESS.equalsIgnoreCase(response.getCode())){
            this.data = response.getObject();
            notify();
        }else{
            throw new RuntimeException("远程调用异常:"+response.getCode()+","+response.getMsg());
        }

    }

    /**
     * 写出数据，开始等待唤醒
     */

    public synchronized Object call() throws InterruptedException {
        Long startTime=System.currentTimeMillis();
        System.out.println("request send....");
        context.writeAndFlush(request);
        wait();
        Long endTime = System.currentTimeMillis();
        Long  cost = endTime-startTime;
        String serviceName=request.getClassName();
        String serviceAddress = request.getServiceAddress();
        discoveryCenter.registerCostTime(serviceName,serviceAddress,cost);
        return data;
    }

    /*
     设置参数
     */
    void setData(RPCRequest  request) {
        this.request = request;
    }



}
