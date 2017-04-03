package org.reactor.monitoring.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.hibernate.Session;
import org.reactor.monitoring.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionPool {
    private static final Logger Logger = LoggerFactory.getLogger(SessionPool.class);

    /**
     * where the objects stored
     */
    ConcurrentHashMap<CompositeKey, Session> sessions;
    /**
     * where the expired info stored
     */
    ConcurrentHashMap<CompositeKey, Session> expirySessions;

    /**
     * a service clean up expired info
     */
    private ScheduledExecutorService scheduleService;

    /**
     * set 5 min as default expire interval
     */
    private int expiryInterval = 10;
    private int waitTime = 5;


    
    static class CompositeKey{
    	private final String id;
    	private final LocalDateTime time;
    	
		public CompositeKey(String id, LocalDateTime time) {
			super();
			this.id = id;
			this.time = time;
		}
		
		public String getId() {
			return id;
		}
		public LocalDateTime getTime() {
			return time;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CompositeKey other = (CompositeKey) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}

		
    	
		
    	
    }


    SessionPool()
    {
        init();
    }

    SessionPool(int expiryInterval,int waitTime)
    {
        this.expiryInterval = expiryInterval;
        this.waitTime = waitTime;
        init();
    }


    @SuppressWarnings("unchecked")
    private void init()
    {
        sessions = new ConcurrentHashMap<>();
        expirySessions = new ConcurrentHashMap<>();

        scheduleService = Executors.newScheduledThreadPool(1);

        scheduleService.scheduleAtFixedRate(new CheckOutOfDateSchedule(),
        		waitTime* 60, expiryInterval * 60, TimeUnit.SECONDS);
        Logger.info("####CheckService is start!");
           
    }

    public boolean clear()
    {
        if (sessions != null)
        	for(Entry<CompositeKey, Session> entry : sessions.entrySet())
            {
        		entry.getValue().clear();
        		entry.getValue().close();
            }

        if (expirySessions != null)
            expirySessions.clear();

        return true;
    }


  

	class CheckOutOfDateSchedule implements java.lang.Runnable {

		public void run() {
			check();
		}

		public void check() {
			try {
				for (Entry<CompositeKey, Session> entry : sessions.entrySet()) {
					Logger.info("************************** Going to move session : " + entry.getKey().getId()
							+ " to expiration map ****************** ");
					Session removedSession = removeSession(entry.getKey(), sessions, expiryInterval);
					if (removedSession != null) {
						expirySessions.put(entry.getKey(), removedSession);
						Logger.info("************************** moved session : " + entry.getKey().getId()
								+ " to expiration map ****************** ");
					}
				}

				for (Entry<CompositeKey, Session> entry : expirySessions.entrySet()) {
					Logger.info("************************** Going to close session : " + entry.getKey().getId()
							+ " from expiration map ****************** ");
					Session removedSession = removeSession(entry.getKey(), expirySessions, expiryInterval + waitTime);
					if (removedSession != null) {
						removedSession.clear();
						removedSession.close();
						Logger.info("************************** closed session : " + entry.getKey().getId()
								+ " from expiration map ****************** ");
					}
				}
			} catch (Throwable ex) {
				Logger.error("Exception while checking Sessions", ApplicationException.getStackTrace(ex));
			}
		}

	}
    
    private Session removeSession(final CompositeKey key, final ConcurrentHashMap<CompositeKey, Session> map, long timeGap){

    	Session removedSession = null;
		LocalDateTime now = LocalDateTime.now();
		Duration duration = Duration.between(key.getTime(), now);
		long minutes = duration.toMinutes();
		
		if (minutes >= timeGap) {
			Logger.info("************************** move session which lifetime is "+ minutes+" minutes ****************** ");
			removedSession = map.remove(key);
		}
		return removedSession;
    }
    
    public Session removeSessionByForce(final CompositeKey key){

    	Session removedSession = null;
		Logger.info("************************** move session forcefully  ****************** ");
		removedSession = sessions.remove(key);
		return removedSession;
    }
    
    void removeSession(final CompositeKey key){
    	Session removedSession = removeSession(key, sessions, expiryInterval);
    	if(removedSession != null){
    		expirySessions.put(key, removedSession);
    		Logger.info("************************** moved session : "+ key.getId()+" to expiration map ****************** ");
		}
    }


   
    public void destroy()
    {
        try
        {
            clear();

            if (scheduleService != null)
                scheduleService.shutdown();

            scheduleService = null;
        }
        catch(Exception ex)
        {
            Logger.error("Exception while shutting down scheduler",ex);
        }
    }

}
