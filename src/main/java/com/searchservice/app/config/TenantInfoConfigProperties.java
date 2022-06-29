package com.searchservice.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "tenant-cache")
@Getter
@Setter
public class TenantInfoConfigProperties {
	
	private String name;
	private String key;
	private String tenant;
	
}