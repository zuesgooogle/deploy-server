package com.simplegame.deploy.io.moduleinit;

import org.springframework.stereotype.Component;

import com.simplegame.deploy.io.IoModuleInfo;
import com.simplegame.deploy.io.command.IoCommands;
import com.simplegame.deploy.share.moduleInit.Group;
import com.simplegame.deploy.share.moduleInit.ModuleInit;

/**
 *
 * @Author zeusgooogle@gmail.com
 * @sine   2015年5月7日 下午6:44:55
 *
 */
@Component
public class IoModuleInit extends ModuleInit {

	@Override
	protected InCmd getInCmd() {
		return new InCmd(IoModuleInfo.MODULE_NAME, Group.IO.name, new String[] { IoCommands.PING});
	}

}
