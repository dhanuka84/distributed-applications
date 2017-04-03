package org.reactor.monitoring.application;

import java.util.HashMap;
import java.util.Map;

public enum WorkerType {
	
	SYNTHETIC_COLLECTOR;
	
	private static final Map<String,WorkerType> typeMap = new HashMap<String,WorkerType>();
	
	static {
		for(WorkerType type : WorkerType.values()){
			typeMap.put(type.toString(), type);
		}
	}
	
	public static WorkerType getApplicationType(final String name){
		return typeMap.get(name);
	}

}
