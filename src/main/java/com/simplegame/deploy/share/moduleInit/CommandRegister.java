package com.simplegame.deploy.share.moduleInit;

import java.util.HashMap;
import java.util.Map;

public class CommandRegister {

    private static Map<String, Group> groupMap = new HashMap<String, Group>();

	/**
	 * 消息对应 Group
	 */
	private static Map<String, Group> cmdGroupMap = new HashMap<String, Group>();
	
	/**
	 * 消息对应 Module
	 */
	private static Map<String, String> cmdModuleMap = new HashMap<String, String>();

	public static void registerCmd(String group, String module, String command) {
		cmdGroupMap.put(command, groupMap.get(group));
		cmdModuleMap.put(command, module);
	}

	public static void registerCmd(String group, String module, String[] cmds) {
		if (null != cmds) {
			for (String command : cmds) {
				registerCmd(group, module, command);
			}
		}
	}

	public static String getCmdModule(String command) {
		return cmdModuleMap.get(command);
	}

	public static Group getCmdGroup(String command) {
		return cmdGroupMap.get(command);
	}
	
	public static boolean isModule(String command, String module) {
	    String m = cmdModuleMap.get(command);
	    if( null == m ) {
	        return false;
	    }
	    return m.equals(module);
	}

	static {
		groupMap.put(Group.IO.name           , Group.IO);
		groupMap.put(Group.BUS.name          , Group.BUS);
		groupMap.put(Group.SYSTEM.name       , Group.SYSTEM);
	}
}