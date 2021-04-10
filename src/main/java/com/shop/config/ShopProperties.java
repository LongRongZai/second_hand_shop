package com.shop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "shop")
@Data
public class ShopProperties {
    //无需验证的路由
    private List<String> loginInterceptorExcludePath;
    //附件保存地址
    private String uploadSavePath;
    //附件预览地址
    private String uploadViewPath;
}