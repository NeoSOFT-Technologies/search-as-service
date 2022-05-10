package com.searchservice.app.domain.port.spi;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.infrastructure.adaptor.KeycloakConfigAdapter.KeycloakConfigAdapterResponse;

public interface KeycloakConfigAdapterPort {

	public KeycloakConfigAdapterResponse getRolesInfo(String url);
	
	public Response getAdminCliToken(String username, String password);
}
