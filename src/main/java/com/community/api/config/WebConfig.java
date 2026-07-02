package com.community.api.config;

import com.community.api.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/users/signup",
                        "/users/login",
                        "/images",
                        "images/**"
                );
    }

    // CORS 에러를 해결하기 위한 코드 추가
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins("http://127.0.0.1:5500", "http://localhost:5500")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*");
    }

    // 저장된 이미지 파일을 꺼내서 보여줌
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        String uploadPath = "file:" + System.getProperty("user.home") + "/community-uploads/";
        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadPath);
    }
}
