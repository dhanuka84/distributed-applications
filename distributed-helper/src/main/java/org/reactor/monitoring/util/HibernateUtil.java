package org.reactor.monitoring.util;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.PersistenceException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic Hibernate helper class, handles SessionFactory, Session and
 * Transaction.
 * <p/>
 * Uses a static initializer for the initial SessionFactory creation and holds
 * Session and Transactions in thread local variables. All exceptions are
 * wrapped in an unchecked InfrastructureException.
 */
public final class HibernateUtil {
	private static final Logger LOG = LoggerFactory.getLogger(HibernateUtil.class);
	private volatile static Map<String, SessionFactory> factoryMap = new ConcurrentHashMap<>();

	private static String getFactoryKey(String sessionFactoryName) {

		return sessionFactoryName;
	}

	/**
	 * Returns the SessionFactory according to jndi name supplied.
	 *
	 * @param sessionFactoryName
	 *            This name needs to be according to what's defined in
	 *            Hibernate.cfg.xml
	 * @return SessionFactory
	 */
	private static SessionFactory getInstance(String sessionFactoryName, String configFileName)
			throws PersistenceException {
		if (factoryMap.get(getFactoryKey(sessionFactoryName)) != null) {
			return (SessionFactory) factoryMap.get(getFactoryKey(sessionFactoryName));
		} else {
			synchronized (HibernateUtil.class) {
				if (factoryMap.get(getFactoryKey(sessionFactoryName)) == null) {
					SessionFactory sessionFactory;
					ServiceRegistry serviceRegistry;
					try {

						Configuration configuration = new Configuration();
						if (configFileName != null) {
							File file = new File(configFileName);
							configuration.configure(file);
						} else {
							configuration.configure();
						}

						serviceRegistry = new StandardServiceRegistryBuilder()
								.applySettings(configuration.getProperties()).build();
						sessionFactory = configuration.buildSessionFactory(serviceRegistry);

					} catch (Throwable ex) {
						System.err.println("Failed to create sessionFactory object: " + ex.getMessage());
						throw new ExceptionInInitializerError(ex);
					}

					factoryMap.put(getFactoryKey(sessionFactoryName), sessionFactory);
				}

			}

		}
		return (SessionFactory) factoryMap.get(getFactoryKey(sessionFactoryName));

	}

	/**
	 * Returns the Session for associated factory
	 *
	 * @param sessionFactoryName
	 *            This name needs to be according to what's defined in
	 *            Hibernate.cfg.xml
	 * @return SessionFactory
	 */
	public static Session getSession(String sessionFactoryName, String configFileName) throws PersistenceException {
		try {
			LOG.debug("Opening Session!");
			return getInstance(sessionFactoryName, configFileName).openSession();
		} catch (HibernateException ex) {
			throw new PersistenceException(ex);
		}
	}

	public static void closeSession(Object sessionObj) throws PersistenceException {
		try {
			Session session = (Session) sessionObj;
			if (session != null && session.isOpen()) {
				LOG.debug("Closing Session!");
				session.close();
			}
		} catch (HibernateException ex) {
			throw new PersistenceException(ex);
		}
	}

	public static boolean verifySessionFactoryWithSchema(Session session, String sessionFactoryName)
			throws PersistenceException {
		try {
			if (session != null && session.isOpen()) {
				String factoryKey = getFactoryKey(sessionFactoryName);
				if (factoryMap.get(factoryKey) == null || (factoryMap.get(factoryKey) != null
						&& !session.getSessionFactory().equals(factoryMap.get(factoryKey)))) {
					return false;
				}
			}
		} catch (HibernateException ex) {
			throw new PersistenceException(ex);
		}
		return true;
	}

	public static boolean initSessionFactory(String sessionFactoryName, String configFileName)
			throws PersistenceException {
		try {
			LOG.debug("Building Session Factory!");
			return getInstance(sessionFactoryName, configFileName) != null;
		} catch (HibernateException ex) {
			throw new PersistenceException(ex);
		}
	}
	
	
	public static void evict2ndLevelCache(String sessionFactoryName, Class entityClass, Serializable id) {
	    try {
	    	SessionFactory sessionFactory = (SessionFactory) factoryMap.get(getFactoryKey(sessionFactoryName));
	    	LOG.info("Evicting Entity from 2nd level cache: " + entityClass.getCanonicalName());
            sessionFactory.getCache().evictEntity(entityClass, id);
	    } catch (Exception e) {
	    	LOG.error("SessionController", "evict2ndLevelCache", "Error evicting 2nd level hibernate cache entities: ", e);
	    }
	}
	
	
	public static void evict2ndLevelCache(String sessionFactoryName) {
	    try {
	    	SessionFactory sessionFactory = (SessionFactory) factoryMap.get(getFactoryKey(sessionFactoryName));
	    	LOG.info("Evicting all data from 2nd level cache: " );
            sessionFactory.getCache().evictAllRegions();
	    } catch (Exception e) {
	    	LOG.error("SessionController", "evict2ndLevelCache", "Error evicting 2nd level hibernate cache entities: ", e);
	    }
	}

}
