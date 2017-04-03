package org.reactor.monitoring.model;

import java.io.Serializable;

import org.reactor.monitoring.common.ManagerType;
import org.reactor.monitoring.exception.ApplicationException;

public interface AbstractDAOManager<T extends Serializable> {

	public T getConnection(final ManagerType type) throws ApplicationException;
	
	public void start();
	
	public void stop(final String name);
	
	public void removeFromActivePool(final String name) throws ApplicationException;
	
	public void evict2ndLevelCache(String sessionFactoryName, Class entityClass, Serializable id);
	
	public void evict2ndLevelCache(String sessionFactoryName);
}
