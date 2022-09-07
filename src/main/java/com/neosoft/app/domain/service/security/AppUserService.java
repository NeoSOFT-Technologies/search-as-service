package com.neosoft.app.domain.service.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neosoft.app.domain.port.api.AppUserServicePort;
import com.neosoft.app.infrastructure.entity.security.AppUser;
import com.neosoft.app.infrastructure.entity.security.Role;
import com.neosoft.app.infrastructure.repository.security.AppUserRepository;
import com.neosoft.app.infrastructure.repository.security.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AppUserService implements AppUserServicePort, UserDetailsService {

	private final AppUserRepository appUserRepository;
	private final RoleRepository roleRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUser user = appUserRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("username not found: " + username);
		}

		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		});

		return new User(user.getUsername(), user.getPassword(), authorities);
	}

	@Override
	public AppUser saveUser(AppUser newAppUser) {
		newAppUser.setPassword(bCryptPasswordEncoder.encode(newAppUser.getPassword()));
		return appUserRepository.save(newAppUser);
	}

	@Override
	public Role saveRole(Role role) {
		return roleRepository.save(role);
	}

	@Override
	public void addRoleToAppUser(String username, String rolename) {
		AppUser tempAppUser = appUserRepository.findByUsername(username);
		Role tempRole = roleRepository.findByName(rolename);
		tempAppUser.getRoles().add(tempRole);

	}

	@Override
	public AppUser getUser(String username) {
		return appUserRepository.findByUsername(username);
	}

	@Override
	public List<AppUser> getUsers() {
		return appUserRepository.findAll();
	}

	@Override
	public void deleteUserByUsername(String username) throws UsernameNotFoundException {
		AppUser tempAppUser = appUserRepository.findByUsername(username);
		appUserRepository.delete(tempAppUser);
	}

}
