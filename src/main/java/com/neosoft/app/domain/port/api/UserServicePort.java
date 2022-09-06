package com.neosoft.app.domain.port.api;

import com.neosoft.app.domain.dto.Response;
import com.neosoft.app.domain.dto.user.User;

public interface UserServicePort {
	Response getToken(User user);
}