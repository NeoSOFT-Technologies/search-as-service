package com.solr.clientwrapper.usecase.user;

import com.solr.clientwrapper.domain.dto.AdminUserDTO;
import com.solr.clientwrapper.domain.port.api.UserServicePort;
import com.solr.clientwrapper.infrastructure.entity.User;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterUser {

    private final UserServicePort userServicePort;

    public RegisterUser(UserServicePort userServicePort) {
        this.userServicePort = userServicePort;
    }

    // actions
    public User registerUser(AdminUserDTO userDTO, String password) {
    	return userServicePort.registerUser(userDTO, password);
    }
    
    public Optional<User> activateRegistration(String key) {
    	return userServicePort.activateRegistration(key);
    }

}
