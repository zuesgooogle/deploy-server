package com.simplegame.deploy.bus.file.model;

import com.simplegame.core.data.IEntity;

/**
 *
 * @Author zeusgooogle@gmail.com
 * @sine   2015年9月15日 下午4:53:21
 *
 */

public class FileProgressiveEntity implements IEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String agentId;
    
    private String fileName;
    
    private long fileSize;

    private long progress;
    
    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    /**
     * 更新进度
     * 
     * @param progress
     */
    public void updateProgress(long progress) {
        this.progress = progress;
    }
    
    @Override
    public String getPirmaryKeyName() {
        return "agentId";
    }

    @Override
    public Object getPrimaryKeyValue() {
        return getAgentId();
    }

    @Override
    public IEntity copy() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return "FileProgressiveEntity [agentId=" + agentId + ", fileName=" + fileName + ", fileSize=" + fileSize + ", progress=" + progress + "]";
    }
    
    

}
