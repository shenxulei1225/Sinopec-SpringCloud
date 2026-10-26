package com.example.opcuaclient;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * OPC UA客户端应用程序主启动类
 */
@SpringBootApplication
@MapperScan("com.example.opcuaclient.repository")
public class OpcUaClientApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(OpcUaClientApplication.class, args);
    }
}