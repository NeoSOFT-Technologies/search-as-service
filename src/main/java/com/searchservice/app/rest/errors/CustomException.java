package com.searchservice.app.rest.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CustomException extends RuntimeException{

		private static final long serialVersionUID = 1L;
		private int exceptionCode;
		private HttpStatusCode status;
		private String exceptionMessage;
		
		
		
	}

