package com.simplegame.deploy.network.data;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 
 * @Author zeusgooogle@gmail.com
 * @sine 2015年9月14日 上午11:16:10
 * 
 */
@Component
public class DataServerListener {

    private Logger LOG = LogManager.getLogger(getClass());

    @Value(value = "${command.server.port}")
    private int port;

    @PostConstruct
    public void start() {
        
        Thread thread = new Thread("IO-DataServerListener") {
            @Override
            public void run() {

                EventLoopGroup bossGroup = new NioEventLoopGroup(1);
                EventLoopGroup workerGroup = new NioEventLoopGroup();

                try {
                    ServerBootstrap b = new ServerBootstrap();
                    b.group(bossGroup, workerGroup)
                     .channel(NioServerSocketChannel.class)
                     .childHandler(new DataServerInitializer());

                    Channel channel = b.bind(port).sync().channel();

                    LOG.info("data server start success. bind port: {}", port);
                    
                    // shut down your server.
                    channel.closeFuture().sync();
                    
                } catch(Exception e) {
                    LOG.error("data server start failed.", e);
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            }
        };
        
        thread.setDaemon(true);
        thread.start();
    }
}
