package com.opus.erp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Opus ERP 应用启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.opus.erp")
@MapperScan("com.opus.erp.**.mapper")
public class OpusErpApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpusErpApplication.class, args);
    }
}
