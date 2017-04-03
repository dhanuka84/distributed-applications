package org.reactor.monitoring.admin.service;

import static org.reactor.monitoring.common.ApplicationConfig.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.reactor.monitoring.admin.service.internal.AdminManagerFactory;
import org.reactor.monitoring.admin.service.internal.MyDashboardAdminManager;
import org.reactor.monitoring.common.EntityType;
import org.reactor.monitoring.common.ManagerType;
import org.reactor.monitoring.exception.ApplicationException;
import org.reactor.monitoring.model.entity.AbstractEntity;
import org.reactor.monitoring.model.internal.MyDashboardDAOManager;
import org.reactor.monitoring.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author uranadh
 */
public abstract class AbstractFacade<T extends Cloneable> {
	private static final Logger log = LoggerFactory.getLogger(AbstractFacade.class);

	private Class<T> entityClass;
	private EntityType type;
	private MyDashboardAdminManager manager;
	private MyDashboardDAOManager daoManager;

	protected AbstractFacade(Class<T> entityClass) {
		this.entityClass = entityClass;
		this.type = EntityType.getEntityType(this.entityClass.getSimpleName());
		this.manager = (MyDashboardAdminManager) AdminManagerFactory.getAdminManager(ManagerType.DEFAULT);
		this.daoManager = (MyDashboardDAOManager) this.manager.getAbstractDAOManager(ManagerType.DEFAULT);
	}

	protected Response create(T entity) {
		Response response = null;

		try {
			MyDashboardDAOManager.getDAO(type).persist(daoManager.getConnection(ManagerType.DEFAULT), entity);
			daoManager.removeFromActivePool(ManagerType.DEFAULT.toString());
			response = Response.status(Response.Status.OK).entity(type.getClassName() + " created ").build();
		} catch (Exception ex) {
			log.error(ApplicationException.getStackTrace(ex));
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(JSONUtils.convertToString(ApplicationException.getStackTrace(ex))).build();

		}
		return response;
	}

	protected Response edit(T entity, Serializable id) {
		Response response = null;

		try {
			AbstractEntity<T> entityObj = (AbstractEntity<T>) MyDashboardDAOManager.getDAO(type)
					.findId(daoManager.getConnection(ManagerType.DEFAULT), id);
			
			if (entityObj == null) {
				response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(" No record found ").build();
			} else {
				updateEntity(entity, (T) entityObj);
				MyDashboardDAOManager.getDAO(type).attachDirty(daoManager.getConnection(ManagerType.DEFAULT), entityObj);
				daoManager.evict2ndLevelCache(HIBERNATE_FACTORY_NAME, entityClass, id);	
				daoManager.removeFromActivePool(ManagerType.DEFAULT.toString());

				response = Response.status(Response.Status.OK).entity(type.getClassName() + " updated ").build();
			}
			
			
		} catch (Exception ex) {
			log.error(ApplicationException.getStackTrace(ex));
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(JSONUtils.convertToString(ApplicationException.getStackTrace(ex))).build();
		}
		return response;
	}

	protected Response remove(Serializable id) {
		Response response = null;

		try {
			AbstractEntity<T> entity = (AbstractEntity<T>) MyDashboardDAOManager.getDAO(type)
					.findId(daoManager.getConnection(ManagerType.DEFAULT), id);
			if (entity == null) {
				response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(" No record found ").build();
			} else {
				MyDashboardDAOManager.getDAO(type).delete(daoManager.getConnection(ManagerType.DEFAULT),entity);
				daoManager.evict2ndLevelCache(HIBERNATE_FACTORY_NAME, entityClass, id);
				daoManager.removeFromActivePool(ManagerType.DEFAULT.toString());
				
				response = Response.status(Response.Status.OK).entity(type.getClassName() + " deleted for Id " + id)
						.build();
			}

		} catch (Exception ex) {
			log.error(ApplicationException.getStackTrace(ex));
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(JSONUtils.convertToString(ApplicationException.getStackTrace(ex))).build();
		}

		return response;
	}

	protected Response find(Serializable id) {

		Response response = null;
		try {
			AbstractEntity<T> entity = (AbstractEntity<T>) MyDashboardDAOManager.getDAO(type)
					.findId(daoManager.getConnection(ManagerType.DEFAULT), id);
			response = Response.status(Response.Status.OK).entity(entity.clone()).build();
		} catch (Exception ex) {
			log.error(ApplicationException.getStackTrace(ex));
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(JSONUtils.convertToString(ApplicationException.getStackTrace(ex))).build();
		}

		return response;
	}

	protected Response findAll() {

		Response response = null;

		List<AbstractEntity<T>> dto = new ArrayList<>();
		try {

			List<AbstractEntity<T>> list = MyDashboardDAOManager.getDAO(type).findAll(daoManager.getConnection(ManagerType.DEFAULT));
			for (AbstractEntity<T> entity : list) {
				dto.add((AbstractEntity<T>) entity.clone());
			}

			String jsonString = JSONUtils.convertToString(dto);
			response = Response.status(Response.Status.OK).entity(jsonString).build();
		} catch (Exception ex) {
			log.error(ApplicationException.getStackTrace(ex));
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(JSONUtils.convertToString(ApplicationException.getStackTrace(ex))).build();
		}

		return response;

	}
	
	protected Response evict2ndLevelCache(Serializable id){
		Response response = null;
		try{
			daoManager.evict2ndLevelCache(HIBERNATE_FACTORY_NAME, entityClass, id);
			daoManager.removeFromActivePool(ManagerType.DEFAULT.toString());
		}catch(Throwable ex){
			log.error(ApplicationException.getStackTrace(ex));
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(JSONUtils.convertToString(ApplicationException.getStackTrace(ex))).build();
		}		
		response = Response.status(Response.Status.OK).entity(entityClass.getCanonicalName()).build();
		return response;
	}
	
	protected Response evict2ndLevelCache(){
		Response response = null;
		try{
			daoManager.evict2ndLevelCache(HIBERNATE_FACTORY_NAME);
			daoManager.removeFromActivePool(ManagerType.DEFAULT.toString());
		}catch(Throwable ex){
			log.error(ApplicationException.getStackTrace(ex));
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(JSONUtils.convertToString(ApplicationException.getStackTrace(ex))).build();
		}		
		response = Response.status(Response.Status.OK).entity(" All cache cleared").build();
		return response;
	}
	
	protected Response updateEntityMap(){
		Response response = null;
		try {
			MyDashboardDAOManager.list(daoManager.getConnection(ManagerType.DEFAULT));
		} catch(Throwable ex){
			log.error(ApplicationException.getStackTrace(ex));
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(JSONUtils.convertToString(ApplicationException.getStackTrace(ex))).build();
		}
		
		response = Response.status(Response.Status.OK).entity(" All cache updated").build();
		return response;
	}
	
	protected abstract void updateEntity(T latest,T old);

}
