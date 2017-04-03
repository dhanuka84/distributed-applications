package org.reactor.monitoring.application;

import java.util.Map;

import org.reactor.monitoring.application.internal.ApplicationClusterUtil;
import org.reactor.monitoring.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonApplication extends ScheduleApplication<CommonApplication> {
	
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = LoggerFactory.getLogger(CommonApplication.class);
	
	@Override
	public void start(final Map<String, String> map) {
		LOGGER.info("Common application started");
		//start hazelcast instance
		ApplicationClusterUtil.listParentEntities(map);
		super.start(map);
	}

	@Override
	protected void doWork(Map<String, String> map) throws ApplicationException {
		ApplicationClusterUtil.loadBalanceTasks(null,"default",true);
		
	}

}
