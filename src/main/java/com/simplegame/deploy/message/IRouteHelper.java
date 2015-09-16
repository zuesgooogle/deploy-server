package com.simplegame.deploy.message;

import com.simplegame.core.message.Message;
import com.simplegame.deploy.executor.Route;

/**
 * @Author zeusgooogle@gmail.com
 * @sine   2015年5月10日 下午8:30:15 
 *
 */
public interface IRouteHelper {

	public Route getRoute(Message message);
	
}
