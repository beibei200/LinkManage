package com.beibei.linkmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.beibei.linkmanagement.mapper")
public class LinkManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(LinkManagementApplication.class, args);
    }
} 