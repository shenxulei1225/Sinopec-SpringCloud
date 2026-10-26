package com.example.opcuaclient.service;

import com.example.opcuaclient.config.OpcUaConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.WriteValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * OPC UA客户端服务类
 */
@Data
@Slf4j
@Service
public class OpcUaClientService {

    @Autowired
    private OpcUaConfig config;

    private OpcUaClient client;

    /**
     * 初始化OPC UA客户端
     */
    @PostConstruct
    public void init() {
        try{
            connect();
            log.info("OPC UA客户端初始化完成");
        }catch (Exception e)
        {
            log.info("OPC UA客户端初始化完成，opc-server未连接");
        }
    }

    /**
     * 连接到OPC UA服务器
     */
    public void connect() throws Exception {
        if (client != null && isConnected()) {
            log.info("客户端已连接");
            return;
        }
        try {
            // 使用最简单的create方式，根据URL自动解析endpoint
            client = OpcUaClient.create(config.getServerUrl());
            // 连接
            CompletableFuture<org.eclipse.milo.opcua.sdk.client.api.UaClient> future = client.connect();
            client = (OpcUaClient) future.get(config.getConnectTimeout(), TimeUnit.MILLISECONDS);
            log.info("成功连接到OPC UA服务器: {}", config.getServerUrl());
        } catch (Exception e) {
            log.error("连接失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 断开连接
     */
    @PreDestroy
    public void disconnect() {
        if (client != null) {
            try {
                CompletableFuture<OpcUaClient> future = client.disconnect();
                future.get(5, TimeUnit.SECONDS);
                log.info("OPC UA客户端已断开连接");
            } catch (Exception e) {
                log.error("断开连接时发生错误: {}", e.getMessage(), e);
            }
        }
    }

    public DataValue readValue(String nodeId) throws Exception {
        return readValue(NodeId.parse(nodeId));
    }

    public DataValue readValue(NodeId nodeId) throws Exception {
        if (client == null || !isConnected()) {
            connect();
        }

        List<NodeId> nodeIds = Arrays.asList(nodeId);
        CompletableFuture<List<DataValue>> future = client.read(config.getRequestTimeout(), TimestampsToReturn.Both, nodeIds, Arrays.asList(AttributeId.Value.uid()));
        List<DataValue> values = future.get(config.getRequestTimeout(), TimeUnit.MILLISECONDS);
        return values.get(0);
    }

    public void writeValue(String nodeId, Object value) throws Exception {
        writeValue(NodeId.parse(nodeId), value);
    }

    public void writeValue(NodeId nodeId, Object value) throws Exception {
        if (client == null || !isConnected()) {
            connect();
        }
        // 类型适配：将字符串数字/数值转为Integer(Int32)
        Object toWrite = coerceToInt32(value);
        DataValue dataValue = new DataValue(new Variant(toWrite));
        WriteValue wv = new WriteValue(nodeId, AttributeId.Value.uid(), null, dataValue);
        CompletableFuture<org.eclipse.milo.opcua.stack.core.types.structured.WriteResponse> future = client.write(Arrays.asList(wv));
        org.eclipse.milo.opcua.stack.core.types.structured.WriteResponse resp = future.get(config.getRequestTimeout(), TimeUnit.MILLISECONDS);
        org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode[] results = resp.getResults();
        if (results.length == 0 || !results[0].isGood()) {
            throw new RuntimeException("写入节点值失败: " + (results.length > 0 ? results[0] : "UNKNOWN"));
        }
        log.info("成功写入节点值: {} = {}", nodeId, toWrite);
    }

    private Object coerceToInt32(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        String s = value.toString().trim();
        if (s.matches("[-+]?\\d+")) {
            return Integer.parseInt(s);
        }
        // 非数字则原样返回（可能由服务器进行类型转换或报错）
        return value;
    }

    public String getServerInfo() throws Exception {
        if (client == null || !isConnected()) {
            connect();
        }
        DataValue serverTime = readValue(Identifiers.Server_ServerStatus_CurrentTime);
        return "服务器时间: " + serverTime.getValue().getValue();
    }

    public boolean isConnected() {
        try {
            return client != null && client.getSession() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public OpcUaClient getClient() {
        return client;
    }

    public static void browseAllNodes(OpcUaClient client, NodeId startingNode) {
        browseRecursive(client, startingNode, 0);
    }

    /**
     * 浏览所有节点并获取其URL和值
     * @param client OPC UA客户端
     * @param serverUrl 服务器URL
     */
    public static void browseAllNodesWithValues(OpcUaClient client, String serverUrl) {
        try {
            // 从Objects文件夹开始浏览，这通常包含了服务器上的所有设备和标签
            NodeId objectsFolder = Identifiers.ObjectsFolder;
            log.info("开始浏览所有节点，从 {} 开始...", objectsFolder.toParseableString());
            browseRecursiveWithValues(client, objectsFolder, 0, serverUrl);
            log.info("节点浏览完成");
        } catch (Exception e) {
            log.error("浏览节点失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 浏览指定节点下的所有子节点并获取其URL和值
     * @param client OPC UA客户端
     * @param nodeIdStr 指定节点的NodeId字符串
     * @param serverUrl 服务器URL
     */
    public static void browseNodeWithChildrenValues(OpcUaClient client, String nodeIdStr, String serverUrl) {
        try {
            // 解析NodeId
            NodeId targetNode = NodeId.parse(nodeIdStr);
            log.info("开始浏览节点 {} 下的所有子节点...", nodeIdStr);
            browseRecursiveWithValues(client, targetNode, 0, serverUrl);
            log.info("节点浏览完成");
        } catch (Exception e) {
            log.error("浏览节点失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 递归浏览节点并获取其URL和值
     * 确保递归到所有层级的子节点
     */
    private static void browseRecursiveWithValues(OpcUaClient client, NodeId nodeId, int level, String serverUrl) {
        try {
            List<ReferenceDescription> refs = browseNode(client, nodeId);
            log.info("在节点 {} 发现 {} 个子节点", nodeId.toParseableString(), refs.size());

            for (ReferenceDescription ref : refs) {
                // 获取节点ID的字符串表示
                String nodeIdStr = ref.getNodeId().toParseableString();
                // 构建节点URL
                String nodeUrl = serverUrl + "/" + nodeIdStr;

                // 打印节点信息（带缩进表示层级）
                String indent = "  ".repeat(level);
                log.info("{}{} (类型: {}) - NodeId: {}",
                        indent,
                        ref.getBrowseName().getName(),
                        ref.getNodeClass(),
                        nodeIdStr);
                log.info("{}URL: {}", indent, nodeUrl);

                // 尝试将ReferenceDescription中的NodeId转换为实际的NodeId对象
                NodeId childNodeId = null;
                try {
                    childNodeId = ref.getNodeId().toNodeId(client.getNamespaceTable()).orElse(null);
                    if (childNodeId == null) {
                        log.warn("{}无法转换节点ID: {}", indent, nodeIdStr);
                    } else {
                        // 对所有节点尝试读取值
                        try {
                            DataValue value = client.readValue(0.0, TimestampsToReturn.Both, childNodeId).get(5000, TimeUnit.MILLISECONDS);
                            if (value != null) {
                                log.info("{}DataValue: ", indent);
                                if (value.getValue() != null) {
                                    Variant variant = value.getValue();
                                    log.info("{}  Value: {} (类型: {})",
                                            indent,
                                            variant.getValue(),
                                            variant.getValue().getClass().getSimpleName());
                                } else {
                                    log.info("{}  Value: [null或无效]", indent);
                                }
                                log.info("{}  StatusCode: {}", indent, value.getStatusCode());
                                // 尝试使用可能的替代方法名获取时间戳
                                log.info("{}  SourceTimestamp: {}", indent, value.getSourceTime() != null ? value.getSourceTime().toString() : "[null]");
                                log.info("{}  ServerTimestamp: {}", indent, value.getServerTime() != null ? value.getServerTime().toString() : "[null]");
                            } else {
                                log.info("{}DataValue: [null]", indent);
                            }
                        } catch (Exception e) {
                            log.error("{}读取DataValue失败: {}", indent, e.getMessage(), e);
                        }

                        // 无论节点类型是什么，只要成功转换了节点ID，就尝试递归浏览
                        // 确保递归到所有层级
                        browseRecursiveWithValues(client, childNodeId, level + 1, serverUrl);
                    }
                } catch (Exception e) {
                    log.warn("{}转换节点ID {} 失败: {}", indent, nodeIdStr, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("浏览节点 {} 失败: {}", nodeId.toParseableString(), e.getMessage(), e);
        }
    }

    private static void browseRecursive(OpcUaClient client, NodeId nodeId, int level) {
        try {
            List<ReferenceDescription> refs = browseNode(client, nodeId);

            for (ReferenceDescription ref : refs) {
                // 打印节点信息（带缩进表示层级）
            log.info("{}{}({})",
                    "  ".repeat(level),
                    ref.getReferenceTypeId(),
                    ref.getNodeId().toParseableString());

                // 如果是容器节点则递归浏览
                if (ref.getNodeClass() == NodeClass.Object ||
                        ref.getNodeClass() == NodeClass.View) {
                    browseRecursive(client, ref.getReferenceTypeId(), level + 1);
                }
            }
        } catch (Exception e) {
            log.error("浏览节点 {} 失败: {}", nodeId.toParseableString(), e.getMessage(), e);
        }
    }

    /**
     * 打印指定NodeId下所有子节点的nodeId和DataValue的value值，递归到最后一层
     * @param client OPC UA客户端
     * @param nodeId 指定的节点ID
     */
    public static void printNodeValuesRecursively(OpcUaClient client, NodeId nodeId) {
        try {
            log.info("开始打印节点 {} 下的所有子节点值...", nodeId.toParseableString());
            // 调用递归方法，从当前节点开始，层级为0
            printNodeValuesRecursive(client, nodeId, 0);
            log.info("节点值打印完成");
        } catch (Exception e) {
            log.error("打印节点值失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 递归打印节点值的辅助方法
     */
    private static void printNodeValuesRecursive(OpcUaClient client, NodeId nodeId, int level) {
        try {
            List<ReferenceDescription> refs = browseNode(client, nodeId);
            String indent = "  ".repeat(level);

            // 打印当前节点信息和值
            log.info("{}{} - NodeId: {}",
                    indent,
                    "当前节点",
                    nodeId.toParseableString());

            // 读取当前节点的值
            try {
                DataValue value = client.readValue(0.0, TimestampsToReturn.Both, nodeId).get(5000, TimeUnit.MILLISECONDS);
                if (value != null) {
                    if (value.getValue() != null) {
                        Variant variant = value.getValue();
                        log.info("{}  Value: {} (类型: {})",
                                indent,
                                variant.getValue(),
                                variant.getValue().getClass().getSimpleName());
                    } else {
                        log.info("{}  Value: [null或无效]", indent);
                    }
                } else {
                    log.info("{}  Value: [null]", indent);
                }
            } catch (Exception e) {
                log.error("{}读取当前节点值失败: {}", indent, e.getMessage(), e);
            }

            // 递归处理所有子节点
            log.info("{}{} 下有 {} 个子节点:", indent, "当前节点", refs.size());
            for (ReferenceDescription ref : refs) {
                String nodeIdStr = ref.getNodeId().toParseableString();
                NodeId childNodeId = null;
                try {
                    childNodeId = ref.getNodeId().toNodeId(client.getNamespaceTable()).orElse(null);
                    if (childNodeId == null) {
                        log.warn("{}无法转换节点ID: {}", indent, nodeIdStr);
                    } else {
                        // 递归打印子节点
                        printNodeValuesRecursive(client, childNodeId, level + 1);
                    }
                } catch (Exception e) {
                    log.warn("{}转换节点ID {} 失败: {}", indent, nodeIdStr, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("浏览节点 {} 失败: {}", nodeId.toParseableString(), e.getMessage(), e);
        }
    }

    public static List<ReferenceDescription> browseNode(OpcUaClient client, NodeId nodeId) throws Exception {
        BrowseDescription browse = new BrowseDescription(
                nodeId,
                BrowseDirection.Forward,
                Identifiers.HierarchicalReferences, // 浏览层级引用
                true, // 包含子类型
                Unsigned.uint(NodeClass.Object.getValue() |
                        NodeClass.Variable.getValue() |
                        NodeClass.Method.getValue()), // 浏览对象、变量和方法
                Unsigned.uint(0) // 返回数量无限制
        );

        BrowseResult result = client.browse(browse).get();
        if (result.getStatusCode().isGood()) {
            return Arrays.asList(result.getReferences());
        }
        throw new RuntimeException("浏览失败: " + result.getStatusCode());
    }

    public static void browseTags(OpcUaClient client, NodeId parentNode, int level) throws Exception {
        // 浏览当前节点下的所有子节点
        List<ReferenceDescription> refs = browseNode(client, parentNode);

        for (ReferenceDescription ref : refs) {
            String indent = "  ".repeat(level);
            log.info("{}{} [{}]",
                    indent,
                    ref.getBrowseName().getName(),
                    ref.getNodeClass());

            // 如果是容器节点（PLC设备），继续递归
            if (ref.getNodeClass() == NodeClass.Object) {
                browseTags(client, ref.getReferenceTypeId(), level + 1);
            }
            // 如果是变量节点（标签），直接读取值
            else if (ref.getNodeClass() == NodeClass.Variable) {
                try {
                    DataValue value = client.readValue(0.0, TimestampsToReturn.Both, ref.getReferenceTypeId()).get();
                    log.info("{}  VALUE: {}",
                            indent,
                            value.getValue().getValue());
                } catch (Exception e) {
                    log.info("{}  [读取失败] {}",
                            indent,
                            e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        OpcUaClient client = null;
        String url = args.length > 0 ? args[0] : "opc.tcp://192.168.10.16:4990/FactoryTalkLinxGateway1/";

        try {
            // 使用最简单的create方式，根据URL自动解析endpoint
            client = OpcUaClient.create(url);
            // 连接
            client.connect().get();
            log.info("成功连接到OPC UA服务器: {}", url);

            // 测试新方法: 打印指定节点下所有子节点的值
            try {
                NodeId targetNode = NodeId.parse("ns=2;s=FactoryTalkLinxGateway"); // 用户指定的节点
                printNodeValuesRecursively(client, targetNode);
            } catch (Exception e) {
                log.error("打印节点值失败: {}", e.getMessage(), e);
            }

        } catch (Exception e) {
            log.error("连接或浏览失败: {}", e.getMessage(), e);
        } finally {
            // 释放资源
            if (client != null) {
                try {
                    client.disconnect().get(5, TimeUnit.SECONDS);
                    log.info("OPC UA客户端已断开连接");
                } catch (Exception e) {
                    log.error("断开连接时发生错误: {}", e.getMessage(), e);
                }
            }
        }
    }
}
