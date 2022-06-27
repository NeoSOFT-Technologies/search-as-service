package com.searchservice.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "user-cache")
@Getter
@Setter
public class UserPermissionConfigProperties {
	
	private String name;
	private String key;
	private String view;
	private String create;
	private String edit;
	private String delete;
	
}