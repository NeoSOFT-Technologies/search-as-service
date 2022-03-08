package com.searchservice.app.rest.errors;

public enum HttpStatusCode {
	
	INVALID_TABLE_NAME(101,"invalid table name"),
	
	INVALID_SKU_NAME(102,"Invalid SKU name"),
	
	INVALID_COLUMN_ATTRIBUTE(103,"invalid column attribute value"),
	
	NULL_COLUMN(104,"column is null,Provide atleast one Column"),
	
	INVALID_JSON_INPUT(105,"invalid json input or json format"),
	
	UNRECOGNIZED_FIELD(106,"check sequence of fields or field name"),
	
	UNDER_DELETION_PROCESS(107,"under deletion process");
	
		
	private int code;
	private String message;
	
	HttpStatusCode(int code, String message) {
		this.code=code;
		this.message=message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	

}
