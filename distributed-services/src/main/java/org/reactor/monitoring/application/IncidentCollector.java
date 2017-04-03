package org.reactor.monitoring.application;

import java.util.Map;

import org.reactor.monitoring.application.internal.ApplicationClusterUtil;
import org.reactor.monitoring.application.task.IncidentTask;
import org.reactor.monitoring.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IncidentCollector  extends ScheduleApplication<IncidentCollector>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6006433830521125552L;
	private final static Logger LOGGER = LoggerFactory.getLogger(IncidentCollector.class);
	
	@Override
	public void start(Map<String,String> map) {
		LOGGER.info("Incident collector started");
		super.start(map);
	}

	@Override
	protected void doWork(final Map<String,String> map) throws ApplicationException {		
		Task<IncidentTask> task = new IncidentTask(map);
		ApplicationClusterUtil.loadBalanceTasks(task,"default",false);
		
	}

}
