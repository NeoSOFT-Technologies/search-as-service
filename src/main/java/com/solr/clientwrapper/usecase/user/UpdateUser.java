package com.solr.clientwrapper.usecase.user;

import com.solr.clientwrapper.domain.dto.AdminUserDTO;
import com.solr.clientwrapper.domain.port.api.UserServicePort;
import com.solr.clientwrapper.infrastructure.entity.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UpdateUser {

    private final UserServicePort userServicePort;

    public UpdateUser(UserServicePort userServicePort) {
        this.userServicePort = userServicePort;
    }
    
    // actions
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        userServicePort.updateUser(firstName, lastName, email, langKey, imageUrl);
    }
    
    public Optional<AdminUserDTO> updateAdminUser(AdminUserDTO adminUserDTO) {
        return userServicePort.updateUser(adminUserDTO);
    }

    public void changePassword(String currentClearTextPassword, String newPassword) {
    	userServicePort.changePassword(currentClearTextPassword, newPassword);
    }
    
    public Optional<User> requestPasswordReset(String mail) {
    	return userServicePort.requestPasswordReset(mail);
    }
    
    public Optional<User> completePasswordReset(String newPassword, String key) {
    	return userServicePort.completePasswordReset(newPassword, key);
    }

}
