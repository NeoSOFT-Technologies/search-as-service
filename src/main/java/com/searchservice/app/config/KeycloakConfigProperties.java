package com.searchservice.app.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
public class KeycloakConfigProperties {

    private Credentials credentials;
    private String realm;
    private String resource;
    private String auth_server_url;
    private String ssl_required;
    private String public_client;
    private String use_resource_role_mappings;
    @Getter
    @Setter
    public static class Credentials {
        private String secret;
    }

}
