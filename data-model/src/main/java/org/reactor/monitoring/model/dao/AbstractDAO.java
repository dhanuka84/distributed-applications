package org.reactor.monitoring.model.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.reactor.monitoring.common.EntityType;
import org.reactor.monitoring.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDAO<T, V> implements IDAOBase<Session,V>{
	
	private static final Logger log = LoggerFactory.getLogger(AbstractDAO.class);

	private final Class<V> entityClass;
	private EntityType type;

	public AbstractDAO() {
		this.entityClass = (Class<V>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[1];
		this.type = EntityType.getEntityType(this.entityClass.getSimpleName());
	}


	@Override
	public void persist(Session session, V instance) throws ApplicationException {
		log.debug("persisting V instance");
		try {
			/*Transaction tx = session.beginTransaction();*/
			session.saveOrUpdate(instance);
			session.flush();
			/*tx.commit();*/
			log.info("persist successful");
		} catch (Exception re) {
			log.error("persist failed", re);
			throw new ApplicationException("persist failed",re);
		}
	}

	@Override
	public void attachDirty(Session session,V instance) throws ApplicationException {
		log.debug("attaching dirty V instance");
		try {
			session.update(instance);
			session.flush();
			log.info("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw new ApplicationException("attach failed",re);
		}
	}

	@Override
	public void attachClean(Session session,V instance) throws ApplicationException {
		log.debug("attaching clean V instance");
		try {
			session.lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (Exception re) {
			log.error("attach failed", re);
			throw new ApplicationException("attach failed",re);
		}
	}

	@Override
	public void delete(Session session,V persistentInstance) throws ApplicationException {
		log.debug("deleting V instance");
		try {
			session.delete(persistentInstance);
			session.flush();
			log.info("delete successful");
		} catch (Exception re) {
			log.error("delete failed", re);
			throw new ApplicationException("delete failed",re);
		}
	}

	@Override
	public V merge(Session session,V detachedInstance) throws ApplicationException {
		log.debug("merging V instance");
		try {
			V result = (V) session.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (Exception re) {
			log.error("merge failed", re);
			throw new ApplicationException("merge failed",re);
		}
	}


	@Override
	public V findId(Session session,Serializable id) throws ApplicationException {
		log.debug("getting V instance with id: " + id);
		try {
			V instance = (V) session.get(entityClass, id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw new ApplicationException("get failed",re);
		}
	}

	@Override
	public List<V> findAll(Session session) throws ApplicationException{
		List<V> list = session.createQuery("FROM "+type.getClassName()).list();
		if(list == null || list.isEmpty()){
			list = Collections.EMPTY_LIST;
		}
		
		return list;
	}
	
	public V detach(Session session,V detachedInstance){
		session.evict(detachedInstance);
		return detachedInstance;
	}

}
