package com.kabu.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.kabu.admin.**.mapper")
public class KabuAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(KabuAdminApplication.class, args);
    }
}
