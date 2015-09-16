package com.simplegame.deploy.bus.swap;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.simplegame.core.message.Message;
import com.simplegame.core.message.Message.DestType;
import com.simplegame.core.message.Message.FromType;
import com.simplegame.deploy.message.IMsgDispatcher;
import com.simplegame.deploy.message.manager.SwapManager;

/**
 *
 * @Author zeusgooogle@gmail.com
 * @sine   2015年6月19日 下午6:35:59
 *
 */
@Component
public class BusMsgSender {

    @Resource
    private IMsgDispatcher busDispatcher;

    @Resource
    private SwapManager swapManager;
    
    public void send2OneBySessionId(String command, String sessionId, Object data) {
        Message message = new Message(command, data, FromType.BUS, DestType.CLIENT, null, sessionId);
        
        swapManager.swap(message);
    }
    
    public void send2One(String command, String roleId, Object data) {
        
        Message message = new Message(command, data, FromType.BUS, DestType.CLIENT, roleId);
        message.setRoute(1); // send to one player
        
        swapManager.swap(message);
    }
    
}
