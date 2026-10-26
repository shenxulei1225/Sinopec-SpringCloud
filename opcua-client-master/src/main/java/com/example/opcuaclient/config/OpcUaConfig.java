package com.example.opcuaclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OPC UA连接配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "opcua")
public class OpcUaConfig {
    
    /**
     * OPC UA服务器地址
     */
    private String serverUrl = "opc.tcp://localhost:4840/freeopcua/server/";
    
    /**
     * 连接超时时间（毫秒）
     */
    private int connectTimeout = 10000;
    
    /**
     * 请求超时时间（毫秒）
     */
    private int requestTimeout = 5000;
    
    /**
     * 是否启用安全连接
     */
    private boolean securityEnabled = false;
    
    /**
     * 用户名（如果服务器需要认证）
     */
    private String username;
    
    /**
     * 密码（如果服务器需要认证）
     */
    private String password;
    
    /**
     * 应用程序名称
     */
    private String applicationName = "OPC UA Client";
    
    /**
     * 应用程序URI
     */
    private String applicationUri = "urn:opcua:client:example";
} 