package com.searchservice.app.domain.port.api;

import com.searchservice.app.domain.dto.Response;

public interface UserServicePort {
	Response getToken(String userName, String password);
}