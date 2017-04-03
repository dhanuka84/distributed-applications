package org.reactor.monitoring.admin.service.internal;

import java.io.Serializable;
import java.util.Map;

import org.reactor.monitoring.common.ManagerType;

public final class AdminManagerFactory{
	
	private static volatile AbstractAdminManager<? extends Object> adminManager;
	
	public static AbstractAdminManager<? extends Serializable> getAdminManager(final ManagerType type, final Map<String, String> config) {
		AbstractAdminManager<? extends Serializable> manager;
		
		switch (type) {
		case SYNTHETIC:
			manager = new MyDashboardAdminManager(config);
			adminManager = manager;	
			break;

		default:
			manager = new MyDashboardAdminManager(config);
			adminManager = manager;	
			break;
		}
		
			
		return manager;
	}
	
	public static AbstractAdminManager<? extends Object> getAdminManager(final ManagerType type) {
		return adminManager;
	}
	

}
