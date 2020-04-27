package com.mengma.server;

import com.google.common.collect.Lists;
import com.mengma.annotation.RPCService;
import com.mengma.curator.rpc.RegisterCenter;
import com.mengma.curator.rpc.RpcRegisterService;
import com.mengma.curator.rpc.impl.RegisterCenterImpl;
import com.mengma.handler.RequestDecoder;
import com.mengma.handler.ResponseEncoder;
import com.mengma.handler.UserServerHandler;
import com.mengma.pojo.RPCRequest;
import com.mengma.pojo.RPCResponse;
import com.mengma.serializer.JSONSerializer;
import com.mengma.serializer.Serializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author fgm
 * @description  RPC Service
 * @date 2020-04-11
 ***/
@Component
public class RPCServer implements ApplicationContextAware{

    private static List<String> serverList= Lists.newArrayList("127.0.0.1:8990","127.0.0.1:8991");

    private RegisterCenter registerCenter = new RegisterCenterImpl();

    private List<Object> serviceBeans=Lists.newArrayList();

    @PostConstruct
    public void init(){
        try {
            for(String serverAddress:serverList){
                String[] serverInfos=serverAddress.split(":");
                String  host = serverInfos[0];
                Integer port = Integer.valueOf(serverInfos[1]);
                RpcRegisterService  registerService = new RpcRegisterService(registerCenter,serverAddress);
                //启动服务
                startServer(host,port);
                //绑定服务
                registerService.bind(serviceBeans);
                //发布服务
                registerService.publisher();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    public static void startServer(String hostName,int port) throws InterruptedException {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        final Serializer serializer = new JSONSerializer();

        io.netty.bootstrap.ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new ResponseEncoder(RPCResponse.class,serializer));
                    pipeline.addLast(new RequestDecoder(RPCRequest.class,serializer));
                    pipeline.addLast(new UserServerHandler());

                }
            });
        serverBootstrap.bind(hostName,port).sync();

        System.out.println("RPC server start at "+port);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beans=applicationContext.getBeansWithAnnotation(RPCService.class);
        for(Map.Entry<String, Object> entry:beans.entrySet()){
            serviceBeans.add(entry.getValue());
        }

    }
}
