package com.searchservice.app.domain.port.api;

import org.springframework.stereotype.Component;

@Component
public interface PublicKeyServicePort {
	String retirevePublicKey(String realmName);
	boolean checkIfPublicKeyExistsInCache();
}