package org.reactor.monitoring.common;

import java.io.Serializable;

import com.fasterxml.jackson.databind.JsonNode;

public class JSONElement implements Serializable{
	
	private JsonNode element;
	
	public JSONElement() {
		super();
	}

	public JSONElement(JsonNode element) {
		super();
		this.element = element;
	}

	public JsonNode getElement() {
		return element;
	}

	public void setElement(JsonNode element) {
		this.element = element;
	}
		

}
