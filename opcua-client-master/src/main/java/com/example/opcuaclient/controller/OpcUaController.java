package com.example.opcuaclient.controller;

import com.example.opcuaclient.service.OpcUaClientService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * OPC UA操作控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/opcua")
public class OpcUaController {

    @Autowired
    private OpcUaClientService opcUaClientService;

    /**
     * 检查连接状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("connected", opcUaClientService.isConnected());
        response.put("timestamp", System.currentTimeMillis());
        
        if (opcUaClientService.isConnected()) {
            response.put("status", "已连接");
        } else {
            response.put("status", "未连接");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 连接到OPC UA服务器
     */
    @PostMapping("/connect")
    public ResponseEntity<Map<String, Object>> connect() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            opcUaClientService.connect();
            response.put("success", true);
            response.put("message", "连接成功");
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("连接失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "连接失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 断开连接
     */
    @PostMapping("/disconnect")
    public ResponseEntity<Map<String, Object>> disconnect() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            opcUaClientService.disconnect();
            response.put("success", true);
            response.put("message", "断开连接成功");
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("断开连接失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "断开连接失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 读取节点值
     */
    @GetMapping("/read/{nodeId}")
    public ResponseEntity<Map<String, Object>> readValue(@PathVariable String nodeId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            DataValue dataValue = opcUaClientService.readValue(nodeId);
            
            response.put("success", true);
            response.put("nodeId", nodeId);
            response.put("value", dataValue.getValue().getValue());
            response.put("sourceTime", dataValue.getSourceTime().getJavaTime());
            response.put("serverTime", dataValue.getServerTime().getJavaTime());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("读取节点值失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("nodeId", nodeId);
            response.put("message", "读取失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 写入节点值
     */
    @PostMapping("/write/{nodeId}")
    public ResponseEntity<Map<String, Object>> writeValue(
            @PathVariable String nodeId,
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        Object value = request.get("value");
        
        if (value == null) {
            response.put("success", false);
            response.put("message", "缺少value参数");
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            opcUaClientService.writeValue(nodeId, value);
            
            response.put("success", true);
            response.put("nodeId", nodeId);
            response.put("value", value);
            response.put("message", "写入成功");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("写入节点值失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("nodeId", nodeId);
            response.put("value", value);
            response.put("message", "写入失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取服务器信息
     */
    @GetMapping("/server-info")
    public ResponseEntity<Map<String, Object>> getServerInfo() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String serverInfo = opcUaClientService.getServerInfo();
            
            response.put("success", true);
            response.put("serverInfo", serverInfo);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取服务器信息失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "获取服务器信息失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 批量读取节点值
     */
    @PostMapping("/batch-read")
    public ResponseEntity<Map<String, Object>> batchRead(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        java.util.List<String> nodeIds = (java.util.List<String>) request.get("nodeIds");
        
        if (nodeIds == null || nodeIds.isEmpty()) {
            response.put("success", false);
            response.put("message", "缺少nodeIds参数");
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(response);
        }
        
        Map<String, Object> results = new HashMap<>();
        boolean hasError = false;
        
        for (String nodeId : nodeIds) {
            try {
                DataValue dataValue = opcUaClientService.readValue(nodeId);
                Map<String, Object> nodeResult = new HashMap<>();
                nodeResult.put("value", dataValue.getValue().getValue());
                nodeResult.put("sourceTime", dataValue.getSourceTime().getJavaTime());
                nodeResult.put("serverTime", dataValue.getServerTime().getJavaTime());
                nodeResult.put("success", true);
                results.put(nodeId, nodeResult);
            } catch (Exception e) {
                log.error("读取节点 {} 失败: {}", nodeId, e.getMessage());
                Map<String, Object> nodeResult = new HashMap<>();
                nodeResult.put("success", false);
                nodeResult.put("error", e.getMessage());
                results.put(nodeId, nodeResult);
                hasError = true;
            }
        }
        
        response.put("success", !hasError);
        response.put("results", results);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 递归打印指定节点下所有子节点的值
     */
    @GetMapping("/browse/{nodeIdStr}")
    public ResponseEntity<Map<String, Object>> browseNodeRecursively(@PathVariable String nodeIdStr) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!opcUaClientService.isConnected()) {
                response.put("success", false);
                response.put("message", "OPC UA客户端未连接");
                return ResponseEntity.status(400).body(response);
            }

            NodeId nodeId = NodeId.parse(nodeIdStr);
            log.info("开始递归浏览节点: {}", nodeIdStr);

            // 调用递归打印方法
            OpcUaClientService.printNodeValuesRecursively(opcUaClientService.getClient(), nodeId);

            response.put("success", true);
            response.put("nodeId", nodeIdStr);
            response.put("message", "递归浏览完成，请查看日志获取详细信息");
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("递归浏览节点失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("nodeId", nodeIdStr);
            response.put("message", "递归浏览失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(response);
        }
    }
}