package org.reactor.monitoring.util;

import static org.reactor.monitoring.common.ApplicationConfig.*;

import java.time.LocalDateTime;

import javax.persistence.PersistenceException;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.reactor.monitoring.common.ApplicationConfig;
import org.reactor.monitoring.util.SessionPool.CompositeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SessionUtil {
    private static final Logger Logger = LoggerFactory.getLogger(SessionUtil.class);

    private static final String TRANSACTION_PREFIX = "tx_";
    private static final String SESSION_PREFIX = "ses_";

    

    private HibernateConfiguration configuration;

    private static final ThreadLocal customerDbLink = new ThreadLocal();

    private SessionPool pool;

    private SessionUtil() {
    	new SessionPool();
    }

    public SessionUtil(int expiryInterval,int waitTime) {
        pool = new SessionPool(expiryInterval, waitTime);

        String localConfigurationFile = ApplicationConfig.CONFIG_PROPERTIES.getProperty(ApplicationConfig.HIBERNATE_LOCAL_CONFIG_KEY); //This enables you to use hibernate in offline mode
        if (localConfigurationFile != null) {
            configuration = new HibernateConfiguration(HIBERNATE_FACTORY_NAME, localConfigurationFile);
        } else {
            configuration = new HibernateConfiguration(HIBERNATE_FACTORY_NAME, null);
        }
    }

    
    public static String getSessionId(String postFix) {
        return SESSION_PREFIX + postFix;
    }

    private String getTxId(String postFix) {
        return TRANSACTION_PREFIX + postFix;
    }


    public String getCustomerDbLink() {
        return (String) customerDbLink.get();
    }

    public void setCustomerDbLink(String link) {
        customerDbLink.set(link);
    }

    public void initializateAllSessionFactory() throws PersistenceException {
    	Logger.info("HibernateSessionFactory initilizing a hibernate session factory: ");
        HibernateUtil.initSessionFactory(configuration.getHibernateFactoryName(), configuration.getHibernateServerConfigFile());
        
    }

    public void removeFromActivePool(final String postFix){
    	CompositeKey key = new CompositeKey(getSessionId(postFix),LocalDateTime.now());
    	pool.removeSessionByForce(key);
    }

    public Session getSession(final String postFix)throws PersistenceException 
    {
        Logger.info(new StringBuilder().append("Getting session for object ID: ").append(getSessionId(postFix)).toString());
        CompositeKey key = new CompositeKey(getSessionId(postFix),LocalDateTime.now());
        Session session = (Session) pool.sessions.get(key);
        if ((session != null && !session.isOpen())) {
        	pool.removeSession(key);
            session = null;
        }
        if (session != null) {
            if (!HibernateUtil.verifySessionFactoryWithSchema(session, configuration.getHibernateFactoryName())) {
                Logger.info(new StringBuilder().append("HIBERNATE SESSION IS NOT VALID WITH THE GIVEN CUSTOMER SCHEMA:").append(". So getting the correct one.").toString());
                pool.removeSession(key);
                session = null;
            }

        }
        if (session == null) {
            //Get the configuration for the class
            if (configuration != null) {
                session = HibernateUtil.getSession(configuration.getHibernateFactoryName(), configuration.getHibernateServerConfigFile());
                session.setFlushMode(FlushMode.MANUAL);
                pool.sessions.put(key, session);
            } else {
                throw new IllegalStateException("HibernateSessionFactory: No configuration available!");
            }

        }
        return session;
    }

	public SessionPool getPool() {
		return pool;
	}
	
	public void removeSession(final String name){
    	pool.removeSession(new CompositeKey(getSessionId(name), null));
    }
    
    
}