package com.solr.clientwrapper.usecase.user;

import com.solr.clientwrapper.domain.dto.AdminUserDTO;
import com.solr.clientwrapper.domain.port.api.UserServicePort;
import com.solr.clientwrapper.infrastructure.entity.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateUser {

    private final UserServicePort userServicePort;

    public CreateUser(UserServicePort userServicePort) {
        this.userServicePort = userServicePort;
    }


    public User createUser(AdminUserDTO userDTO) {
    	
        return userServicePort.createUser(userDTO);
    }
    
    public void saveAccount(AdminUserDTO userDTO, String userLogin) {
    	userServicePort.saveAccount(userDTO, userLogin);
    }

}
