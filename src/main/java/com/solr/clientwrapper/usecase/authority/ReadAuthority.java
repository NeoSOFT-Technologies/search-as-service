package com.solr.clientwrapper.usecase.authority;

import com.solr.clientwrapper.domain.port.api.AuthorityServicePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReadAuthority {

    private final AuthorityServicePort authorityServicePort;

    public ReadAuthority(AuthorityServicePort authorityServicePort) {
        this.authorityServicePort = authorityServicePort;
    }

    public List<String> getAuthorities() {
    	return authorityServicePort.getAuthorities();
    }

}
