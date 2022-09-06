package com.neosoft.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "authentication")
@Getter
@Setter
public class AuthConfigProperties {
	
	private String realmName;
	private String keyUrl;
}

