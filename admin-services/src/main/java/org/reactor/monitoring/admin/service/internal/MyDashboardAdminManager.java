package org.reactor.monitoring.admin.service.internal;

import java.util.Map;

import org.hibernate.Session;
import org.reactor.monitoring.common.ManagerType;
import org.reactor.monitoring.exception.ApplicationException;
import org.reactor.monitoring.model.AbstractDAOManager;
import org.reactor.monitoring.model.internal.DAOManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyDashboardAdminManager implements AbstractAdminManager<Session>{
	private final static Logger LOGGER = LoggerFactory.getLogger(MyDashboardAdminManager.class);
	final Map<String,String> config;

	public MyDashboardAdminManager(final Map<String, String> config) {
		super();
		this.config = config;
	}



	@Override
	public Session getConnection(final ManagerType type) throws ApplicationException {
		AbstractDAOManager<Session> daoMgr = (AbstractDAOManager<Session>)DAOManagerFactory.getDAOManager(type);
		Session currentSession = null;
		try {
			currentSession = daoMgr.getConnection(type);
		} catch (ApplicationException ex) {
			LOGGER.error("Error while getting Session "+ApplicationException.getStackTrace(ex));
			throw ex;
		}catch (Throwable ex) {
			LOGGER.error("Error while getting Session "+ApplicationException.getStackTrace(ex));
			throw new ApplicationException(ex);
		}
		return currentSession;
	}



	@Override
	public AbstractDAOManager<Session> getAbstractDAOManager(ManagerType type) {
		AbstractDAOManager<Session> daoMgr = (AbstractDAOManager<Session>)DAOManagerFactory.getDAOManager(type);
		return daoMgr;
	}

}
