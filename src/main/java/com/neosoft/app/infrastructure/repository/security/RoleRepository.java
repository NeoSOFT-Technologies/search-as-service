package com.neosoft.app.infrastructure.repository.security;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neosoft.app.infrastructure.entity.security.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByName(String name);
	void deleteByName(String rolename);
}
