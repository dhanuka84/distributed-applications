/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reactor.monitoring.admin.service.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.reactor.monitoring.admin.service.AbstractFacade;
import org.reactor.monitoring.exception.ApplicationException;
import org.reactor.monitoring.model.entity.Test;

/**
 *
 * @author uranadh
 */

@Path("/admin.test")
public class TestFacadeREST extends AbstractFacade<Test> {


    public TestFacadeREST() {
        super(Test.class);
    }

    @POST
    @Path("create")
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(Test entity) {
    	return super.create(entity);
    }

    @PUT
    @Path("update/{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response edit(@PathParam("id") Long id, Test entity) {
    	return super.edit(entity, id);
    }

    @DELETE
    @Path("remove/{id}")
    public Response remove(@PathParam("id") Long id) {
    	return super.remove(id);
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response find(@PathParam("id") Long id) {
    	return super.find(id);
    }

    @GET
    @Path("all")
    @Override
    @Produces({MediaType.APPLICATION_JSON})
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
	protected void updateEntity(Test latest, Test old) {
		// TODO Auto-generated method stub
		
	}

    
}
