package com.neosoft.app.rest.errors;

public enum HttpStatusCode {
	
	// With Custom Error Status Code
	INVALID_PRODUCT_NAME(101, "Invalid table name"),
	
	INVALID_COLUMN_ATTRIBUTE(103, "Invalid column attribute is provided"), 
	
	INVALID_JSON_INPUT(105, "Invalid JSON input or JSON format"),
	
	UNRECOGNIZED_FIELD(106, "Check sequence of fields or field name"),

	PRODUCT_NOT_FOUND(108, "Product does not exist"),

	PRODUCT_ALREADY_EXISTS(110, "Already exists"),
	
	INVALID_COLUMN_NAME(111, "Invalid column name provided"),
	
	WRONG_DATA_TYPE(112, "Wrong datatype selected for non multivalued field"),
	
	IO_EXCEPTION(113, "I/O exception occurred"), 
	
	JSON_PARSE_EXCEPTION(114, "JSON parse error occurred"), 
	
	INVALID_FIELD_VALUE(116, "Value for field : %s is not expected as : %s"), 
	
	APP_SERVER_ERROR(119, "This feature is currently down. Try again later"), 
	
	CONNECTION_REFUSED(120, "Connection is refused from the server"), 
	
	INVALID_CREDENTIALS(121, "Invalid credentials provided"), 
	
	// With Primitive Error Status Code
	BAD_REQUEST_EXCEPTION(400, "Bad Request Occuured"),
	
	NULL_POINTER_EXCEPTION(500, "Received Null response"),
	
	SERVER_UNAVAILABLE(503, "Unable to Connect To the Server"),
	
	OPERATION_NOT_ALLOWED(405, "Operation is Not Allowed"),
	
	UNAUTHORIZED_EXCEPTION(401, "Unauthorized To Perform Request"),
	
	FORBIDDEN_EXCEPTION(403, "Forbidden access attempted"),
	
	INTERNAL_SERVER_ERROR (500, "Internal Server Error Occured"),
	
	PROCESSING_NOT_COMPLETED (202, "Request cannot be Processed"),
	
	REQUEST_FORBIDEN(403, "Request Unable To Authorize"),
	
	NOT_ACCEPTABLE_ERROR (406, "Request Not accpetable");
	
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
