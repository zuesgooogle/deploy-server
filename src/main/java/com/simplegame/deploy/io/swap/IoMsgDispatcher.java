package com.simplegame.deploy.io.swap;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.simplegame.core.action.front.IActionFrontend;
import com.simplegame.core.message.Message;
import com.simplegame.deploy.executor.IBusinessExecutor;
import com.simplegame.deploy.executor.IRunnable;
import com.simplegame.deploy.executor.Route;
import com.simplegame.deploy.executor.impl.RunnableImpl;
import com.simplegame.deploy.io.message.IoMessage;
import com.simplegame.deploy.message.IMsgDispatcher;

/**
 *
 * @Author zeusgooogle@gmail.com
 * @sine   2015年5月7日 下午5:59:05
 *
 */
@Component(value = "ioDispatcher")
public class IoMsgDispatcher implements IMsgDispatcher {

	private ThreadLocal<IRunnable> runnableLocal = new ThreadLocal<IRunnable>();
	
	private IoRouteHelper routeHelper = new IoRouteHelper();
	
	@Resource(name="ioExecutor")
	private IBusinessExecutor businessExexutor;
	
	@Resource(name="actionFrontend")
	private IActionFrontend actionFrontend;
	
	@Override
	public void in(Message message) {
		IoMessage msg = new IoMessage(message);
		
		Runnable localRunnable = getRunnable().getRunnable(msg);
		Route route = this.routeHelper.getRoute(message);
		
		this.businessExexutor.execute(localRunnable, route);
	}

	private IRunnable getRunnable() {
		IRunnable runnalbe = this.runnableLocal.get();
		if (null == runnalbe) {
			runnalbe = new RunnableImpl(this.actionFrontend);
			this.runnableLocal.set(runnalbe);
		}
		return runnalbe;
	}
}
