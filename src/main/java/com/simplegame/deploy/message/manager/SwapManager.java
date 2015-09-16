package com.simplegame.deploy.message.manager;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.simplegame.core.message.Message;
import com.simplegame.core.message.Message.DestType;
import com.simplegame.core.message.Message.FromType;
import com.simplegame.deploy.message.IMsgDispatcher;
import com.simplegame.deploy.share.moduleInit.CommandRegister;
import com.simplegame.deploy.share.moduleInit.Group;

/**
 * @Author zeusgooogle@gmail.com
 * @sine 2015年5月10日 下午8:41:22
 * 
 */
@Component
public class SwapManager {

    private Logger LOG = LogManager.getLogger(getClass());

    @Resource
    private IMsgDispatcher ioDispatcher;

    @Resource
    private IMsgDispatcher busDispatcher;
    
    public void swap(Message message) {
        FromType from = message.getFrom();
        switch (from) {
        case CLIENT:
            swapClientMsg(message);
            break;
        case BUS:
            componentMsgSwap(message);
            break;
        default:
            componentMsgSwap(message);
            break;
        }
    }

    public void swapClientMsg(Message message) {
        String command = message.getCommand();

        /**
         * 获取 command 所属分组
         */
        Group group = CommandRegister.getCmdGroup(command);
        
        //消息路由，不同routeHelper 意义不相同，根据具体上下文判断
        message.setRoute(group.value);
        
        LOG.debug("swap msg command: {}, dest group: {}", command, group);

        switch (group) {
        case BUS:
            busDispatcher.in(message);
            break;
        default:
            LOG.error("message cmd: {} not find group.", command);
            break;
        }
    }

    public void swapPublicMsg(Message message) {
        componentMsgSwap(message);
    }

    private void componentMsgSwap(Message message) {
        DestType dest = message.getDest();
        switch (dest) {
        case CLIENT:
            toClient(message);
            break;
        case BUS:
            this.busDispatcher.in(message);
            break;
        case PUBLIC:
            break;
        case STAGE:
            break;
        case INOUT:
        case INNER_SYSTEM:
            break;
        default:
            break;
        }
    }

    private void toClient(Message message) {
        ioDispatcher.in(message);
    }

}
