package com.searchservice.app.domain.utils;

public enum HttpStatusCode {
	
	INVALID_TABLE_NAME(101,"invalid table name"),
	
	INVALID_SKU_NAME(102,"Invalid SKU name"),
	
	INVALID_COLUMN_ATTRIBUTE(103,"invalid column attribute value"),
	
	NULL_COLUMN(104,"column is null,Provide atleast one Column"),
	
	INVALID_JSON_INPUT(105,"invalid json input or json format"),
	
	UNRECOGNIZED_FIELD(106,"check sequence of fields or field name"),
	
	UNDER_DELETION_PROCESS(107,"under deletion process"),
	
	TABLE_NOT_FOUND(108, "does not exist"),
	
	TABLE_NOT_UNDER_DELETION(109, "not under deletion"),
	
	BAD_REQUEST_EXCEPTION(400,"Bad Request Occuured"),
	
	NULL_POINTER_EXCEPTION(404,"Received Null response"),
	
	SERVER_UNAVAILABLE(503,"Unable to Connect To the Server"),
	
	OPERATION_NOT_ALLOWED(405,"Operation is Not Allowed"),
	
	UNAUTHORIZED_EXCEPTION(401, "Unauthorized To Perform Request"),
	
	INTERNAL_SERVER_ERROR (500, "Internal Server Error Occured"),
	
	PROCESSING_NOT_COMPLETED (202, "Request cannot be Processed"),
	
	NOT_ACCEPTABLE_ERROR (406, "Request Not accpetable"),
	
	TABLE_ALREADY_EXISTS(110, "already exists"),
	
	INVALID_COLUMN_NAME(111, "invalid column name provided"),
	
	WRONG_DATA_TYPE(112, "wrong datatype selected for non multivalued field");
	
	private int code;
	private String message;
	
	HttpStatusCode(int code, String message) {
		this.code=code;
		this.message=message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	
	

}
