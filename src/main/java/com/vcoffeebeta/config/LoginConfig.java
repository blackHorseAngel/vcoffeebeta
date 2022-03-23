package com.vcoffeebeta.config;

import com.vcoffeebeta.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author zhangshenming
 * @date 2022/3/23 6:22
 * @version 1.0
 */
@Configuration
@Slf4j
public class LoginConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("使用loginConfig中的登录拦截器");
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/*");
    }
}
