package com.searchservice.app.rest.errors;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Component
public class NullColumnOccurredException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private int exceptioncode;
	private String exceptionMessage;
	
	
	
}