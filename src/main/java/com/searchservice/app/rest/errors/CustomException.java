package com.searchservice.app.rest.errors;

import org.springframework.stereotype.Component;

import com.searchservice.app.domain.utils.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Component
public class CustomException extends RuntimeException{

		private static final long serialVersionUID = 1L;
		private int exceptionCode;
		private HttpStatusCode status;
		private String exceptionMessage;
		
		
		
	}

