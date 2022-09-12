package com.neosoft.app.infrastructure.repository.security;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neosoft.app.infrastructure.entity.security.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long>{
	AppUser findByUsername(String userName);

	boolean existsByUsername(String username);

	void deleteByUsername(String username);
}
