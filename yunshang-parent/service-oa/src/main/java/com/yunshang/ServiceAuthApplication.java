package com.yunshang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-06 14:26
 */
@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ServiceAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceAuthApplication.class, args);
    }
}
