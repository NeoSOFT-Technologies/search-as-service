package com.searchservice.app.rest.errors;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class RestApiErrorHandling {
       private int statusCode;
       private HttpStatusCode status;
	   private String message;
	   
	   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	   private LocalDateTime timestamp;

	   private RestApiErrorHandling() {
	       timestamp = LocalDateTime.now();
	   }
	   RestApiErrorHandling(HttpStatusCode status,int statusCode) {
	       this();
	       this.statusCode=status.getCode();
	       this.status=status;
	       this.message = "Unexpected Exception";
	   }
	   
	   RestApiErrorHandling(HttpStatusCode status, String message) {
	       this();
           this.statusCode=status.getCode();
           this.status=status;
	       this.message = message;
	   }

	   RestApiErrorHandling(HttpStatusCode status, Throwable ex) {
	       this();
           this.statusCode=status.getCode();
           this.status=status;
	       this.message = ex.getLocalizedMessage();
	   }
	   
	   RestApiErrorHandling(int statuscode, Throwable ex) {
	       this();
           this.statusCode=statuscode;
	       this.message = ex.getLocalizedMessage();
	   }
	   
	   RestApiErrorHandling(int statuscode, HttpStatusCode status,String message) {
	       this();
           this.statusCode=statuscode;
           this.status=status;
	       this.message = message;
	   }

	public RestApiErrorHandling(int code, String messgae) {
		this.statusCode=code;
        this.message = message;
	}
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public HttpStatusCode getStatus() {
		return status;
	}

	public void setStatus(HttpStatusCode status) {
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