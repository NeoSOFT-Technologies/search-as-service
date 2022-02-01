package com.searchservice.app.infrastructure.adaptor.versioning;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.searchservice.app.rest.errors.ContentNotFoundException;

import static java.lang.Integer.parseInt;

import java.util.Iterator;

@RestControllerAdvice
public class AdapterAdvice implements ResponseBodyAdvice<Versioned> {
    private static final String PROTOCOL_VERSION_HEADER = "X-Protocol-Version";

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
    	for(Class<?> c: ((Class<?>)methodParameter.getGenericParameterType()).getInterfaces()) {
    		if(c.equals(Versioned.class))
    			return true;
    	}
    	
        return false;
    }

    @Override
    public Versioned beforeBodyWrite(
            Versioned versioned,
            MethodParameter methodParameter,
            MediaType mediaType,
            Class<? extends HttpMessageConverter<?>> aClass,
            ServerHttpRequest serverHttpRequest,
            ServerHttpResponse serverHttpResponse) {

        String version = serverHttpRequest.getHeaders().getFirst(PROTOCOL_VERSION_HEADER);

        if(version == null) {
        	throw new ContentNotFoundException(
        			404, 
        			String.format("'%s' header NOT FOUND.", PROTOCOL_VERSION_HEADER));
        }
        
        return versioned.toVersion(parseInt(version));
    }
}
