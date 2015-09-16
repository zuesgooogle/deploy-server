package com.simplegame.deploy.executor;

/**
 * 
 * @Author zeusgooogle@gmail.com
 * @sine 2015年5月6日 下午3:36:19
 * 
 */

public class Route {

	public String group;

	public String data;

	public Route() {

	}
	
	public Route(String group) {
		this.group = group;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
