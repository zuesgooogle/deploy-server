package com.simplegame.deploy.bus.file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 
 * @Author zeusgooogle@gmail.com
 * @sine 2015年9月15日 下午3:37:58
 * 
 */
@Component
@ConfigurationProperties(prefix = "file.server")
public class FileServerConfig {

    /**
     * file server listen port
     */
    @Value(value = "${file.server.port}")
    private int port;

    /**
     * 文件存放路径
     */
    @Value(value = "${file.server.directory}")
    private String directory;
    
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

}
