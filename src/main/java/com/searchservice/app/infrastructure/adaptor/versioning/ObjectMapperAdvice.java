package com.searchservice.app.infrastructure.adaptor.versioning;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.searchservice.app.domain.utils.StringMatcherRegexUtil;
import com.searchservice.app.rest.errors.ContentNotFoundException;
import com.searchservice.app.rest.errors.OperationIncompleteException;

import lombok.extern.slf4j.Slf4j;

import static java.lang.Integer.parseInt;


@RestControllerAdvice
@Slf4j
public class ObjectMapperAdvice implements ResponseBodyAdvice<VersionedObjectMapper> {
    @Value("${saas-ms.request-header.api-version}")
	private String SAAS_VERSION_HEADER;
    @Value("${base-url.api-endpoint.manage-table}")
    private String BASE_URL_MANAGE_TABLE;
    
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
    	
    	Class<?> resourceClass = methodParameter.getContainingClass();
    	boolean isResourceVersioned = VersionedFeatures.getAllVersionedResources()
    			.stream()
    			.anyMatch(c -> c.equals(resourceClass));
    	log.info("ReturnType containing class: "+methodParameter.getContainingClass());
    	
    	if(isResourceVersioned) {
        	for(Class<?> c: ((Class<?>)methodParameter
        			.getGenericParameterType())
        			.getInterfaces()) {
        		if(c.equals(VersionedObjectMapper.class))
        			return true;
        	}
    	}
    	
        return false;
    }
    

	@Override
	public VersionedObjectMapper beforeBodyWrite(
			VersionedObjectMapper body,
			MethodParameter returnType, 
			MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, 
			ServerHttpRequest request,
			ServerHttpResponse response) {
		try {
			// SET SAAS version in request header
			String version = StringMatcherRegexUtil
								.getMatchedSaasVersion(BASE_URL_MANAGE_TABLE).substring(1);
			request.getHeaders().set(SAAS_VERSION_HEADER, version);
	        if(version == null) {
	        	throw new ContentNotFoundException(
	        			404, 
	        			String.format("'%s' header NOT FOUND.", SAAS_VERSION_HEADER));
	        }
	        
	        return body.toVersion(parseInt(version));
		} catch(Exception e) {
			log.error("Exception occurred: ", e);
			throw new OperationIncompleteException(500, "Unexpected error occurred. Try again later");
		}

	}
}
