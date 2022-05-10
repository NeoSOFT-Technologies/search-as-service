package com.searchservice.app.domain.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.searchservice.app.config.AuthConfigProperties;
import com.searchservice.app.domain.port.api.PublicKeyServicePort;


@Service
public class PublicKeyService implements PublicKeyServicePort{

	private static final String CACHE_NAME = "publicKeyCache";

	private final Logger log = LoggerFactory.getLogger(PublicKeyService.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired 
	private AuthConfigProperties authConfigProperties;
	
	@Autowired
	private CacheManager cacheManager;
		
	@Override	
	@Cacheable(cacheNames = {PublicKeyService.CACHE_NAME}, key = "#realmName")
	public String retirevePublicKey(String realmName) {
		log.info("Adding Public Key Value in Cache for Realm: {}", realmName);
		return getPublicKeyFromServer(realmName);
	}
	
	@CachePut(cacheNames = {PublicKeyService.CACHE_NAME}, key = "#realmName")
	public String updatePublicKey(String realmName) {
		log.info("Updating Public Key Value in Cache for Realm: {}", realmName);
		return getPublicKeyFromServer(realmName);
	}
	
	public String getPublicKeyFromServer(String realmName) {
		String publicKey = "";
		try {
			log.debug("Obtaining Public Key Value For Realm: {}",realmName);
			ResponseEntity<String> result = restTemplate.getForEntity(authConfigProperties.getKeyUrl()
					+ realmName, String.class);
			JSONObject obj = new JSONObject(result.getBody());
			if (obj.has("public_key")) {
				publicKey = obj.getString("public_key");
				log.debug("Public Key Obtained Successfully");
			}
		} catch (Exception e) {
			log.error("Something Went Wrong While Obtaining Public Key");
		}
		return publicKey;
	}

	public boolean checkIfPublicKeyExistsInCache() {
		boolean isPublicKeyPresent = false;
		String realmName = authConfigProperties.getRealmName();
		Cache cache = cacheManager.getCache(PublicKeyService.CACHE_NAME);
		if(cache.get(authConfigProperties.getRealmName())!=null) {
			log.debug("Public Key Found in Cache For Realm: {}",realmName);
			updatePublicKey(realmName);
			isPublicKeyPresent = true;
		}
		return isPublicKeyPresent;
	}
	
	
}
