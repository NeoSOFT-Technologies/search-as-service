package com.searchservice.app.domain.dto;

public class ResponseMessages {
	private ResponseMessages() {}
    public static final String BAD_REQUEST_MSG = "Bad Request call made. Unable to perform the request";
    public static final String DEFAULT_EXCEPTION_MSG = "REST call could not be performed";
    public static final String SUCCESS_RESPONSE_MSG = "REST call processed successfully";
    public static final String NULL_RESPONSE_MESSAGE = "Received Null response";
	public static final String TABLE_DELETE_INITIALIZE_ERROR_MSG = "Error While Initializing Deletion For Table: {}"; 
	public static final String TABLE_DELETE_UNDO_ERROR_MSG = "Undo Table Delete Failed , Invalid CLient ID Provided";
}
