package com.shop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author lehr
 */
@Configuration
@EnableConfigurationProperties(ShopProperties.class)
public class JwtInterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private final ShopProperties shopProperties;

    public JwtInterceptorConfig(ShopProperties shopProperties) {
        this.shopProperties = shopProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //默认拦截所有路径
        registry.addInterceptor(authenticationInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(shopProperties.getLoginInterceptorExcludePath().toArray(new String[]{}));//白名单URL;
    }

    @Bean
    public JwtAuthenticationInterceptor authenticationInterceptor() {
        return new JwtAuthenticationInterceptor();
    }
}