package com.searchservice.app.domain.port.api;

import com.searchservice.app.domain.dto.ResponseDTO;

public interface UserServicePort {
	ResponseDTO getToken(String userName, String password);
}
