package com.neosoft.app.domain.port.api;

import org.springframework.stereotype.Component;

@Component
public interface PublicKeyServicePort {
	String retrievePublicKey(String realmName);
	boolean checkIfPublicKeyExistsInCache();

}

