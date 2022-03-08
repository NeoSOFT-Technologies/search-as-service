package com.searchservice.app.config;

import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.client.RestTemplate;

import com.searchservice.app.domain.service.JwtTokenFilterService;

@KeycloakConfiguration
public class SecurityConfiguration extends KeycloakWebSecurityConfigurerAdapter {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${keycloak.realm}")
	private String realm_name;
	
	@Value("${keycloak.resource}")
	private String client_id;
	
	@Value("${keycloak.credentials.secret}")
	private String client_Secret;
	
// Register Keycloak as the Authentication Provider
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
//        SimpleAuthorityMapper grantedAuthorityMapper = new SimpleAuthorityMapper();
//        grantedAuthorityMapper.setPrefix("ROLE_");

        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

// Defines the session authentication strategy.
    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    	//return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    	return new NullAuthenticatedSessionStrategy();
    }
    
    @Bean
    @Override
    @ConditionalOnMissingBean(HttpSessionManager.class)
    protected HttpSessionManager httpSessionManager() {
        return new HttpSessionManager();
    }
    
    @Override
	public void configure(WebSecurity web) throws Exception {
    	//web.ignoring().mvcMatchers("/swagger-ui/**").mvcMatchers("/test/**");
    	web.ignoring().antMatchers("/user/token").antMatchers("/v3/api-docs/**").antMatchers("/swagger-ui/**").antMatchers("/test/**");
	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable CSRF
        http = http.csrf().disable();

        // Set session management to stateless
        http = http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();
        
        // Add JWT token filter
       http = http.addFilterBefore(new JwtTokenFilterService(realm_name,client_id,client_Secret,restTemplate), UsernamePasswordAuthenticationFilter.class);
    }
}