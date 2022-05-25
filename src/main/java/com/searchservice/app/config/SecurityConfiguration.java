package com.searchservice.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.searchservice.app.domain.filter.JwtTokenAuthorizationFilter;
import com.searchservice.app.domain.service.PublicKeyService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	
	@Autowired 
	AuthConfigProperties authConfigProperties;
	
	@Autowired
	private PublicKeyService publicKeyService;

    @Override
	public void configure(WebSecurity web) throws Exception {
    	web.ignoring().antMatchers("/user/token").antMatchers("/v3/api-docs/**").antMatchers("/swagger-ui/**").antMatchers("/test/**");
	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
		/*
		 * // Disable CSRF http = http.csrf().disable();
		 * 
		 * // Set session management to stateless http = http .sessionManagement()
		 * .sessionCreationPolicy(SessionCreationPolicy.STATELESS) .and();
		 */
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

		expressionInterceptUrlRegistry
		.antMatchers(HttpMethod.GET, "/api/v1/manage/table/**").hasAnyRole("user", "admin")
		.antMatchers("/api/v1/manage/table/**").hasAnyRole("admin")
		.antMatchers("/api/v1/ingest/**", "/api/v1/ingest-nrt/**").hasAnyRole("admin");
        
        expressionInterceptUrlRegistry.antMatchers("/api/v1/ingest/**", "/api/v1/ingest-nrt/**").hasAnyRole("admin");

        expressionInterceptUrlRegistry
        	.anyRequest().permitAll();
        
//        expressionInterceptUrlRegistry.antMatchers("/api/v1/manage/table/**").hasAuthority("ADMIN")
//        .and()
//        .exceptionHandling().accessDeniedHandler();
        
		// Add JWT token filter
		http.addFilterBefore(new JwtTokenAuthorizationFilter(authConfigProperties, publicKeyService),
				UsernamePasswordAuthenticationFilter.class);
    }
}