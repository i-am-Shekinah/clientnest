package com.clientnest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.clientnest")
public class ClientnestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientnestApplication.class, args);
    }
}
