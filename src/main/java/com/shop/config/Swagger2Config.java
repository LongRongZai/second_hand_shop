package com.shop.config;

import com.google.common.base.Predicate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spi.service.contexts.SecurityContextBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableSwagger2 //这个注解一定不能忘记加，否则swagger就不生效，也可以加在启动类上面
@EnableConfigurationProperties(ShopProperties.class)
public class Swagger2Config {
    //这里同样需要用到配置的白名单URL
    private ShopProperties shopProperties;

    public Swagger2Config(ShopProperties shopProperties) {
        this.shopProperties = shopProperties;
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.shop.controller"))// 指定api路径
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("福大二手交易商城API")
                .description("福大二手交易商城相关接口")
                .version("1.0")
                .build();
    }

    private List<ApiKey> securitySchemes() {
        //参数1：字段的名字，参数2：字段的键，参数3：参数位置
        return Arrays.asList(new ApiKey("Authorization", "Authorization", "header"));
    }

    //认证的上下文，这里面需要指定哪些接口需要认证
    private List<SecurityContext> securityContexts() {
        SecurityContextBuilder builder = SecurityContext.builder().securityReferences(securityReferences());
        //指定需要认证的path，大写的注意，这里就用到了配置文件里面的URL，需要自己实现path选择的逻辑
        builder.forPaths(forExcludeAntPaths(shopProperties.getLoginInterceptorExcludePath()));
        return Arrays.asList(builder.build());
    }

    /**
     * 匹配登陆拦截器过滤地址
     *
     * @param antPatterns - ant Patterns
     * @return predicate that matches a particular ant pattern
     */
    private Predicate<String> forExcludeAntPaths(final List<String> antPatterns) {
        return (input) -> {
            //使用spring的ant路径配置
            AntPathMatcher matcher = new AntPathMatcher();
            //如果不是白名单的URL，就需要认证
            return !antPatterns.stream().anyMatch(antPattern -> matcher.match(antPattern, input));
        };
    }

    //这个方法是验证的作用域，不能漏了
    private List<SecurityReference> securityReferences() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        return Arrays.asList(
                new SecurityReference("Authorization", new AuthorizationScope[]{authorizationScope}));
    }

}