package com.searchservice.app.domain.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.searchservice.app.config.AuthConfigProperties;
import com.searchservice.app.domain.port.api.PublicKeyServicePort;

import lombok.Data;
import net.spy.memcached.MemcachedClient;

@Service
@Data
public class PublicKeyService implements PublicKeyServicePort{
	
	private final Logger log = LoggerFactory.getLogger(PublicKeyService.class);
	
	@Autowired
	private RestTemplate restTemplate;
	private AuthConfigProperties authConfigProperties;
	
	@Override
	public boolean addPublicKey(String publicKey) {
		try {
			MemcachedClient mcc = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
			String publicKeyExist = checkIfPublicKeyExist();
			if(publicKeyExist.isBlank()) {
				log.debug("Adding Public Key: {} For Realm: {} In Cache Server",publicKey, authConfigProperties.getRealmName());
				return mcc.set(authConfigProperties.getRealmName(), 12000, publicKey).isDone();
			}else {
				log.debug("Replacing Public Key: {} For Realm: {} In Cache Server",publicKey, authConfigProperties.getRealmName());
				return mcc.replace(authConfigProperties.getRealmName(), 12000, publicKey).isDone();
			}		
		} catch (IOException e) {
			log.error("Error While Adding Public Key to the Cache Server",e);
			return false;
		}
	}

	@Override
	public String retirevePublicKey() {
		String publicKeyExists = checkIfPublicKeyExist();
		if(publicKeyExists.isBlank()) {
			publicKeyExists = getPublicKeyFromServer();
		}
		return publicKeyExists;
	}
	
	public String getPublicKeyFromServer() {
		String publicKey = "";
		try {
			log.debug("Obtaining Public Key Value For Realm: {} From the Server",authConfigProperties.getRealmName());
			ResponseEntity<String> result = restTemplate.getForEntity(authConfigProperties.getKeyUrl()
					+ authConfigProperties.getRealmName(), String.class);
			JSONObject obj = new JSONObject(result.getBody());
			if (obj.has("public_key")) {
				publicKey = obj.getString("public_key");
				log.debug("Public Key Obtained Successfully from the Server ");
				addPublicKey(publicKey);
			}
		} catch (Exception e) {
			log.error("Something Went Wrong While Obtaining Public Key From Server",e);
		}
		return publicKey;
	}
	
	public String checkIfPublicKeyExist() {
		String existingPublicKey = "";
		try {
			log.debug("Checking Public Key Value For Realm: {} In Cache Server", authConfigProperties.getRealmName());
			MemcachedClient mcc = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
			existingPublicKey = mcc.get(authConfigProperties.getRealmName()).toString();
			log.info("Public Key Successfully Obatined From the Cache Server For Realm: {}", authConfigProperties.getRealmName());
		} catch (Exception e) {
			 log.error("Public Key Not Found For Realm: {} ", authConfigProperties.getRealmName());
		}
		return existingPublicKey;
	}
	

}
