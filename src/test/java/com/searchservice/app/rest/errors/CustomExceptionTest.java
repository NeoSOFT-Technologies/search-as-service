package com.searchservice.app.rest.errors;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.searchservice.app.domain.utils.HttpStatusCode;

class CustomExceptionTest {

	//For BadRequest
	CustomException exception1 = new CustomException();
	CustomException exception2 = new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
			HttpStatusCode.BAD_REQUEST_EXCEPTION, HttpStatusCode.BAD_REQUEST_EXCEPTION.getMessage());
	
	@Test
	void customExceptionTest() {
		exception1.setExceptionCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
		exception1.setExceptionMessage(HttpStatusCode.BAD_REQUEST_EXCEPTION.getMessage());
		exception1.setStatus(HttpStatusCode.BAD_REQUEST_EXCEPTION);
		exception1.equals(exception2);
		exception1.canEqual(exception2);
		assertNotNull(exception1.toString());
		assertNotEquals(0, exception1.hashCode());
		assertNotNull(exception1.getExceptionMessage());
		assertEquals(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), exception1.getExceptionCode());
		assertEquals(HttpStatusCode.BAD_REQUEST_EXCEPTION.getMessage(), exception1.getExceptionMessage());
		assertEquals(HttpStatusCode.BAD_REQUEST_EXCEPTION, exception1.getStatus());
	}
}