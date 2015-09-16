package com.simplegame.deploy.io.swap;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.simplegame.core.message.Message;
import com.simplegame.deploy.io.IoModuleInfo;
import com.simplegame.deploy.message.manager.SwapManager;
import com.simplegame.deploy.share.moduleInit.CommandRegister;

/**
 *
 * @Author zeusgooogle@gmail.com
 * @sine   2015年5月7日 下午6:11:17
 *
 */
@Component
public class IoMsgSender {

	@Resource
	private IoMsgDispatcher ioDispatcher;
	
	@Resource
	private SwapManager swapManager;
	
	public void swap(Message message) {
		String command = message.getCommand();
		if( CommandRegister.isModule(command, IoModuleInfo.MODULE_NAME) ) {
			ioDispatcher.in(message);
		}else {
			swapManager.swap(message);
		}
	}
	
}
