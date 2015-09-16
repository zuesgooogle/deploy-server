package com.simplegame.deploy.bus.agent.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.simplegame.core.action.annotation.ActionMapping;
import com.simplegame.core.action.annotation.ActionWorker;
import com.simplegame.core.message.Message;
import com.simplegame.deploy.bus.agent.command.AgentCommands;

/**
 *
 * @Author zeusgooogle@gmail.com
 * @sine   2015年9月16日 下午4:45:53
 *
 */
@ActionWorker
public class AgentAction {

    private Logger LOG = LogManager.getLogger(getClass());
    
    @ActionMapping(mapping = AgentCommands.REGISTER)
    public void register(Message message) {
        LOG.info("agent register...");
    }
    
    @ActionMapping(mapping = AgentCommands.LOGIN)
    public void login(Message message) {
        LOG.info("agent login...");
    }
    
}
