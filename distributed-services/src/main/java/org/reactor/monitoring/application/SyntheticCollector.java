package org.reactor.monitoring.application;

import java.util.Map;

import org.reactor.monitoring.application.internal.ApplicationClusterUtil;
import org.reactor.monitoring.application.task.SyntheticTask;
import org.reactor.monitoring.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyntheticCollector extends ScheduleApplication<SyntheticCollector> {

	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = LoggerFactory.getLogger(SyntheticCollector.class);

	@Override
	public void start(final Map<String,String> map) {
		LOGGER.info("Synthetic collector started");
		//start hazelcast instance
		super.start(map);
	}

	@Override
	protected void doWork(final Map<String,String> map) throws ApplicationException {
		Task<SyntheticTask> task = new SyntheticTask(map);
		ApplicationClusterUtil.loadBalanceTasks(task,"default",false);

	}

}
