package com.searchservice.app.config;

import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.client.RestTemplate;

import com.searchservice.app.domain.filter.JwtTokenFilterService;

@KeycloakConfiguration
@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class SecurityConfiguration extends KeycloakWebSecurityConfigurerAdapter {
	
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

	/*
	 * @Override protected void configure(HttpSecurity http) throws Exception { //
	 * Disable CSRF http = http.csrf().disable();
	 * 
	 * // Set session management to stateless http = http .sessionManagement()
	 * .sessionCreationPolicy(SessionCreationPolicy.STATELESS) .and();
	 * http.authorizeRequests() .antMatchers("/api/v1/manage/table/**").permitAll();
	 * http.authorizeRequests() // .antMatchers(HttpMethod.GET).hasAnyRole("user")
	 * // .antMatchers(HttpMethod.GET, "/api/v1/manage/table/**").hasAnyRole("user")
	 * // .antMatchers(HttpMethod.POST,
	 * "/api/v1/manage/table/**").hasAnyRole("admin") //
	 * .antMatchers("/api/v1/manage/table/**").hasAnyRole("user")
	 * .antMatchers("/api/v1/ingest/**").hasAuthority("ROLE_USER") .and()
	 * .authorizeRequests() .anyRequest().permitAll();
	 * 
	 * // Add JWT token filter http = http.addFilterBefore(new
	 * JwtTokenFilterService(keycloakConfigProperties,restTemplate),
	 * UsernamePasswordAuthenticationFilter.class); }
	 */
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.headers().frameOptions().sameOrigin();
        
        
        // Set session management to stateless
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry = http.cors()
        .and()
        .csrf().disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests();
        
//        expressionInterceptUrlRegistry = expressionInterceptUrlRegistry.antMatchers("/api/v1/manage/table/**").hasRole("admin");
        expressionInterceptUrlRegistry = expressionInterceptUrlRegistry.antMatchers(HttpMethod.GET, "/api/v1/manage/table/**").hasRole("user");
        expressionInterceptUrlRegistry = expressionInterceptUrlRegistry.antMatchers("/api/v1/manage/table/**").hasRole("admin");
        
        expressionInterceptUrlRegistry.antMatchers("/api/v1/ingest/**").hasRole("bossbatch")
        .and()
        .authorizeRequests()
        .antMatchers("/api/v1/ingest-nrt/**").hasAnyRole("bossnrt", "user");
//        expressionInterceptUrlRegistry = expressionInterceptUrlRegistry.antMatchers("/api/v1/ingest/**").hasRole("bossadmin");
//        expressionInterceptUrlRegistry = expressionInterceptUrlRegistry.antMatchers("/api/v1/ingest/**").hasRole("admin");
//        expressionInterceptUrlRegistry = expressionInterceptUrlRegistry.antMatchers("/api/v1/ingest/**").hasRole("admin");
//        expressionInterceptUrlRegistry = expressionInterceptUrlRegistry.antMatchers("/api/v1/ingest/**").hasRole("admin");
//        expressionInterceptUrlRegistry = expressionInterceptUrlRegistry.antMatchers("/api/v1/ingest/**").hasRole("admin");
        

        expressionInterceptUrlRegistry.anyRequest().permitAll();
        
//		http.authorizeRequests()
//		.antMatchers("/api/v1/manage/table/**").permitAll();
//		http.authorizeRequests()
//		.antMatchers(HttpMethod.GET).hasAnyRole("user")
//		.antMatchers(HttpMethod.GET, "/api/v1/manage/table/**").hasAnyRole("user")
//		.antMatchers(HttpMethod.POST, "/api/v1/manage/table/**").hasAnyRole("admin")
//		.antMatchers("/api/v1/manage/table/**").hasAnyRole("user")
//		.antMatchers("/api/v1/ingest/**").hasAuthority("ROLE_USER")
//		.and()
//		.authorizeRequests()
//		.anyRequest().permitAll();
        
        // Add JWT token filter
       http = http.addFilterBefore(new JwtTokenFilterService(keycloakConfigProperties,restTemplate), UsernamePasswordAuthenticationFilter.class);
    }
    
}