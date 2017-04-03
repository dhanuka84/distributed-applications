package org.reactor.monitoring.common;

import java.util.HashMap;
import java.util.Map;

public enum EntityType {
	LOCATION("Location"), PRODUCT("Product"), TEST("Test"), TEST_LOCATION("TestLocation");

	private String className;

	EntityType(String name) {
		this.className = name;
	}

	public String getClassName() {
		return className;
	}

	private static final Map<String, EntityType> typeMap = new HashMap<String, EntityType>();

	static {
		for (EntityType type : EntityType.values()) {
			typeMap.put(type.className, type);
		}
	}

	public static EntityType getEntityType(final String name) {
		return typeMap.get(name);
	}
}
