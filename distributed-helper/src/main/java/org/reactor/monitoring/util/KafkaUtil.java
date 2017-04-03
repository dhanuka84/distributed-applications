package org.reactor.monitoring.util;

import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.reactor.monitoring.common.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaUtil {
	private final static Logger LOGGER = LoggerFactory.getLogger(KafkaUtil.class);
	
    /* This variable denotes the Kafka server client */ 
    private static KafkaProducer client; 
    
    public static void init(final Properties config) {  
        client = new KafkaProducer(config); 
    } 
    
    //public static 


}
