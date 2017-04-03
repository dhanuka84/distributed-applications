package org.reactor.monitoring.common;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.reactor.monitoring.util.FileHandler;

public final class ApplicationConfig {
	public static volatile Properties CONFIG_PROPERTIES;
	
	public static void init() throws IOException{
		CONFIG_PROPERTIES = FileHandler.readEnvConfig();
		
	}
	
	public static final String HIBERNATE_SESSION_TIMEOUT = "sessionTimeOut";
	public static final String SESSION_CLOSE_WAIT_TIME = "sessionCloseWaitTime";
	public static final String SCHEDULER_INITIAL_DELAY = "schedulerInitialDelay";
	public static final String SCHEDULER_DELAY = "schedulerDelay";
	public static final String HIBERNATE_LOCAL_CONFIG_KEY = "hibernate.localconfiguration";
	public static final String KAFKA_LOCAL_CONFIG_KEY = "kafka.localconfiguration";
	public static final String HTTP_LOCAL_CONFIG_KEY = "http.localconfiguration";
	public static final String HIBERNATE_FACTORY_NAME = "HibernateFactory";
    public static final String HIBERNATE_SERVER_CONFIG_FILE = "hibernate.cfg.xml";
    public static final String ADMIN_PORT = "adminPort";
    public static final String WHITE_LIST_APPLICATIONS = "whitelistApps";
    public static final String ENABLE_KAFKA = "enableKafka";
    public static final String ENABLE_HTTP = "enableHTTP";
    
    public static Map<String,Object> convertToMap(final Properties properties, final Map<String,Object> map){
    	for (final String name: properties.stringPropertyNames())
    	    map.put(name, properties.getProperty(name));
    	
    	return map;
    }
    
    	

}
