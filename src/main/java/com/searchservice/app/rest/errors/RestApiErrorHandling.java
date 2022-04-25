package com.searchservice.app.rest.errors;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.searchservice.app.domain.dto.BaseResponse;

public class RestApiErrorHandling extends BaseResponse{
      
       private HttpStatusCode status;
	 
	   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	   private LocalDateTime timestamp;

	   private RestApiErrorHandling() {
	       timestamp = LocalDateTime.now();
	   }
	   RestApiErrorHandling(HttpStatusCode status,int statusCode) {
	       this();
	       super.statusCode=status.getCode();
	       this.status=status;
	       super.message = "Unexpected Exception";
	   }
	   
	   RestApiErrorHandling(HttpStatusCode status, String message) {
	       this();
	       super.statusCode=status.getCode();
           this.status=status;
           super.message = message;
	   }

	   RestApiErrorHandling(HttpStatusCode status, Throwable ex) {
	       this();
	       super.statusCode=status.getCode();
           this.status=status;
           super.message = ex.getLocalizedMessage();
	   }
	   
	   RestApiErrorHandling(int statuscode, Throwable ex) {
	       this();
	       super.statusCode=statuscode;
	       super.message = ex.getLocalizedMessage();
	   }
	   
	   RestApiErrorHandling(int statuscode, HttpStatusCode status,String message) {
	       this();
	       super.statusCode=statuscode;
           this.status=status;
           super.message = message;
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