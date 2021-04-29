package com.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SecondHandShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecondHandShopApplication.class, args);
    }

}
