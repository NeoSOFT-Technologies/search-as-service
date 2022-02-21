package com.searchservice.app.rest.errors;

import java.time.LocalDateTime;


import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class RestApiError {
       private int statusCode;
	   private HttpStatus status;
	   private String message;
	   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	   private LocalDateTime timestamp;

	   private RestApiError() {
	       timestamp = LocalDateTime.now();
	   }

	   RestApiError(HttpStatus status,int statusCode) {
	       this();
	       this.statusCode=status.value();
	       this.status=status;
	       this.message = "Unexpected Exception";
	   }
	   
	   RestApiError(HttpStatus status, String message) {
	       this();
           this.statusCode=status.value();
           this.status=status;
	       this.message = message;
	   }

	   RestApiError(HttpStatus status, Throwable ex) {
	       this();
           this.statusCode=status.value();
           this.status=status;
	       this.message = ex.getLocalizedMessage();
	   }
	}