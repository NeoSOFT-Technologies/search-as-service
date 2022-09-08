package com.neosoft.app.config;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.neosoft.app.domain.filter.UserAuthorizationFilter;
import com.neosoft.app.domain.service.security.AppUserService;
import com.neosoft.app.domain.utils.security.AuthUtil;
import com.neosoft.app.domain.utils.security.SecurityLabel;
import com.neosoft.app.infrastructure.entity.security.AppUser;
import com.neosoft.app.infrastructure.repository.security.AppUserRepository;

import lombok.NoArgsConstructor;

@Configuration
@EnableWebSecurity
@NoArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private AuthUtil authUtil;
	
	@Autowired
	private AppUserService appUserService;
	
	@Value("${base-url.api-endpoint.product}")
	private String productUrl;
	
	@Value("${base-url.api-endpoint.app-user}")
	private String appUserUrl;
	
	public SecurityConfiguration(AppUserRepository appUserRepository, AuthUtil authUtil, AppUserService appUserService) {
		super();
		this.appUserRepository = appUserRepository;
		this.authUtil = authUtil;
		this.appUserService = appUserService;
	}

	private final UserDetailsService userDetailsService = new UserDetailsService() {
		@Override
		public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			AppUser user = appUserRepository.findByUsername(username);
			if (user == null) {
				throw new UsernameNotFoundException("Username not found: " + username);
			}

			Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
			user.getRoles().forEach(role -> {
				authorities.add(new SimpleGrantedAuthority(role.getName()));
			});

			return new User(user.getUsername(), user.getPassword(), authorities);
		}
	};

	private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}

    @Override
	public void configure(WebSecurity web) throws Exception {
    	web.ignoring().antMatchers("/app-user").antMatchers("/v3/api-docs/**").antMatchers("/swagger-ui/**").antMatchers("/test/**");
	}
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
		// Disable CSRF
		http = http.csrf().disable();

		// Set session management to stateless
		http = http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();
		
		http.authorizeRequests().antMatchers("/api/v1/login").permitAll()
//		.antMatchers(HttpMethod.PUT, productUrl)
//			.hasAnyAuthority(SecurityLabel.KEYWORD_ROLE_ADMIN.getLabel())
		.antMatchers("api/v1/product/del/**")
			.hasAnyAuthority("sjhdbc").and();
//		http.authorizeRequests().antMatchers(HttpMethod.DELETE, productUrl)
//			.hasRole("sjdhfvb");
//		.antMatchers(HttpMethod.POST, productUrl)
//			.hasAnyAuthority(SecurityLabel.KEYWORD_ROLE_ADMIN.getLabel(), "admin")
//		.antMatchers(HttpMethod.GET, productUrl).permitAll()
//		.antMatchers(appUserUrl)
//			.hasAnyAuthority(SecurityLabel.KEYWORD_ROLE_ADMIN.getLabel())
//		.anyRequest().authenticated();
        
		// Add Auth filter
		http.addFilterBefore(new UserAuthorizationFilter(authUtil, appUserService), UsernamePasswordAuthenticationFilter.class);
    }


	@Bean(name = "AuthenticationManager")
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}
