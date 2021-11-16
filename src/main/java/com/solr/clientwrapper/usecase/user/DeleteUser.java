package com.solr.clientwrapper.usecase.user;

import com.solr.clientwrapper.domain.port.api.UserServicePort;
import com.solr.clientwrapper.infrastructure.entity.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteUser {

    private final UserServicePort userServicePort;

    public DeleteUser(UserServicePort userServicePort) {
        this.userServicePort = userServicePort;
    }

    // actions
    public void deleteUser(String login) {
        userServicePort.deleteUser(login);
    }
    
    public boolean removeNonActivatedUser(User existingUser) {
    	return userServicePort.removeNonActivatedUser(existingUser);
    }
    
    public void removeNotActivatedUsers() {
    	userServicePort.removeNotActivatedUsers();
    }
    
    public void clearUserCaches(User user) {
    	userServicePort.clearUserCaches(user);
    }
    
}
