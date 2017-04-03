package org.reactor.monitoring.application.internal;

import java.util.Set;

import org.reactor.monitoring.application.Application;
import org.reactor.monitoring.application.WorkerType;

public final class Work<T> {
	
	private final WorkerType type;
	private final String name;
	private final Set<Application<T>> tasks;
	
	public Work(WorkerType type, String name, Set<Application<T>> tasks) {
		super();
		this.type = type;
		this.name = name;
		this.tasks = tasks;
	}

	public WorkerType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public Set<Application<T>> getTasks() {
		return tasks;
	}
	
	
	
	

}
