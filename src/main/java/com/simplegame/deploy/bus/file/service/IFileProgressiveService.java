package com.simplegame.deploy.bus.file.service;

import com.simplegame.deploy.bus.file.model.FileProgressiveEntity;

/**
 *
 * @Author zeusgooogle@gmail.com
 * @sine   2015年9月15日 下午4:59:06
 *
 */

public interface IFileProgressiveService {

    /**
     * 初始化一个下载文件进度
     * 
     * @param agentId
     * @param fileName
     * @param fileSize
     */
    public void initProgress(String agentId, String fileName, long fileSize);
    
    /**
     * 更新下载进度
     * 
     * @param agentId
     * @param progress
     */
    public void updateProgress(String agentId, long progress);
    
    public FileProgressiveEntity loadById(String agentId);
    
}
