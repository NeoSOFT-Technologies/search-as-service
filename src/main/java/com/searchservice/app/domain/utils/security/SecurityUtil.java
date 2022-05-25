package com.searchservice.app.domain.utils.security;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
}
