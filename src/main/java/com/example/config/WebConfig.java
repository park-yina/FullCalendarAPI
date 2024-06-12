package com.example.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
	public class WebConfig implements WebMvcConfigurer {

	    @Override
	    public void addInterceptors(InterceptorRegistry registry) {
	        registry.addInterceptor(new AuthInterceptor())
	                .addPathPatterns("/**") // 모든 경로에 대해 Interceptor 적용
	                .excludePathPatterns("/css/**", "/js/**", "/images/**"); // 정적 자원 경로는 제외
	    }
	}