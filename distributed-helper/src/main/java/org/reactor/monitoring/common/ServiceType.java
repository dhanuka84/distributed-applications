package org.reactor.monitoring.common;

import java.util.HashMap;
import java.util.Map;

public enum ServiceType {

	DEFAULT, SYNTHETIC, INCIDENT;

	private static final Map<String, ServiceType> typeMap = new HashMap<String, ServiceType>();

	static {
		for (ServiceType type : ServiceType.values()) {
			typeMap.put(type.toString(), type);
		}
	}

	public static ServiceType getServiceType(final String name) {
		return typeMap.get(name);
	}
}
