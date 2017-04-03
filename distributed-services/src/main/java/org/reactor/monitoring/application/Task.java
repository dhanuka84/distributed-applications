package org.reactor.monitoring.application;

import java.util.Map;

public abstract class Task<T>{

	protected final transient Map<String,String> map;

	public Task(final Map<String,String> map) {
		this.map = map;
	}
	
	public Map<String, String> getConfig(){
		return map;
	}
	
	protected abstract void execute();

}
