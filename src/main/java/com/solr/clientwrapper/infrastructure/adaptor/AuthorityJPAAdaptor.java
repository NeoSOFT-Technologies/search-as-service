package com.solr.clientwrapper.infrastructure.adaptor;

import java.util.List;
import java.util.Optional;

import com.solr.clientwrapper.domain.port.spi.AuthorityPersistencePort;
import com.solr.clientwrapper.infrastructure.entity.Authority;
import com.solr.clientwrapper.infrastructure.repository.AuthorityRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorityJPAAdaptor implements AuthorityPersistencePort {



    private final AuthorityRepository authRepository;

    public AuthorityJPAAdaptor(AuthorityRepository authRepository) {
        this.authRepository = authRepository;
    }
    
    public List<Authority> findAll() {
        return authRepository.findAll();
    }
    
    public Optional<Authority> findById(String id) {
        return     authRepository.findById(id);

    }

}
