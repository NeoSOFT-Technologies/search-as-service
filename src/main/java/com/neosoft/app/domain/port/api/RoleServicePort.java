package com.neosoft.app.domain.port.api;

import com.neosoft.app.domain.dto.security.RoleDTO;

public interface RoleServicePort {
	RoleDTO saveRole(RoleDTO newRoleDTO);
	void deleteAppUserByUsername(String username);
}
