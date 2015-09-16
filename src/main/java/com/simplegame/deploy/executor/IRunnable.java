package com.simplegame.deploy.executor;

import com.simplegame.core.message.Message;

/**
 *
 * @Author zeusgooogle@gmail.com
 * @sine   2015年5月6日 下午3:56:15
 *
 */

public interface IRunnable {

	public Runnable getRunnable(Message message);
	
}
