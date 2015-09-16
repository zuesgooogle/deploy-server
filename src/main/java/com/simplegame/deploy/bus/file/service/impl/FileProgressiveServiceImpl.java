package com.simplegame.deploy.bus.file.service.impl;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.simplegame.core.container.DataContainer;
import com.simplegame.deploy.bus.file.FileModuleInfo;
import com.simplegame.deploy.bus.file.model.FileProgressiveEntity;
import com.simplegame.deploy.bus.file.service.IFileProgressiveService;

/**
 *
 * @Author zeusgooogle@gmail.com
 * @sine   2015年9月15日 下午5:04:35
 *
 */
@Service
public class FileProgressiveServiceImpl implements IFileProgressiveService {

    private Logger LOG = LogManager.getLogger(getClass());
    
    @Resource
    private DataContainer dataContainer;
    
    @Override
    public void initProgress(String agentId, String fileName, long fileSize) {
        FileProgressiveEntity progressive = dataContainer.getData(FileModuleInfo.MODULE_NAME, agentId);
        if( null == progressive ) {
            progressive = new FileProgressiveEntity(); 
        }
        
        progressive.setAgentId(agentId);
        progressive.setFileName(fileName);
        progressive.setFileSize(fileSize);
        progressive.setProgress(0);
        
        dataContainer.putData(FileModuleInfo.MODULE_NAME, agentId, progressive);
    }

    @Override
    public void updateProgress(String agentId, long progress) {
        FileProgressiveEntity progressive = dataContainer.getData(FileModuleInfo.MODULE_NAME, agentId);
        if( null == progressive ) {
            LOG.error("agentId: {}, data not found. please initProgress first.", agentId);
            return;
        }
        
        progressive.updateProgress(progress);
        
        LOG.info(progressive.toString());
    }

    @Override
    public FileProgressiveEntity loadById(String agentId) {
        return dataContainer.getData(FileModuleInfo.MODULE_NAME, agentId);
    }

}
