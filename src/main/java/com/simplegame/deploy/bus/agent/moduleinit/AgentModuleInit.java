package com.simplegame.deploy.bus.agent.moduleinit;

import org.springframework.stereotype.Component;

import com.simplegame.deploy.bus.agent.AgentModuleInfo;
import com.simplegame.deploy.bus.agent.command.AgentCommands;
import com.simplegame.deploy.share.moduleInit.Group;
import com.simplegame.deploy.share.moduleInit.ModuleInit;

/**
 *
 * @Author zeusgooogle@gmail.com
 * @sine   2015年9月16日 下午4:38:55
 *
 */
@Component
public class AgentModuleInit extends ModuleInit {

    @Override
    protected InCmd getInCmd() {
        return new InCmd(AgentModuleInfo.MODULE_NAME, Group.BUS.name, new String[]{AgentCommands.REGISTER, AgentCommands.LOGIN});
    }
    
}
