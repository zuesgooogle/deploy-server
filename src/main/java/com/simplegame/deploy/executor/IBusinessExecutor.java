package com.simplegame.deploy.executor;

import java.util.Map;

/**
 *
 * @Author zeusgooogle@gmail.com
 * @sine   2015年5月6日 下午3:38:41
 *
 */

public interface IBusinessExecutor {

	/**
	 * execute runnable task
	 * 
	 * use route dispatch message
	 * 
	 * @param runnable
	 * @param route
	 */
    public void execute(Runnable runnable, Route route);

    /**
     * add executor by group
     * 
     * @param group
     * @param size
     */
    public void addExecutorGroup(String group, int size);

    public Map<String, Map<String, Integer>> getExecutors();
	
}
