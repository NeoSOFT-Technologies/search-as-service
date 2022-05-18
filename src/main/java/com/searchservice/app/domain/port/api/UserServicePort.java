package com.searchservice.app.domain.port.api;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.user.User;

public interface UserServicePort {
	Response getToken(User user);
}