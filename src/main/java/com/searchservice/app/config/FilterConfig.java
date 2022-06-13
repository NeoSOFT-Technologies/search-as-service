package com.searchservice.app.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.searchservice.app.domain.filter.ResourcesAuthorizationFilter;
import com.searchservice.app.domain.filter.ThrottlerFilterBatch;
import com.searchservice.app.domain.filter.ThrottlerFilterNRT;

@Configuration
public class FilterConfig {
	
	@Value("${base-url.api-endpoint.home}")
	private String apiEndpoint;
	
	@Bean
	public FilterRegistrationBean<ThrottlerFilterNRT> registrationBeanNRT(ThrottlerFilterNRT throttlerFilterNRT) {
		
		FilterRegistrationBean<ThrottlerFilterNRT> registrationBean = new FilterRegistrationBean<>();
		
		registrationBean.setFilter(throttlerFilterNRT);
		registrationBean.addUrlPatterns(apiEndpoint+ "/ingest-nrt/*");
		
		return registrationBean;
	}
	
	
	@Bean
	public FilterRegistrationBean<ThrottlerFilterBatch> registrationBeanBatch(ThrottlerFilterBatch throttlerFilterBatch) {
		
		FilterRegistrationBean<ThrottlerFilterBatch> registrationBean = new FilterRegistrationBean<>();
		
		registrationBean.setFilter(throttlerFilterBatch);
		registrationBean.addUrlPatterns(apiEndpoint+ "/ingest/*");
		
		return registrationBean;
	}
	
	
	@Bean
	public FilterRegistrationBean<ResourcesAuthorizationFilter> registrationBeanResourcesAuthorization(ResourcesAuthorizationFilter resourcesAuthorizationFilter) {
		
		FilterRegistrationBean<ResourcesAuthorizationFilter> registrationBean = new FilterRegistrationBean<>();
		
		registrationBean.setFilter(resourcesAuthorizationFilter);
		registrationBean.addUrlPatterns(apiEndpoint+"/*");
		
		return registrationBean;
	}

}
