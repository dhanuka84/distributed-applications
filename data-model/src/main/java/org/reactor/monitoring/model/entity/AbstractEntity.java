package org.reactor.monitoring.model.entity;

import java.io.Serializable;

public abstract class AbstractEntity<T> implements Serializable,Cloneable,Comparable<T> {
	
	@Override
	public int compareTo(T that) {
		
		return 1;
	}
	
	public Object clone(){
		return null;
	}

}
