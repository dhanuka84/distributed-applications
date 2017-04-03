package org.reactor.monitoring.model.internal;

import static org.reactor.monitoring.common.ApplicationConfig.HIBERNATE_FACTORY_NAME;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.hibernate.Session;
import org.reactor.monitoring.common.ApplicationConfig;
import org.reactor.monitoring.common.EntityType;
import org.reactor.monitoring.common.ManagerType;
import org.reactor.monitoring.exception.ApplicationException;
import org.reactor.monitoring.model.AbstractDAOManager;
import org.reactor.monitoring.model.dao.AbstractDAO;
import org.reactor.monitoring.model.dao.LocationDAO;
import org.reactor.monitoring.model.dao.ProductDAO;
import org.reactor.monitoring.model.dao.TestDAO;
import org.reactor.monitoring.model.dao.TestLocationDAO;
import org.reactor.monitoring.model.entity.Product;
import org.reactor.monitoring.model.entity.Test;
import org.reactor.monitoring.util.HibernateUtil;
import org.reactor.monitoring.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyDashboardDAOManager implements AbstractDAOManager<Session>{
	
	private final static Logger LOGGER = LoggerFactory.getLogger(MyDashboardDAOManager.class);
	private static volatile Map<String,Long> ENTITY_MAP = new ConcurrentHashMap<>();
	private Properties config;
	private int sessionTimeOut = 5;
	private int waitTime = 5;
	
	private SessionUtil sessionUtil;
	private static final Map<EntityType,AbstractDAO> daoMap = new HashMap<>();
	
	private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
		
	@Override
	public Session getConnection(final ManagerType type) throws ApplicationException {
		if(sessionUtil == null){
			throw new ApplicationException("=========================Error getting Hibernate Session====================");
		}
		return sessionUtil.getSession(type.toString());
	}

	@Override
	public void start() {
		try {
			config = ApplicationConfig.CONFIG_PROPERTIES;
			
			if (config.getProperty(ApplicationConfig.HIBERNATE_SESSION_TIMEOUT) != null) {
				sessionTimeOut = Integer.parseInt(config.getProperty(ApplicationConfig.HIBERNATE_SESSION_TIMEOUT));
			}else{
				LOGGER.info("====================== session timeout not configured =======================");
			}

			if (config.getProperty(ApplicationConfig.SESSION_CLOSE_WAIT_TIME) != null) {
				waitTime = Integer.parseInt(config.getProperty(ApplicationConfig.SESSION_CLOSE_WAIT_TIME));
			}else{
				LOGGER.info("=========================== number Of Cleaners not configured ==========================");
			}
			
			sessionUtil = new SessionUtil(sessionTimeOut,waitTime);
		} catch (Exception ex) {
			LOGGER.error("Error occurred while starting application "+ex);
		}
		
		
	}
	

	public SessionUtil getSessionUtil() {
		return sessionUtil;
	}
	
	public static void load(Session currentSession){
	
		 for(Entry<String, Long> product : ENTITY_MAP.entrySet()){
         	Product entry = (Product) currentSession.get(Product.class, product.getValue());
             for(Test test : entry.getTests()){
            	 test.getTestlocations();
            	 test.getLocations();
             }
         }
	}
	
	public static void list(Session currentSession) throws ApplicationException{
		
		try{
			LOCK.writeLock().lock();
			 AbstractDAO dao = MyDashboardDAOManager.getDAO(EntityType.PRODUCT);
			 List<Product> products = dao.findAll(currentSession);
	         for (Product entry : products) {
	        	 ENTITY_MAP.put(entry.getProductId().intern(), entry.getId());
	             for(Test test : entry.getTests()){
	            	 test.getTestlocations();
	            	 test.getLocations();
	             }
	             
	         }
			
		}finally{
			LOCK.writeLock().unlock();
		}
		
	}
	
	public static Map<String,Long> getEntityMap(){
		
		try{
			LOCK.readLock().lock();
			return ENTITY_MAP;
		}finally{
			LOCK.readLock().unlock();
		}
		
	}

	@Override
	public void stop(final String name) {
		if(sessionUtil != null){
			sessionUtil.removeSession(name);
		}
		
	}
	
	public static AbstractDAO getDAO(final EntityType type) {
		AbstractDAO dao;
		if(daoMap.containsKey(type)){
			dao = daoMap.get(type);
		}else{
			dao = createDAO(type);
			daoMap.put(type, dao);
		}
		
		return dao;
	}
	
	private static AbstractDAO createDAO(final EntityType type) {
		AbstractDAO dao;
		
		switch (type) {
		case PRODUCT:
			dao = new ProductDAO();
			break;
		case LOCATION:
			dao = new LocationDAO();
			break;
		case TEST_LOCATION:
			dao = new TestLocationDAO();
			break;
		case TEST:
			dao = new TestDAO();
			break;

		default:
			dao = new ProductDAO();
			break;
		}
		
		return dao;

	}

	@Override
	public void removeFromActivePool(final String name) throws ApplicationException {
		if(sessionUtil == null){
			throw new ApplicationException("=========================Error getting Hibernate Session====================");
		}
		sessionUtil.removeFromActivePool(name);
		
	}

	@Override
	public void evict2ndLevelCache(String sessionFactoryName, Class entityClass, Serializable id) {
		HibernateUtil.evict2ndLevelCache(HIBERNATE_FACTORY_NAME, entityClass, id);
		
	}

	@Override
	public void evict2ndLevelCache(String sessionFactoryName) {
		HibernateUtil.evict2ndLevelCache(HIBERNATE_FACTORY_NAME);
		
	}

}
