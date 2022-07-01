package com.searchservice.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.searchservice.app.domain.filter.RealmCacheManagementFilter;
import com.searchservice.app.domain.filter.ResourcesAuthorizationFilter;
import com.searchservice.app.domain.filter.ThrottlerFilterBatch;
import com.searchservice.app.domain.filter.ThrottlerFilterNRT;

@Configuration
public class FilterConfig {
	
	@Autowired
	ThrottlerFilterNRT throttlerFilterNRT;
	
	@Autowired
	ThrottlerFilterBatch throttlerFilterBatch;
	
	@Autowired
	ResourcesAuthorizationFilter resourcesAuthorizationFilter;
	
	@Autowired
	RealmCacheManagementFilter realmCacheManagementFilter;
	
	@Bean
	public FilterRegistrationBean<ThrottlerFilterNRT> registrationBeanNRT() {
		
		FilterRegistrationBean<ThrottlerFilterNRT> registrationBean = new FilterRegistrationBean<>();
		
		registrationBean.setFilter(throttlerFilterNRT);
		registrationBean.addUrlPatterns("/api/v1/ingest-nrt/*");
		
		return registrationBean;
	}
	
	
	@Bean
	public FilterRegistrationBean<ThrottlerFilterBatch> registrationBeanBatch() {
		
		FilterRegistrationBean<ThrottlerFilterBatch> registrationBean = new FilterRegistrationBean<>();
		
		registrationBean.setFilter(throttlerFilterBatch);
		registrationBean.addUrlPatterns("/api/v1/ingest/*");
		
		return registrationBean;
	}
	
	
	@Bean
	public FilterRegistrationBean<ResourcesAuthorizationFilter> registrationBeanResourcesAuthorization() {
		
		FilterRegistrationBean<ResourcesAuthorizationFilter> registrationBean = new FilterRegistrationBean<>();
		
		registrationBean.setFilter(resourcesAuthorizationFilter);
		registrationBean.addUrlPatterns("/api/v1/*");
		
		return registrationBean;
	}
	
	
	@Bean
	public FilterRegistrationBean<RealmCacheManagementFilter> registrationBeanRealmCacheManagement() {
		
		FilterRegistrationBean<RealmCacheManagementFilter> registrationBean = new FilterRegistrationBean<>();
		
		registrationBean.setFilter(realmCacheManagementFilter);
		registrationBean.addUrlPatterns("/api/v1/*");
		
		return registrationBean;
	}

}
