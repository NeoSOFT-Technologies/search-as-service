package com.searchservice.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.searchservice.app.domain.filter.ThrottlerFilterBatch;
import com.searchservice.app.domain.filter.ThrottlerFilterNRT;
import com.searchservice.app.domain.port.api.ThrottlerServicePort;

@Configuration
public class FilterConfig {

	public ThrottlerServicePort throttlerServicePort;
	
	@Autowired
	ThrottlerFilterNRT throttlerFilterNRT;
	
	@Autowired
	ThrottlerFilterBatch throttlerFilterBatch;
	
	@Bean
	public FilterRegistrationBean<ThrottlerFilterNRT> registrationBean() {
		
		FilterRegistrationBean<ThrottlerFilterNRT> registrationBean = new FilterRegistrationBean<>();
		
		registrationBean.setFilter(throttlerFilterNRT);
		registrationBean.addUrlPatterns("/api/v1/ingest-nrt/*");
		
		return registrationBean;
	}
	
	
	@Bean
	public FilterRegistrationBean<ThrottlerFilterBatch> registrationBean2() {
		
		FilterRegistrationBean<ThrottlerFilterBatch> registrationBean = new FilterRegistrationBean<>();
		
		registrationBean.setFilter(throttlerFilterBatch);
		registrationBean.addUrlPatterns("/api/v1/ingest/*");
		
		return registrationBean;
	}
}
