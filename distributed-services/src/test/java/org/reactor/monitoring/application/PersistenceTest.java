package org.reactor.monitoring.application;

import static org.reactor.monitoring.util.CommonUtils.sleepMillis;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.service.ServiceRegistry;
import org.reactor.monitoring.application.Application;
import org.reactor.monitoring.application.internal.Member;
import org.reactor.monitoring.common.ManagerType;
import org.reactor.monitoring.exception.ApplicationException;
import org.reactor.monitoring.model.AbstractDAOManager;
import org.reactor.monitoring.model.entity.Product;
import org.reactor.monitoring.model.entity.Test;
import org.reactor.monitoring.model.internal.DAOManagerFactory;
import org.reactor.monitoring.model.internal.MyDashboardDAOManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PersistenceTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(PersistenceTest.class);
	
	private static SessionFactory sessionFactory;
	private static ServiceRegistry serviceRegistry;
	private static Session session = null;
	private static String DEFAULT_POSTFIX = "DEFAULT";
	
	private static Map<String,Long> productMap = MyDashboardDAOManager.getEntityMap();
	
	/*public static SessionFactory createSF(){
	        try {
	        	 Configuration configuration = new Configuration();
	        	    configuration.configure();
	        	    serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
	        	            configuration.getProperties()).build();
	        	    sessionFactory = configuration.buildSessionFactory(serviceRegistry);

	        } catch (Throwable ex) {
	            System.err.println("Failed to create sessionFactory object: " + ex.getMessage());
	            throw new ExceptionInInitializerError(ex);
	        }
	        
	        return sessionFactory;
	        
	        
	}*/
	
	public static void load(Session currentSession){
		System.out.println("======================== "+Thread.currentThread().getId());
		 for(Entry<String, Long> product : productMap.entrySet()){
         	Product entry = (Product) currentSession.get(Product.class, product.getValue());
         	System.out.print("Id: " + entry.getId());
             System.out.print(", productId " + entry.getProductId());
             System.out.print(", tests " + entry.getTests());
             for(Test test : entry.getTests()){
             	System.out.println(", testlocation: " + test.getTestlocations());
             }
         }
	}
	
	public static void list(Session currentSession){
		 List<Product> products = currentSession.createQuery("FROM Product").list();
         for (Product entry : products) {
         	productMap.put(entry.getProductId(), entry.getId());
            
             System.out.print("Id: " + entry.getId());
             System.out.print(", productId " + entry.getProductId());
             System.out.print(", tests " + entry.getTests());
             for(Test test : entry.getTests()){
             	System.out.println(", testlocation: " + test.getTestlocations());
             }
             
         }
	}
	
	private static class Loader implements Runnable{

		@Override
		public void run() {
			load(session);
			
		}
		
	}
	
	public static void main(String ...arg) throws ApplicationException{
		testCollectorSessionMgmt();
	}
	
	public static void testCollectorSessionMgmt() throws ApplicationException{
		Member.start(false);
		AbstractDAOManager<Session> manager = (AbstractDAOManager<Session>) DAOManagerFactory.getDAOManager(ManagerType.DEFAULT);
		MyDashboardDAOManager myManager = (MyDashboardDAOManager) manager;
		Session session = myManager.getConnection(ManagerType.DEFAULT);
		MyDashboardDAOManager.list(session);
	}
	
	public static void testHibernateSessionManagement() throws ApplicationException{
		//createSF();
		Member.start(false);
		AbstractDAOManager<Session> manager = (AbstractDAOManager<Session>) DAOManagerFactory.getDAOManager(ManagerType.DEFAULT);
		MyDashboardDAOManager myManager = (MyDashboardDAOManager) manager;
		Session session1 = myManager.getConnection(ManagerType.DEFAULT);
		Application synthetic = Member.getApplications().get(ManagerType.SYNTHETIC);
		/*SessionPool pool = new SessionPool();
		SessionUtil sessionUtil = new SessionUtil(pool);*/
		Scanner reader = new Scanner(System.in);
		
        //Session session1 = sessionUtil.getSession(DEFAULT_POSTFIX);
        session = session1;
        //Transaction tx1 = session1.beginTransaction();
        Transaction tx1 = null;
        Session session2 = session1;
        //Transaction tx2 = session2.beginTransaction();
        Transaction tx2 = tx1;
        Session currentSession = session1;
        Transaction currentTx = tx1;
        int current = 1;
        

        while (true) {
            sleepMillis(100);
            System.out.print("[" + current + ". session] Enter command: ");
            String command = reader.nextLine();
            if (command.equals("list")) {
                List<Product> products = currentSession.createQuery("FROM Product").list();
                for (Product entry : products) {
                	productMap.put(entry.getProductId(), entry.getId());
                   
                    System.out.print("Id: " + entry.getId());
                    System.out.print(", productId " + entry.getProductId());
                    System.out.print(", tests " + entry.getTests());
                    for(Test test : entry.getTests()){
                    	System.out.println(", testlocation: " + test.getTestlocations());
                    }
                    
                }
                
                load(currentSession);
                
               
            } else if (command.equals("add")) {
            }else if (command.equals("clear")) {
            	currentSession.clear();
            } else if (command.equals("load")) {
            	//load(currentSession);
            	Thread thread = new Thread(new Loader());
            	thread.start();
            	
            } else if (command.equals("delete")) {
               
            } else if (command.equals("close")) {
                currentTx.commit();
                currentSession.close();
            } else if (command.equals("open")) {
                if (current == 1) {
                    session1 = myManager.getSessionUtil().getSession(DEFAULT_POSTFIX);
                    //tx1 = session1.beginTransaction();
                    currentSession = session1;
                    currentTx = tx1;
                } else {
                    session2 = myManager.getSessionUtil().getSession(DEFAULT_POSTFIX);
                    //tx2 = session2.beginTransaction();
                    currentSession = session2;
                    currentTx = tx2;
                }
            } else if (command.equals("help")) {
                System.out.println("help         this menu");
                System.out.println("list         list all employees");
                System.out.println("add          add an employee");
                System.out.println("delete       delete and employee");
                System.out.println("open         open session and begin transaction");
                System.out.println("close        commit transaction and close session");
                System.out.println("change       change between two sessions");
                System.out.println("exit         exit");
            } else if (command.equals("exit")) {
                if (!tx1.wasCommitted()) {
                    tx1.commit();
                    session1.close();
                }
                if (!tx2.wasCommitted()) {
                    tx2.commit();
                    session2.close();
                }
                myManager.getSessionUtil().removeSession(DEFAULT_POSTFIX);
                break;
            } else if (command.equals("change")) {
                if (currentSession.equals(session1)) {
                    currentSession = session2;
                    currentTx = tx2;
                    current = 2;
                } else {
                    currentSession = session1;
                    currentTx = tx1;
                    current = 1;
                }
            } else {
                System.out.println("Command not found. Use help.");
            }
        }
	}
	
	

}
