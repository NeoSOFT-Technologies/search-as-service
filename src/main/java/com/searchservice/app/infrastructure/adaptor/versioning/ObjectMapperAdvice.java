package com.searchservice.app.infrastructure.adaptor.versioning;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.searchservice.app.domain.dto.MyApiResponseDTO;
import com.searchservice.app.rest.errors.ContentNotFoundException;
import com.searchservice.app.rest.errors.NullPointerOccurredException;

import static java.lang.Integer.parseInt;


@RestControllerAdvice
public class ObjectMapperAdvice implements ResponseBodyAdvice<ResponseEntity<VersionedObjectMapper>> {
    private static final String PROTOCOL_VERSION_HEADER = "X-Protocol-Version";

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
    	for(Class<?> c: ((Class<?>)methodParameter
    			.getGenericParameterType())
    			.getInterfaces()) {
    		System.out.println("cccccccccc :: "+c);
    		System.out.println("supports ??? :: "+c.equals(VersionedObjectMapper.class));
    		if(c.equals(VersionedObjectMapper.class))
    			return true;
    	}
    	
    	System.out.println("Meth Parameter >>>>>>>>> :: "+methodParameter);
    	System.out.println("Meth Parameter; Return Type >>>>>>>>> :: "
    			+methodParameter.getParameterType());
    	System.out.println("Meth Parameter; Return Type 2 >>>>>>>>> :: "
    			+methodParameter.getParameterIndex());
    	System.out.println("Meth Parameter; Return Type 4 >>>>>>>>> :: "
    			+methodParameter.getParameterAnnotations().getClass());
    	System.out.println("Meth Parameter; Return Type 5 >>>>>>>>> :: "
    			+methodParameter.getParameterName());
    	System.out.println("Meth Parameter; Nested Gen Type >>>>>>>>> :: "
    			+methodParameter.getNestedGenericParameterType());
    	System.out.println("Meth Parameter; Gen Type >>>>>>>>> :: "
    			+methodParameter.getGenericParameterType().getTypeName());
    	System.out.println("Meth Parameter; Member >>>>>>>>> :: "
    			+methodParameter.getMember());
    	System.out.println("Meth Parameter; Nested Member >>>>>>>>> :: "
    			+methodParameter
    			.getContainingClass()
    			);
    	System.out.println("DTO class >>>>>>> :: "+MyApiResponseDTO.class);
    	
    	
    	System.out.println("Meth Parameter; Nested Gen Type 2 >>>>>>>>> :: "
    			+methodParameter.getNestedGenericParameterType()
    			);
    	System.out.println("\n");
    	System.out.println("Meth Parameter; Gen Type final >>>>>>>>> :: "
    			+((Class<?>)methodParameter
    			.getGenericParameterType())
    			.getInterfaces());
    	System.out.println("\n");
    	
        return false;
    }
    

	@Override
	public ResponseEntity<VersionedObjectMapper> beforeBodyWrite(
			ResponseEntity<VersionedObjectMapper> body,
			MethodParameter returnType, 
			MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, 
			ServerHttpRequest request,
			ServerHttpResponse response) {
        String version = request.getHeaders().getFirst(PROTOCOL_VERSION_HEADER);
        if(version == null) {
        	throw new ContentNotFoundException(
        			404, 
        			String.format("'%s' header NOT FOUND.", PROTOCOL_VERSION_HEADER));
        }
        
        VersionedObjectMapper versionedObjMap = body.getBody();
        if(versionedObjMap != null)
        	return new ResponseEntity<>(
        		versionedObjMap.toVersion(parseInt(version)), 
        		HttpStatus.OK);
        else
        	throw new NullPointerOccurredException(400, "ObjectMapper is NULL");
	}
	
	
//  @Override
//  public VersionedObjectMapper beforeBodyWrite(
//          VersionedObjectMapper versioned,
//          MethodParameter methodParameter,
//          MediaType mediaType,
//          Class<? extends HttpMessageConverter<?>> aClass,
//          ServerHttpRequest serverHttpRequest,
//          ServerHttpResponse serverHttpResponse) {
//
//      String version = serverHttpRequest.getHeaders().getFirst(PROTOCOL_VERSION_HEADER);
//      if(version == null) {
//      	throw new ContentNotFoundException(
//      			404, 
//      			String.format("'%s' header NOT FOUND.", PROTOCOL_VERSION_HEADER));
//      }
//      
//      return versioned.toVersion(parseInt(version));
//  }
}
