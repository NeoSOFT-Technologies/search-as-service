package com.searchservice.app.rest.errors;

import java.time.LocalDateTime;


import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestApiError {
       private int statusCode;
	   private HttpStatus status;
	   private String message;
	   private HttpStatusCode statuss;
	   
	   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	   private LocalDateTime timestamp;

	   private RestApiError() {
	       timestamp = LocalDateTime.now();
	   }
	   
	   RestApiError(HttpStatus status, String message) {
	       this();
           this.statusCode=status.value();
           this.status=status;
	       this.message = message;
	   }
	   
	   RestApiError(HttpStatusCode status, String message) {
	       this();
	       
           this.statusCode=status.getCode();
           this.statuss=status;
	       this.message = message;
	   }
	   
	   RestApiError(int status, HttpStatusCode statusname,String message) {
	       this();
           this.statusCode=status;
           this.statuss=statusname;
	       this.message = message;
	   }

	   RestApiError(HttpStatus status, Throwable ex) {
	       this();
           this.statusCode=status.value();
           this.status=status;
	       this.message = ex.getLocalizedMessage();
	   }

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	   
	   
	}