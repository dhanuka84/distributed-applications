package org.reactor.monitoring.admin.service.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.reactor.monitoring.admin.service.AbstractFacade;
import org.reactor.monitoring.model.entity.Product;

@Path("/admin")
public class RestAdmin  extends AbstractFacade<Product>{
	
	public RestAdmin() {
		super(Product.class);
	}

	@DELETE
    @Path("cache/evict")
    @Produces({MediaType.APPLICATION_JSON})
    public Response removeAllCache() {
		return super.evict2ndLevelCache();
    }
	
	@DELETE
    @Path("cache/update")
    @Produces({MediaType.APPLICATION_JSON})
    public Response updateEntityMap() {
		return super.updateEntityMap();
    }

	@Override
	protected void updateEntity(Product latest, Product old) {
		
		
	}

}
