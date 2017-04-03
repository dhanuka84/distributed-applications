package org.reactor.monitoring.application.task;

import java.util.Map;

import org.hibernate.Session;
import org.reactor.monitoring.application.Task;
import org.reactor.monitoring.common.ManagerType;
import org.reactor.monitoring.exception.ApplicationException;
import org.reactor.monitoring.model.AbstractDAOManager;
import org.reactor.monitoring.model.internal.DAOManagerFactory;
import org.reactor.monitoring.model.internal.MyDashboardDAOManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IncidentTask extends Task<IncidentTask>{
	
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = LoggerFactory.getLogger(IncidentTask.class);
	
	public IncidentTask(Map<String,String> map) {
		super(map);
	}


	@Override
	public void execute() {
		LOGGER.info(" Incident task execution started");
		AbstractDAOManager<Session> daoMgr = (AbstractDAOManager<Session>) DAOManagerFactory
				.getDAOManager(ManagerType.DEFAULT);
		try {
			Session currentSession = daoMgr.getConnection(ManagerType.DEFAULT);
			//PersistenceTest.list(currentSession);
			MyDashboardDAOManager.load(currentSession);
			LOGGER.info(" Incident task execution done");
		}  catch (ApplicationException ex) {
			LOGGER.error("Error while execution "+ApplicationException.getStackTrace(ex));
		}catch (Throwable ex) {
			LOGGER.error("Error while execution "+ApplicationException.getStackTrace(ex));
		}
		
	}

}
