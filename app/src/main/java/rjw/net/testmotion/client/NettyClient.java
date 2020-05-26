package rjw.net.testmotion.client;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @description:
 * @author: yaKun.shi
 * @create: 2020-03-24 14:42
 **/
public class NettyClient {

    private String host = "192.168.1.3";
    private int port=10000;

    private EventLoopGroup group = new NioEventLoopGroup();

    private SocketChannel socketChannel;

    private ConcurrentHashMap<String, NettyClient> map = new ConcurrentHashMap();


    /**
     * 发送消息
     */
    public void sendMsg(String msg) {
        socketChannel.writeAndFlush(msg);
    }

    public void start() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(host, port)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new NettyClientInitializer());
        ChannelFuture future = bootstrap.connect();
        //客户端断线重连逻辑

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    System.out.println("连接Netty服务端成功");
                } else {
                    System.out.println("连接失败，进行断线重连");
                    channelFuture.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            start();
                        }
                    }, 20, TimeUnit.SECONDS);
                }
            }
        });
        socketChannel = (SocketChannel) future.channel();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public void shutDown() {
        socketChannel.shutdown();
    }
}
