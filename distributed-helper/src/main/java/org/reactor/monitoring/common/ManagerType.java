package org.reactor.monitoring.common;

import java.util.HashMap;
import java.util.Map;

public enum ManagerType{
	DEFAULT,SYNTHETIC,INCIDENT;
	

	private static final Map<String, ManagerType> typeMap = new HashMap<String, ManagerType>();

	static {
		for (ManagerType type : ManagerType.values()) {
			typeMap.put(type.toString(), type);
		}
	}

	public static ManagerType getManagerType(final String name) {
		return typeMap.get(name);
	}
}