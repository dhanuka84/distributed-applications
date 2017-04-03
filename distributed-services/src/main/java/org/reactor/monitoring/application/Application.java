package org.reactor.monitoring.application;

import java.io.Serializable;
import java.util.Map;

public abstract class Application<T> implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3046519687648017152L;
		
	public abstract void start(final Map<String,String> map);
	
	public abstract void stop();

}
