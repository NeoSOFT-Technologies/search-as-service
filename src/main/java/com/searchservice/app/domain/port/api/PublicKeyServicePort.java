package com.searchservice.app.domain.port.api;

public interface PublicKeyServicePort {

	boolean addPublicKey(String publicKey);
	String retirevePublicKey();
}
