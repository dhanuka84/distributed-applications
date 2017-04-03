package org.reactor.monitoring.admin;

import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.reactor.monitoring.admin.service.internal.AdminManagerFactory;
import org.reactor.monitoring.admin.service.internal.MyDashboardAdminManager;
import org.reactor.monitoring.admin.service.rest.ProductFacadeREST;
import org.reactor.monitoring.common.ApplicationConfig;
import org.reactor.monitoring.common.ManagerType;

public class RestApplication {
	
    public static void start(final Map<String,String> config) throws Exception {
    	
    	MyDashboardAdminManager manager = (MyDashboardAdminManager) AdminManagerFactory.getAdminManager(ManagerType.DEFAULT,config);
    	
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        //TODO make this configurable
        int adminPort = 8080;
        if (config.get(ApplicationConfig.ADMIN_PORT) != null) {
        	adminPort = Integer.parseInt((String) config.get(ApplicationConfig.ADMIN_PORT));
		}
        
        Server jettyServer = new Server(adminPort);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(
             org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        /*jerseyServlet.setInitParameter(
           "jersey.config.server.provider.classnames",
           ProductFacadeREST.class.getCanonicalName());*/
        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.packages",
                ProductFacadeREST.class.getPackage().getName());
        

        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }
    
    
}
