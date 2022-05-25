package com.searchservice.app.rest.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class RestApiErrorHandlingTest {

	//For Null Pointer Exception
	RestApiErrorHandling errorHandling1 = new RestApiErrorHandling(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(),
			HttpStatusCode.NULL_POINTER_EXCEPTION,HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());
	
	RestApiErrorHandling errorHandling2 = new RestApiErrorHandling(HttpStatusCode.NULL_POINTER_EXCEPTION,LocalDateTime.now());
	
	@Test
	void restApiErrorHandlingTest() {
		
		errorHandling1.equals(errorHandling2);
		//Setting New Exception Value
		errorHandling2.setMessage(HttpStatusCode.OPERATION_NOT_ALLOWED.getMessage());
		errorHandling2.setStatus(HttpStatusCode.OPERATION_NOT_ALLOWED);
		errorHandling2.setStatusCode(HttpStatusCode.OPERATION_NOT_ALLOWED.getCode());
		errorHandling2.setTimestamp(LocalDateTime.now());
		
		assertNotNull(errorHandling2.toString());
		assertEquals(errorHandling2.getStatusCode(), HttpStatusCode.OPERATION_NOT_ALLOWED.getCode());
		assertEquals(errorHandling2.getMessage(), HttpStatusCode.OPERATION_NOT_ALLOWED.getMessage());
		assertNotNull(errorHandling2.getTimestamp());
		assertEquals(HttpStatusCode.OPERATION_NOT_ALLOWED, errorHandling2.getStatus());
		assertNotEquals(errorHandling2.hashCode(), errorHandling1.hashCode());
		
	}
	
}
