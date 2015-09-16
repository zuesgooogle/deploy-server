package com.simplegame.deploy.executor.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.simplegame.deploy.executor.IBusinessExecutor;
import com.simplegame.deploy.executor.Route;

/**
 * 
 * 控制底层入口IO 进入到系统，在系统无法处理更多消息时，直接丢弃消息
 * 
 * @Author zeusgooogle@gmail.com
 * @sine 2015年5月6日 下午4:30:35
 * 
 */
public class TimeoutBusinessExecutor implements IBusinessExecutor {

    private Logger LOG = LogManager.getLogger(getClass());

	private HashMap<String, ExecutorPoolGroup> groups = new HashMap<String, ExecutorPoolGroup>();

	private String name;

	/**
	 * task alive time(ms)
	 */
	private long aliveTime = 3000L;

	private int triggerSize = 300000;

	/**
	 * clean task interval(ms)
	 */
	private long cleanInterval = 120000L;

	private Map<String, Integer> config;

	@PostConstruct
	public void init() {
		if( null == config ) {
			throw new NullPointerException("executor config can't null.");
		}
		
		Iterator<String> keyIter = config.keySet().iterator();
        while (keyIter.hasNext()) {
            String key = keyIter.next();
            int size = config.get(key).intValue();
            addExecutorGroup(key, size);
        }
        
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    for (;;) {
                        Thread.sleep(cleanInterval);
                        
                        Iterator<ExecutorPoolGroup> iterator = groups.values().iterator();
                        while (iterator.hasNext()) {
                            ExecutorPoolGroup executorPoolGroup = iterator.next();
                            executorPoolGroup.clean();
                        }
                    }
                } catch (Exception e) {
                    LOG.error("", e);
                }
            }
        }, "TimeBusinessExecutor-Cleaner-" + name);
        thread.setDaemon(true);
        thread.start();
	}

	@Override
	public void execute(Runnable runnable, Route route) {
		groups.get(route.getGroup()).getExecutor(route.getData()).execute(runnable);
	}

	@Override
	public void addExecutorGroup(String group, int size) {
		if (!this.groups.containsKey(group)) {
            this.groups.put(group, new ExecutorPoolGroup(group, size));
        }
	}

	@Override
	public Map<String, Map<String, Integer>> getExecutors() {
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getAliveTime() {
		return aliveTime;
	}

	public void setAliveTime(long aliveTime) {
		this.aliveTime = aliveTime;
	}

	public int getTriggerSize() {
		return triggerSize;
	}

	public void setTriggerSize(int triggerSize) {
		this.triggerSize = triggerSize;
	}

	public long getCleanInterval() {
		return cleanInterval;
	}

	public void setCleanInterval(long cleanInterval) {
		this.cleanInterval = cleanInterval;
	}

	public Map<String, Integer> getConfig() {
		return config;
	}

	public void setConfig(Map<String, Integer> config) {
		this.config = config;
	}

	private class ExecutorPoolGroup {

		private int size;

		private TimeoutExecutor[] executors;

		private int assignCount;

		private Map<String, TimeoutExecutor> ruleMap = new HashMap<String, TimeoutExecutor>();

		private String groupName;

		public ExecutorPoolGroup(String groupName, int size) {
			this.groupName = groupName;
			this.size = size;
			init();
		}

		private void init() {
			this.executors = new TimeoutExecutor[this.size];
			for (int i = 0; i < this.size; i++) {
				this.executors[i] = new TimeoutExecutor(this.groupName + "-" + i);
			}
		}

		public synchronized TimeoutExecutor getExecutor(String route) {
			TimeoutExecutor timeoutExecutor = this.ruleMap.get(route);
			if (null == timeoutExecutor) {
				timeoutExecutor = this.executors[(this.assignCount++ % this.size)];
				this.ruleMap.put(route, timeoutExecutor);
			}
			return timeoutExecutor;
		}

		public void clean() {
			for (TimeoutExecutor timeoutExecutor : this.executors) {
				timeoutExecutor.clean();
			}
		}
	}

	private class TimeoutExecutor {

		private LinkedBlockingQueue<TimeoutTask> tasks = new LinkedBlockingQueue<TimeoutTask>();

		private String name;

		public TimeoutExecutor(String name) {
			this.name = name;

			(new Thread() {
				public void run() {
					try {
						for (;;) {
							TimeoutTask timeoutTask = tasks.take();
							timeoutTask.run();
						}
					} catch (Exception exception) {
						LOG.error("", exception);
					}
				}
			}).start();
		}

		public void execute(Runnable runnable) {
			this.tasks.add(new TimeoutTask(runnable));
		}

		public void clean() {
			int size = this.tasks.size();
			LOG.info("TimeExecutor[{}] task current size: {}, trigger size: {}", getName(), size, triggerSize);

			if (size > triggerSize) {
				long now = System.currentTimeMillis();
				int count = 0;

				Iterator<TimeoutTask> iterator = this.tasks.iterator();
				while (iterator.hasNext()) {
					TimeoutTask timeoutTask = iterator.next();
					if (timeoutTask.timeout()) {
						this.tasks.remove(timeoutTask);
						count++;
					}
				}

				LOG.info("TimeExecutor[{}] clean finished. use time: {}, clean count: {}", getName(), (System.currentTimeMillis() - now), count);
			}
		}

		private String getName() {
			return this.name;
		}
	}

	private class TimeoutTask {

		private long startTime;

		private Runnable runnable;

		public TimeoutTask(Runnable runnable) {
			this.runnable = runnable;
			this.startTime = System.currentTimeMillis();
		}

		public boolean timeout() {
			return System.currentTimeMillis() - this.startTime > aliveTime;
		}

		public void run() {
			this.runnable.run();
		}

	}
}
