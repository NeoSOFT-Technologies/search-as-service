package com.searchservice.app.domain.service;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.json.JSONObject;
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
	
	@Autowired
	private RestTemplate restTemplate;
	private AuthConfigProperties authConfigProperties;
	
	@Override
	public boolean addPublicKey(String publicKey) {
		try {
			MemcachedClient mcc = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
			String publicKeyExist = checkIfPublicKeyExist();
			if(publicKeyExist.isBlank()) {
				return mcc.set(authConfigProperties.getRealmName(), 12000, publicKey).isDone();
			}else {
				return mcc.replace(authConfigProperties.getRealmName(), 12000, publicKey).isDone();
			}		
		} catch (IOException e) {
			e.printStackTrace();
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
			ResponseEntity<String> result = restTemplate.getForEntity(authConfigProperties.getKeyUrl()
					+ authConfigProperties.getRealmName(), String.class);
			JSONObject obj = new JSONObject(result.getBody());
			if (obj.has("public_key")) {
				publicKey = obj.getString("public_key");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return publicKey;
	}
	
	public String checkIfPublicKeyExist() {
		String existingPublicKey = "";
		try {
			MemcachedClient mcc = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
			existingPublicKey = mcc.get(authConfigProperties.getRealmName()).toString();
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return existingPublicKey;
	}

}
