package org.reactor.monitoring.application.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.reactor.monitoring.admin.RestApplication;
import org.reactor.monitoring.application.Application;
import org.reactor.monitoring.application.CommonApplication;
import org.reactor.monitoring.application.IncidentCollector;
import org.reactor.monitoring.application.SyntheticCollector;
import org.reactor.monitoring.common.ApplicationConfig;
import org.reactor.monitoring.common.ManagerType;
import org.reactor.monitoring.common.ServiceType;
import org.reactor.monitoring.exception.ApplicationException;
import org.reactor.monitoring.model.AbstractDAOManager;
import org.reactor.monitoring.model.internal.DAOManagerFactory;
import org.reactor.monitoring.util.FileHandler;
import org.reactor.monitoring.util.HTTPClientUtil;
import org.reactor.monitoring.util.KafkaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Member {

	private final static Logger LOGGER = LoggerFactory.getLogger(Member.class);
	private static volatile Map<ManagerType, Application> applications = new HashMap<>();
	private static volatile boolean isStarted = false;

	private Member() {
	}

	public static void start(boolean startHazlecast) {
		try {
			isStarted = true;
			LOGGER.info("====================================== Member Starting =========================");
			ApplicationConfig.init();
			Properties props = ApplicationConfig.CONFIG_PROPERTIES;
			String enabledApps = props.getProperty(ApplicationConfig.WHITE_LIST_APPLICATIONS);
			Set<ManagerType> enabledTypes = new HashSet<>();
			
			if (enabledApps == null || StringUtils.isEmpty(enabledApps)) {
				LOGGER.warn(
						"====================================== Applications are not enabled =========================");
			}else{
				String[] allEnabledApps = enabledApps.split(",");
				for (String enabledApp : allEnabledApps) {
					ManagerType type = ManagerType.getManagerType(enabledApp.toUpperCase());
					if (type != null) {
						enabledTypes.add(type);
					}
				}
			}
			
			final Map<String, String> config = new HashMap<>();
			addAllConfigs(config);
			
			//enable kafka client
			String enabledKafka = props.getProperty(ApplicationConfig.ENABLE_KAFKA);
			boolean kafkaEnabled = StringUtils.isEmpty(enabledKafka) ? false : Boolean.parseBoolean(enabledKafka);
			
			if(kafkaEnabled){
				Properties kafkaProps = FileHandler.loadPropertiesFromFile(ApplicationConfig.CONFIG_PROPERTIES.getProperty(ApplicationConfig.KAFKA_LOCAL_CONFIG_KEY));
				KafkaUtil.init(kafkaProps);
			}
			
			//enable HTTP client
			String enableHTTP = props.getProperty(ApplicationConfig.ENABLE_HTTP);
			boolean httpEnabled = StringUtils.isEmpty(enabledKafka) ? false : Boolean.parseBoolean(enabledKafka);
			if(httpEnabled){
				HTTPClientUtil.init(config, ServiceType.DEFAULT);
			}
			
			// default dao manager
			AbstractDAOManager<Session> defaultDAOMGR = (AbstractDAOManager<Session>) DAOManagerFactory
					.getDAOManager(ManagerType.DEFAULT);
			defaultDAOMGR.start();
			
			//common application
			Application<CommonApplication> common = new CommonApplication();
			common.start(config);
			applications.put(ManagerType.DEFAULT, common);
			
			
			// synthetic
			if (enabledTypes.contains(ManagerType.SYNTHETIC)) {
				Application<SyntheticCollector> synthetic = new SyntheticCollector();
				synthetic.start(config);
				applications.put(ManagerType.SYNTHETIC, synthetic);

			}

			// incident
			if (enabledTypes.contains(ManagerType.INCIDENT)) {
				/*
				 * AbstractDAOManager<Session> incidentDAOMGR =
				 * (AbstractDAOManager<Session>) DAOManagerFactory
				 * .getDAOManager(ManagerType.DEFAULT); incidentDAOMGR.start();
				 */
				Application<IncidentCollector> incident = new IncidentCollector();
				incident.start(config);
				applications.put(ManagerType.INCIDENT, incident);
			}

			// start rest admin service
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						RestApplication.start(config);
					} catch (Throwable ex) {
						LOGGER.error(" ============================= Error while starting Member "
								+ ApplicationException.getStackTrace(ex));
					}
				}
			}).start();
			// RestApplication.start(syntheticConfig);

			LOGGER.info("====================================== Member Started =========================");
		} catch (Throwable ex) {
			LOGGER.error(" ============================= Error while starting Member "
					+ ApplicationException.getStackTrace(ex));
		}

	}

	public static void stop() {

		if (!isStarted) {
			return;
		}
		DAOManagerFactory.getAllManagers();
		for (Entry<ManagerType, AbstractDAOManager<? extends Serializable>> entry : DAOManagerFactory
				.getAllManagers()) {
			ManagerType type = entry.getKey();
			AbstractDAOManager<? extends Serializable> manager = entry.getValue();
			manager.stop(type.toString());
		}

	}

	public static Map<ManagerType, Application> getApplications() {
		return applications;
	}

	private static void addAllConfigs(final Map<String, String> map) {
		Properties props = ApplicationConfig.CONFIG_PROPERTIES;
		Set<Entry<Object, Object>> set = props.entrySet();
		for (Entry<Object, Object> entry : set) {
			map.put((String) entry.getKey(), (String)entry.getValue());
		}
	}

	public static void main(String[] args) throws Exception {
		new Thread(new Runnable() {

			@Override
			public void run() {
				start(false);

			}
		}).start();

	}
}
