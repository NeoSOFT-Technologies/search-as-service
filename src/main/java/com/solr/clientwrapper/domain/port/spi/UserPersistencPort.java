package com.solr.clientwrapper.domain.port.spi;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.solr.clientwrapper.domain.dto.AdminUserDTO;
import com.solr.clientwrapper.domain.dto.UserDTO;
import com.solr.clientwrapper.infrastructure.entity.User;

public interface UserPersistencPort {

    Optional<User> findOneByActivationKey(String key);

    Optional<User> completePasswordReset(String newPassword, String key);

    Optional<User> requestPasswordReset(String mail);

    Optional<User> findOneByLogin(String login);

    Optional<User> findOneByEmailIgnoreCase(String email);

    Optional<User> findById(Long id);
    User update(AdminUserDTO userDTO,User user);
    User save(AdminUserDTO userDTO, String password);

    boolean delete(User existingUser);

    Page<AdminUserDTO> findAll(Pageable pageable);

    public Page<UserDTO> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);

    Page<UserDTO> getAllUsers(Pageable pageable);

    Optional<User> getUserWithAuthoritiesByLogin(String login);

    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore();

    Optional<User> findOneByResetKey(String key);

    User createUser(AdminUserDTO userDTO);

    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String login);
    
    Optional<User> findOneWithAuthoritiesByLogin(String login);
    Page<UserDTO> getAllPublicUsers(Pageable pageable);

}
