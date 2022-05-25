package com.searchservice.app.domain.utils.security;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;

public class SecurityUtil {
	
	private static final Logger log = LoggerFactory.getLogger(SecurityUtil.class);
	
	private SecurityUtil() {}
	
	public static boolean validate(String token, String rsaPublicKey) {
        boolean isTokenValid = false;
    	try {
    		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(rsaPublicKey));
    		KeyFactory kf = KeyFactory.getInstance("RSA");
    		PublicKey publicKey= kf.generatePublic(keySpec);
    		Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token);
    		 isTokenValid = true;
    		 log.debug("Token Validation Successfull");
    		 } catch (Exception e) {
    			 log.debug("Token Validation Failed",e);
    	}
    	return isTokenValid;
        
	}
	
	public static String getTokenFromRequestHeader(
			HttpServletRequest request, 
			HttpServletResponse response, 
			ObjectMapper mapper, 
			Map<String, Object> errorDetails) throws ServletException, IOException {
		
		errorDetails = new HashMap<>();
		// Get authorization header and validate
		final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		log.info("[JwtTokenFilterService][doFilterInternal] Authorization Header Value : {}",header);
		if (null == header || header.isEmpty() || !header.startsWith("Bearer ")) {
			errorDetails.put("Unauthorized", "Authorization token not found");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			mapper.writeValue(response.getWriter(), errorDetails);
			return null;
		}
		
		return header.split(" ")[1].trim();
	}
	
}
