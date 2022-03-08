package com.searchservice.app;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages={"com.searchservice.app"})
public class SearchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchServiceApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	// For Keycloak @START
		@Bean
	    public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
	        return new KeycloakSpringBootConfigResolver();
	    }
		
//		@Bean
//		public FilterRegistrationBean<JwtTokenFilterService> jwtTokenFilter() {
//		    FilterRegistrationBean<JwtTokenFilterService> registrationBean = new FilterRegistrationBean<>();
//		    registrationBean.setFilter(new JwtTokenFilterService());
//		    registrationBean.addUrlPatterns("/cacheschema/*","/api/*","/rabbit-mq/*","/throttle/*");
//		    return registrationBean;
//		}
	// For Keycloak @END

}
