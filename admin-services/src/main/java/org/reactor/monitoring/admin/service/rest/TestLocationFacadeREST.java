/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reactor.monitoring.admin.service.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

import org.reactor.monitoring.admin.service.AbstractFacade;
import org.reactor.monitoring.exception.ApplicationException;
import org.reactor.monitoring.model.entity.TestLocation;

/**
 *
 * @author uranadh
 */

@Path("/admin.testlocation")
public class TestLocationFacadeREST extends AbstractFacade<TestLocation> {


	private TestLocation.Id getPrimaryKey(PathSegment pathSegment) {
		/*
		 * pathSemgent represents a URI path segment and any associated matrix
		 * parameters. URI path part is supposed to be in form of
		 * 'somePath;testId=testIdValue;locationId=locationIdValue'. Here
		 * 'somePath' is a result of getPath() method invocation and it is
		 * ignored in the following code. Matrix parameters are used as field
		 * names to build a primary key instance.
		 */
		TestLocation.Id key = new TestLocation.Id();
		javax.ws.rs.core.MultivaluedMap<String, String> map = pathSegment.getMatrixParameters();
		java.util.List<String> testId = map.get("testId");
		if (testId != null && !testId.isEmpty()) {
			key.setTestId(new java.lang.Long(testId.get(0)));
		}
		java.util.List<String> locationId = map.get("locationId");
		if (locationId != null && !locationId.isEmpty()) {
			key.setLocationId(new java.lang.Long(locationId.get(0)));
		}
		return key;
	}

	public TestLocationFacadeREST() {
		super(TestLocation.class);
	}

	@PUT
	@Path("update/{id}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({MediaType.APPLICATION_JSON})
	//http://localhost:8080/admin.testlocation/update/1;locationId=1;testId=1
	public Response edit(@PathParam("id") PathSegment id, TestLocation entity) {
		TestLocation.Id key = getPrimaryKey(id);
		return super.edit(entity,key);
	}

	@DELETE
	@Path("remove/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response remove(@PathParam("id") PathSegment id) {
		TestLocation.Id key = getPrimaryKey(id);
		return super.remove(key);
	}

    @POST
    @Path("create")
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response create(TestLocation entity) {
    	return super.create(entity);
    }


    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response find(@PathParam("id") PathSegment id) {
    	TestLocation.Id key = getPrimaryKey(id);
    	return super.find(key);
    }

    @GET
    @Path("all")
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findAll() {
    	return super.findAll();
    }
    
    @DELETE
    @Path("cache/remove/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response evict2ndLevelCache(@PathParam("id") Long id){
    	return super.evict2ndLevelCache(id);
    }

	@Override
	protected void updateEntity(TestLocation latest, TestLocation old) {
		old.setAvailabilityCritical(latest.getAvailabilityCritical() == old.getAvailabilityCritical()
				? old.getAvailabilityCritical() : latest.getAvailabilityCritical());
		old.setAvailabilityWarning(latest.getAvailabilityWarning() == old.getAvailabilityWarning()
				? old.getAvailabilityWarning() : latest.getAvailabilityWarning());
		old.setResponseCritical(latest.getResponseCritical() == old.getResponseCritical()
				? old.getResponseCritical() : latest.getResponseCritical());
		old.setResponseWarning(latest.getResponseWarning() == old.getResponseWarning()
				? old.getResponseWarning() : latest.getResponseWarning());

	}

}
