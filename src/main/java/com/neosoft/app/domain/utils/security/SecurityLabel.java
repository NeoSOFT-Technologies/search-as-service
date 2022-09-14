package com.neosoft.app.domain.utils.security;

public enum SecurityLabel {

	KEYWORD_ROLES("roles"), 
	KEYWORD_ROLE_ADMIN("ROLE_ADMIN"), 
	KEYWORD_ROLE_USER("ROLE_USER"), 
	KEYWORD_ROLE_MANAGER("ROLE_MANAGER"), 
	
	TOKEN_ALGO_SECRET ("neo-coe-secret"), 
	
	URL_LOGIN ("/api/v1/login"), 		// For Login API
	URL_SIGNIN ("/api/v1/signin"), 		// For CustomAuthenticationFilter
	URL_APP_USER ("/api/v1/app-user"), 
	URL_PRODUCT ("/api/v1/product");
	
	
	private String message;
	
	SecurityLabel(String label) {
		this.message=label;
	}

	public String getLabel() {
		return message;
	}

}
