package org.ofbiz.query.model;

import java.io.Serializable;

public class Alias implements Serializable {
	private  String entityAlias;
	private  String name;
	public String getEntityAlias() {
		return entityAlias;
	}
	public void setEntityAlias(String entityAlias) {
		this.entityAlias = entityAlias;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
