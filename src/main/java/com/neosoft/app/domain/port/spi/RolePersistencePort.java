package com.neosoft.app.domain.port.spi;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.neosoft.app.domain.dto.security.RoleDTO;


@Component
public interface RolePersistencePort {
	List<RoleDTO> getAll();
	Optional<RoleDTO> getOneByName(String rolename);
	Optional<RoleDTO> addOne(RoleDTO role);
	void removeOne(String rolename);
}
