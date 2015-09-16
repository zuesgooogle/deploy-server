package com.simplegame.deploy.share.moduleInit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.simplegame.core.event.IEventHandler;
import com.simplegame.core.event.IEventService;

/**
 * 
 * @Author zeusgooogle@gmail.com
 * @sine 2015年5月7日 上午11:21:44
 * 
 */

public abstract class ModuleInit {

    @Resource
    private IEventService eventService;
    
    public ModuleInit() {

    }

    @PostConstruct
    public void init() {
        //ModuleInfo moduleInfo = getModuleInfo();

        InCmd inCmd = getInCmd();
        if (null != inCmd) {
            CommandRegister.registerCmd(inCmd.inCmdGroup, inCmd.inCmdModule, inCmd.inCmds);
        }

        IEventHandler[] eventHandlers = getEventHandlers();
        if (null != eventHandlers) {
            for (IEventHandler handler : eventHandlers) {
                getEventService().subscribe(handler.getEventType(), getOrder(), handler);
            }
        }
    }

    public IEventService getEventService() {
        return eventService;
    }

    /**
     * 模块加载顺序
     * 
     * @return
     */
    public int getOrder() {
        return 200;
    }

    protected ModuleInfo getModuleInfo() {
        return null;
    }

    protected IEventHandler[] getEventHandlers() {
        return null;
    }

    protected abstract InCmd getInCmd();

    public void moduleInit() {
    }

    protected class InCmd {
        private String inCmdModule;
        private String inCmdGroup;
        private String[] inCmds;

        public InCmd(String module, String group, String[] cmds) {
            this.inCmdModule = module;
            this.inCmdGroup = group;
            this.inCmds = cmds;
        }
    }

    protected class ModuleInfo {

        private String moduleName;

        private String moduleNameAlias;

        public ModuleInfo(String moduleName, String moduleNameAlias) {
            this.moduleName = moduleName;
            this.moduleNameAlias = moduleNameAlias;
        }

        public String getModuleName() {
            return this.moduleName;
        }

        public String getModuleNameAlias() {
            return this.moduleNameAlias;
        }
    }

}
