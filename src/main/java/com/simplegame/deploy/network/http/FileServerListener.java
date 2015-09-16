package com.simplegame.deploy.network.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.simplegame.deploy.bus.file.config.FileServerConfig;

/**
 * 
 * @Author zeusgooogle@gmail.com
 * @sine 2015年9月14日 上午11:16:10
 * 
 */
@Component
public class FileServerListener {

    private Logger LOG = LogManager.getLogger(getClass());

    @Resource
    private FileServerConfig fileServerConfig;

    @PostConstruct
    public void start() {
        
        Thread thread = new Thread("IO-FileServerListener") {
            @Override
            public void run() {

                EventLoopGroup bossGroup = new NioEventLoopGroup(1);
                EventLoopGroup workerGroup = new NioEventLoopGroup();

                try {
                    ServerBootstrap b = new ServerBootstrap();
                    b.group(bossGroup, workerGroup)
                     .channel(NioServerSocketChannel.class)
                     .childHandler(new FileServerInitializer());

                    Channel channel = b.bind(fileServerConfig.getPort()).sync().channel();

                    LOG.info("file server start success. bind port: {}", fileServerConfig.getPort());
                    
                    // shut down your server.
                    channel.closeFuture().sync();
                    
                } catch(Exception e) {
                    LOG.error("file server start failed.", e);
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
