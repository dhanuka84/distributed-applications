package org.reactor.monitoring.admin.service.internal;

import org.reactor.monitoring.common.ManagerType;
import org.reactor.monitoring.exception.ApplicationException;
import org.reactor.monitoring.model.AbstractDAOManager;

public interface AbstractAdminManager<T> {

	T getConnection(final ManagerType type) throws ApplicationException;
	
	AbstractDAOManager getAbstractDAOManager(final ManagerType type);
}
