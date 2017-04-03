package org.reactor.monitoring.application;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.reactor.monitoring.common.ApplicationConfig;
import org.reactor.monitoring.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ScheduleApplication<T> extends Application<T>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -295368218346761059L;
	private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleApplication.class);
	protected final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
	
	@Override
	public void start(final Map<String,String> map) {
		
		int initialDelay = 30;
		int delay = 120;
		
		if (map.get(ApplicationConfig.SCHEDULER_INITIAL_DELAY) != null) {
			initialDelay = Integer.parseInt((String) map.get(ApplicationConfig.SCHEDULER_INITIAL_DELAY));
		}else{
			LOGGER.info("====================== session timeout not configured =======================");
		}

		if (map.get(ApplicationConfig.SCHEDULER_DELAY) != null) {
			delay = Integer.parseInt((String) map.get(ApplicationConfig.SCHEDULER_DELAY));
		}else{
			LOGGER.info("=========================== number Of Cleaners not configured ==========================");
		}
		
		final Runnable worker = new Runnable() {
			public void run() {
				try{
					doWork(map);
				}catch(Throwable ex){
					LOGGER.error("Error while execution "+ApplicationException.getStackTrace(ex));
				}
			}
		};
		final ScheduledFuture<?> workerHandler = scheduler.scheduleWithFixedDelay(worker, initialDelay, delay, SECONDS);
	}
	

	@Override
	public void stop() {
		if (!scheduler.isShutdown()) {
			scheduler.shutdown();
		}

	}
	
	protected abstract void doWork(final Map<String,String> map) throws ApplicationException;
}
