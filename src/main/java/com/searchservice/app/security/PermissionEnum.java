package com.searchservice.app.security;

public enum PermissionEnum {
	
	/**
	 * All available permissions with respect to API access, at any time for any active user.
	 */
	
	READ("read"),
	WRITE("write"),
	UPDATE("update"),
	DELETE("delte");
	
	private String permissionLabel;
	
	PermissionEnum(String permissionLabel) {
		this.permissionLabel = permissionLabel;
	}
	
	public String getPermissionLabel() {
		return permissionLabel;
	}

}
