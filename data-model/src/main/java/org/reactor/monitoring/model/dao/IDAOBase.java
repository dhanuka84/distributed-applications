package org.reactor.monitoring.model.dao;

import java.io.Serializable;
import java.util.List;

import org.reactor.monitoring.exception.ApplicationException;

public interface IDAOBase<T,V> {

	public void persist(T session, V transientInstance) throws ApplicationException;

	public void attachDirty(T session,V instance) throws ApplicationException;

	public void attachClean(T session,V instance) throws ApplicationException;

	public void delete(T session,V persistentInstance) throws ApplicationException;

	public V merge(T session,V detachedInstance) throws ApplicationException;

	public V findId(T session,Serializable id) throws ApplicationException;
	
	public List<V> findAll(T session) throws ApplicationException;
	
	public V detach(T session,V detachedInstance);
	

}
