package com.simplegame.deploy.executor.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.simplegame.deploy.executor.IBusinessExecutor;
import com.simplegame.deploy.executor.IRuleChecker;
import com.simplegame.deploy.executor.Route;

/**
 * @Author zeusgooogle@gmail.com
 * @sine 2015年5月9日 下午4:53:17
 * 
 */
public class BalanceBusinessExecutor implements IBusinessExecutor {

    private Logger LOG = LogManager.getLogger(getClass());

	private String name;
	private int loadSize = 11;
	private int loadCurror = 0;
	private int loadInterval = 60000;
	private int cleanInterval = 600000;
	private Map<String, Integer> config;

	private HashMap<String, ExecutorPoolGroup> groups = new HashMap<String, ExecutorPoolGroup>();

	private IRuleChecker ruleChecker;

	@PostConstruct
	public void init() {
		if (null == config) {
			throw new NullPointerException("executor config can't null.");
		}

		for (String group : config.keySet()) {
			int size = config.get(group);
			this.addExecutorGroup(group, size);
		}

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				int ratio = cleanInterval / loadInterval;
				int temp = 0;

				for (;;) {
					try {
						Thread.sleep(loadInterval);

						loadCurror = ++loadCurror % loadSize;
						for (ExecutorPoolGroup poolGroup : groups.values()) {
							poolGroup.setLoadCurror(loadCurror);
							continue;
						}

						if (++temp != ratio) {
							continue;
						}
						temp = 0;

						StringBuilder stringBuffer = new StringBuilder();
						for (ExecutorPoolGroup poolGroup : groups.values()) {
							poolGroup.cleanRuleExecutorRelation();
							stringBuffer.append(poolGroup.groupInfo());

							continue;
						}

						LOG.info(stringBuffer.toString());

					} catch (Exception e) {
						LOG.error("", e);
						continue;
					}
				}
			}
		}, "BalanceBusinessExecutor-Cleaner-" + name);

		thread.setDaemon(true);
		thread.start();

	}

	@Override
	public void execute(Runnable runnable, Route route) {
		ExecutorService executor = this.groups.get(route.getGroup()).getExecutor(route.getData());
		executor.execute(runnable);
	}

	@Override
	public void addExecutorGroup(String group, int size) {
		if (!groups.containsKey(group)) {
			groups.put(group, new ExecutorPoolGroup(group, size));
		}
	}

	@Override
	public Map<String, Map<String, Integer>> getExecutors() {
		HashMap<String, Map<String, Integer>> hashMap = new HashMap<String, Map<String, Integer>>();
		for (ExecutorPoolGroup executorPoolGroup : this.groups.values()) {
			hashMap.put(executorPoolGroup.getName(), executorPoolGroup.getGroupInfo());
		}
		return hashMap;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLoadSize() {
		return loadSize;
	}

	public void setLoadSize(int loadSize) {
		this.loadSize = loadSize;
	}

	public int getLoadCurror() {
		return loadCurror;
	}

	public void setLoadCurror(int loadCurror) {
		this.loadCurror = loadCurror;
	}

	public int getLoadInterval() {
		return loadInterval;
	}

	public void setLoadInterval(int loadInterval) {
		this.loadInterval = loadInterval;
	}

	public int getCleanInterval() {
		return cleanInterval;
	}

	public void setCleanInterval(int cleanInterval) {
		this.cleanInterval = cleanInterval;
	}

	public Map<String, Integer> getConfig() {
		return config;
	}

	public void setConfig(Map<String, Integer> config) {
		this.config = config;
	}

	public HashMap<String, ExecutorPoolGroup> getGroups() {
		return groups;
	}

	public void setGroups(HashMap<String, ExecutorPoolGroup> groups) {
		this.groups = groups;
	}

	public IRuleChecker getRuleChecker() {
		return ruleChecker;
	}

	public void setRuleChecker(IRuleChecker ruleChecker) {
		this.ruleChecker = ruleChecker;
	}

	private class ExecutorPoolGroup {
		private String name;
		private int size;
		private BalanceExecutor[] executors;
		private Map<Object, RuleExecutorRelation> ruleMap;

		public ExecutorPoolGroup(String name, int size) {
			this.ruleMap = new HashMap<Object, RuleExecutorRelation>();
			this.name = name;
			this.size = size;
			this.init();
		}

		private void init() {
			this.executors = new BalanceExecutor[this.size];
			for (int i = 0; i < this.size; ++i) {
				this.executors[i] = new BalanceExecutor(this.name + "-" + i);
			}
		}

		public String getName() {
			return this.name;
		}

		public void setLoadCurror(int n) {
			for (BalanceExecutor balanceExecutor : this.executors) {
				balanceExecutor.setCurror(n);
			}
		}

		public Map<String, Integer> getGroupInfo() {
			HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
			for (BalanceExecutor balanceExecutor : this.executors) {
				hashMap.put(balanceExecutor.getName(), balanceExecutor.getLoad());
			}
			return hashMap;
		}

		public String groupInfo() {
			StringBuilder stringBuilder = new StringBuilder("\n" + this.name);
			for (BalanceExecutor balanceExecutor : this.executors) {
				stringBuilder.append("\n\t").append(balanceExecutor.getName()).append("  ").append(balanceExecutor.getLoad());
			}
			return stringBuilder.toString();
		}

		public synchronized void cleanRuleExecutorRelation() {
			Iterator<RuleExecutorRelation> iterator = this.ruleMap.values().iterator();
			while (iterator.hasNext()) {
				RuleExecutorRelation ruleExecutorRelation = iterator.next();
				if (!ruleExecutorRelation.canClean())
					continue;
				iterator.remove();
			}
		}

		public synchronized ExecutorService getExecutor(String rule) {
			RuleExecutorRelation ruleExecutorRelation = this.ruleMap.get(rule);
			if (null == ruleExecutorRelation) {
				ruleExecutorRelation = new RuleExecutorRelation(rule, this.getLowestExecutor(), ruleChecker);
				this.ruleMap.put(rule, ruleExecutorRelation);
			}
			return ruleExecutorRelation.getBalanceExecutor().getExecutorService();
		}

		private BalanceExecutor getLowestExecutor() {
			BalanceExecutor balanceExecutor = null;
			int n = 0;
			for (BalanceExecutor balanceExecutor2 : this.executors) {
				int n2 = balanceExecutor2.getLoad();
				if (null == balanceExecutor) {
					balanceExecutor = balanceExecutor2;
					n = n2;
					continue;
				}
				if (n2 >= n)
					continue;
				balanceExecutor = balanceExecutor2;
				n = n2;
			}
			return balanceExecutor;
		}
	}

	private class BalanceExecutor {

		private String name;
		private int[] loads;
		private int curror;

		private ExecutorService executorService;

		public BalanceExecutor(String string) {
			this.loads = new int[BalanceBusinessExecutor.this.loadSize];
			this.curror = 0;
			this.executorService = Executors.newSingleThreadExecutor();
			this.name = string;
		}

		public synchronized void setCurror(int n) {
			this.curror = n;
			this.loads[this.curror] = 0;
		}

		public synchronized ExecutorService getExecutorService() {
			int[] arrn = this.loads;
			int n = this.curror;
			arrn[n] = arrn[n] + 1;
			return this.executorService;
		}

		public synchronized int getLoad() {
			int n = 0;
			for (int i = 0; i < BalanceBusinessExecutor.this.loadSize; ++i) {
				n += this.loads[i];
			}
			return n;
		}

		public String getName() {
			return this.name;
		}
	}

	private class RuleExecutorRelation {
		private String rule;
		private BalanceExecutor balanceExecutor;
		private IRuleChecker ruleChecker;

		public RuleExecutorRelation(String rule, BalanceExecutor balanceExecutor, IRuleChecker ruleChecker) {
			this.rule = rule;
			this.balanceExecutor = balanceExecutor;
			this.ruleChecker = ruleChecker;
		}

		public boolean canClean() {
			return this.ruleChecker.valid(this.rule);
		}

		public BalanceExecutor getBalanceExecutor() {
			return this.balanceExecutor;
		}
	}

}
