package com.searchservice.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.client.RestTemplate;

import com.searchservice.app.domain.filter.JwtTokenFilterService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired KeycloakConfigProperties keycloakConfigProperties;
	
//	@Value("${keycloak.realm}")
//	private String realm_name;
//	
//	@Value("${keycloak.resource}")
//	private String tenant_id;
//	
//	@Value("${keycloak.credentials.secret}")
//	private String client_Secret;
	
// Register Keycloak as the Authentication Provider
   

// Defines the session authentication strategy.
   
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
       http = http.addFilterBefore(new JwtTokenFilterService(restTemplate), UsernamePasswordAuthenticationFilter.class);
    }
}