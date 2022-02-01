package com.searchservice.app.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.searchservice.app.domain.dto.MyApiResponse2;
import com.searchservice.app.domain.dto.MyApiResponseDTO;
import com.searchservice.app.domain.dto.MyApiResponseDTOv2;
import com.searchservice.app.domain.dto.ApiResponseDTO;
import com.searchservice.app.domain.dto.GetListItemsResponseDTO;
import com.searchservice.app.domain.dto.table.GetCapacityPlanDTO;
import com.searchservice.app.domain.dto.table.ManageTableDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTO;
import com.searchservice.app.domain.dto.table.TableSchemaResponseDTO;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.NullPointerOccurredException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/test-versioning")
public class VersioningTestResource {

    private final Logger log = LoggerFactory.getLogger(VersioningTestResource.class);

    private static final String BAD_REQUEST_MSG = "REST call could not be performed";
    private static final String DEFAULT_EXCEPTION_MSG = "REST call could not be performed";

    
	@GetMapping("/{version}")
	@Operation(summary = "/show-versioned-response")
	public MyApiResponseDTO testVersioning(
			@PathVariable String version) {
		return new MyApiResponseDTO(
				HttpStatus.OK.toString(), 
				200, 
				"Job", 
				"done");
	}
    
    
	@GetMapping("/res-ent/{version}")
	@Operation(summary = "/show-versioned-response")
	public ResponseEntity<MyApiResponseDTOv2> testVersioning1(
			@PathVariable String version) {
		MyApiResponseDTOv2 resp = new MyApiResponseDTOv2(
				HttpStatus.OK.toString(), 
//				200, 
				"Job", 
				"done");
		return ResponseEntity.status(HttpStatus.OK).body(resp);
	}
	
	
	@GetMapping("")
	@Operation(summary = "/show-versioned-response")
	public ResponseEntity<MyApiResponseDTOv2> testVersioning() {
		MyApiResponseDTOv2 resp = new MyApiResponseDTOv2(
				HttpStatus.OK.toString(), 
//				200, 
				"Job", 
				"done");
		return ResponseEntity.status(HttpStatus.OK).body(resp);
//		if(resp != null)
//			return ResponseEntity.status(HttpStatus.OK).body(resp);
//		else
//			throw new NullPointerOccurredException(500, DEFAULT_EXCEPTION_MSG);
	}
}
