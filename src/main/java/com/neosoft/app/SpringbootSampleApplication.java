package com.neosoft.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages={"com.neosoft.app", "com.neosoft.app.infrastructure.adaptor"})
@EnableCaching
@EnableScheduling
public class SpringbootSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootSampleApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}
