package com.neosoft.app.domain.utils;

public enum PersistenceLabel {
	
	ENTITY_PRODUCT ("Product");
	
	
	private String message;
	
	PersistenceLabel(String label) {
		this.message=label;
	}

	public String getLabel() {
		return message;
	}

}
