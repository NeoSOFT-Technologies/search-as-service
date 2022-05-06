package com.searchservice.app.config.security;

import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;


@Component
public class KeycloakTokenManagement {

	public String getDecodedTokenPayload(String token) {
		
		String payload = token.split("\\.")[1];
		
		return new String(Base64.decodeBase64(payload), StandardCharsets.UTF_8);
	}
	
}
