package com.solr.clientwrapper.domain.dto;

import com.solr.clientwrapper.infrastructure.entity.User;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A DTO representing a user, with only the public attributes.
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class UserDTO {

    private Long id;

    private String login;

    
    public UserDTO(User user) {
        this.id = user.getId();
        // Customize it here if you need, or not, firstName/lastName/etc
        this.login = user.getLogin();
    }

}