package com.searchservice.app.domain.filter;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

//@Component
public class JwtTokenFilterService extends OncePerRequestFilter {

	private RestTemplate restTemplate;
	private ObjectMapper mapper = new ObjectMapper();
	private final Logger log = LoggerFactory.getLogger(JwtTokenFilterService.class);

	public JwtTokenFilterService() {
		super();
	}

	public JwtTokenFilterService(RestTemplate restTemplate) {
		super();
		this.restTemplate = restTemplate;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		Map<String, Object> errorDetails = new HashMap<>();
		// Get authorization header and validate
		final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		 log.info("[JwtTokenFilterService][doFilterInternal] Authorization Header Value : {}",header);
		if (null == header || header.isEmpty() || !header.startsWith("Bearer ")) {
			errorDetails.put("Unauthorized", "Invalid token");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			mapper.writeValue(response.getWriter(), errorDetails);
			return;
		}

		// Get jwt token and validate
		final String token = header.split(" ")[1].trim();
		 log.info("[JwtTokenFilterService][doFilterInternal] Token Value : {}",token);
		if (!validate(token, getPublicKeyTest())) {
			errorDetails.put("Unauthorized", "Invalid token");
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			mapper.writeValue(response.getWriter(), errorDetails);

		} else {
			log.debug("Token Validation Successfull");
			chain.doFilter(request, response);
		}
	}

	
	private boolean validate(String token, String rsaPublicKey) {
        boolean isTokenValid = false;
    	try {
    		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(rsaPublicKey));
    		KeyFactory kf = KeyFactory.getInstance("RSA");
    		PublicKey publicKey= kf.generatePublic(keySpec);
    		Jws<Claims> jwt = Jwts.parserBuilder()
    		                        .setSigningKey(publicKey)
    		                        .build()
    		                        .parseClaimsJws(token);
    		 isTokenValid = true;
    		 log.debug("Token is Valid");
    		 } catch (Exception e) {
    			 log.debug("Token is Invalid");
    		     e.printStackTrace();
    	}
    	return isTokenValid;
        
	}

	private String getPublicKeyTest() {
		final String baseUrl = "https://iam-keycloak.neosofttech.com/auth/realms/master";
		String publicKey = "";
		try {
			ResponseEntity<String> result = restTemplate.getForEntity(baseUrl, String.class);
			JSONObject obj = new JSONObject(result.getBody());
			if (obj.has("public_key")) {
				publicKey = obj.getString("public_key");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return publicKey;
	}
}
