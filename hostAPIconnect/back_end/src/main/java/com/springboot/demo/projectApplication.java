package com.springboot.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

//@SpringBootApplication

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan("com.springboot.demo.dao")
public class projectApplication{
    public static void main(String[] args) {
        SpringApplication.run(projectApplication.class,args);

    }
}
