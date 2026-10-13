package com.course.bookmgmtbackend.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.course.bookmgmtbackend.interceptor.AuthInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	 
	private final AuthInterceptor authInterceptor;
 
	public WebConfig(AuthInterceptor authInterceptor) {
		this.authInterceptor = authInterceptor;
	}
 
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// Public/role rules live inside AuthInterceptor, so just apply it to the whole API
		registry.addInterceptor(authInterceptor).addPathPatterns("/api/**");
	}
}
