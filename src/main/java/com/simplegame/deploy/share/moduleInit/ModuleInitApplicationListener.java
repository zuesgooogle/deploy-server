package com.simplegame.deploy.share.moduleInit;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author zeusgooogle
 * @date 2015-05-07 下午08:19:46
 */
@Component
public class ModuleInitApplicationListener implements ApplicationListener<ApplicationEvent>, ApplicationContextAware {

	private Logger LOG = LogManager.getLogger(getClass());

	private ApplicationContext context;

	public ModuleInitApplicationListener() {
	}

	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		Map<String, ModuleInit> localMap = this.context.getBeansOfType(ModuleInit.class);
		if (null != localMap) {
			ModuleInit[] moduleInit = (ModuleInit[]) localMap.values().toArray(new ModuleInit[localMap.size()]);
			
			Arrays.sort(moduleInit, new Comparator<ModuleInit>() {
				public int compare(ModuleInit moduleInit1, ModuleInit moduleInit2) {
					Integer order1 = moduleInit1.getOrder();
					Integer order2 = moduleInit2.getOrder();

					return order1.compareTo(order2);
				}
			});

			for (ModuleInit module : moduleInit) {
				module.moduleInit();
				LOG.info("module initializing, order[{}], name: {}", module.getOrder(), module.getClass().getSimpleName());
			}
		}
	}

	public void setApplicationContext(ApplicationContext paramApplicationContext) throws BeansException {
		this.context = paramApplicationContext;
	}

}
