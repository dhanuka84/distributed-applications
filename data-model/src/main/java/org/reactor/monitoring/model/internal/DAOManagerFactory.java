package org.reactor.monitoring.model.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.reactor.monitoring.common.ManagerType;
import org.reactor.monitoring.model.AbstractDAOManager;

import java.util.Set;

public class DAOManagerFactory {
	
	private static final Map<ManagerType,AbstractDAOManager<? extends Serializable>> daoMap = new HashMap<>();
	
	public static AbstractDAOManager<? extends Object> getDAOManager(final ManagerType type) {
		AbstractDAOManager<? extends Serializable> manager;
		
		switch (type) {
		case SYNTHETIC:
			if(daoMap.containsKey(type)){
				manager = daoMap.get(type);				
			}else{
				manager = new MyDashboardDAOManager();
				daoMap.put(type, manager);
			}			
			break;

		default:
			if(daoMap.containsKey(type)){
				manager = daoMap.get(type);				
			}else{
				manager = new MyDashboardDAOManager();
				daoMap.put(type, manager);
			}
			break;
		}
		
		return manager;
	}
	
	public static Set<Entry<ManagerType, AbstractDAOManager<? extends Serializable>>> getAllManagers(){
		return daoMap.entrySet();
	}

}
