package com.owen;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.owen.mapper")
public class ShiroSpringbootApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShiroSpringbootApplication.class, args);
    }
}
