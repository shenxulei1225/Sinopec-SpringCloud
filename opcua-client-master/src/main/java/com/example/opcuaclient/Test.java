package com.example.opcuaclient;

import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

import java.util.List;

/**
 * @author chunhao.guo
 * @since 2025-08-08
 */
public class Test {

    public static void main(String[] args) {
        try {
            // 发现服务器所有端点
            List<EndpointDescription> endpoints = DiscoveryClient
                    .getEndpoints("opc.tcp://192.168.10.16:4990/FactoryTalkLinxGateway1/")
                    .get();

            endpoints.forEach(endpoint -> {
                System.out.println("端点URL: " + endpoint.getEndpointUrl());
                System.out.println("安全模式: " + endpoint.getSecurityMode());
                System.out.println("安全策略: " + endpoint.getSecurityPolicyUri());
            });
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
