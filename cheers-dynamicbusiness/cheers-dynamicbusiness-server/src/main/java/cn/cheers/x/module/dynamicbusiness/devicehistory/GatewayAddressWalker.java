package cn.cheers.x.module.dynamicbusiness.devicehistory;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseResultMask;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

/**
 * 从 Gateway 已有的地址空间往下看变量节点。
 * 节点标识只用服务器返回的。禁止按标签名拼一个。
 */
public final class GatewayAddressWalker {

    private static final Logger log = LoggerFactory.getLogger(GatewayAddressWalker.class);
    private static final int MAX_DEPTH = 8;
    private static final int MAX_NODES = 200_000;
    private static final String KNOWN_ROOT = "ns=2;s=FactoryTalkLinxGateway";

    private GatewayAddressWalker() {
    }

    /**
     * 这次会话里能看到的变量。连不上或浏览失败时抛出，调用方重试，不订空名单。
     */
    public static List<GatewaySeenNode> walk(OpcUaClient client) throws Exception {
        NodeId start = startNode(client);
        List<GatewaySeenNode> seen = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        walkLevel(client, start, List.of(), 0, seen, visited);
        log.info("Gateway 浏览到 {} 个变量节点", seen.size());
        return seen;
    }

    private static NodeId startNode(OpcUaClient client) throws Exception {
        try {
            NodeId known = NodeId.parse(KNOWN_ROOT);
            if (!children(client, known).isEmpty()) {
                return known;
            }
        } catch (Exception ex) {
            log.info("Gateway 上打不开 {}：{}", KNOWN_ROOT, ex.getMessage());
        }
        log.info("改从对象文件夹往下看");
        return Identifiers.ObjectsFolder;
    }

    private static void walkLevel(OpcUaClient client, NodeId nodeId, List<String> ancestors, int depth,
                                  List<GatewaySeenNode> seen, Set<String> visited) throws Exception {
        if (depth > MAX_DEPTH || visited.size() >= MAX_NODES) {
            return;
        }
        String here = nodeId.toParseableString();
        if (!visited.add(here)) {
            return;
        }
        for (ReferenceDescription ref : children(client, nodeId)) {
            keep(client, ref, ancestors, depth, seen, visited);
        }
    }

    private static void keep(OpcUaClient client, ReferenceDescription ref, List<String> ancestors, int depth,
                             List<GatewaySeenNode> seen, Set<String> visited) throws Exception {
        NodeId child = toNodeId(client, ref.getNodeId());
        if (child == null) {
            return;
        }
        String name = ref.getBrowseName() == null ? "" : ref.getBrowseName().getName();
        if (ref.getNodeClass() == NodeClass.Variable) {
            seen.add(new GatewaySeenNode(child.toParseableString(), name, ancestors));
        }
        List<String> next = new ArrayList<>(ancestors);
        if (!name.isEmpty()) {
            next.add(name);
        }
        walkLevel(client, child, next, depth + 1, seen, visited);
    }

    private static NodeId toNodeId(OpcUaClient client, ExpandedNodeId expanded) {
        if (expanded == null) {
            return null;
        }
        return expanded.toNodeId(client.getNamespaceTable()).orElse(null);
    }

    private static List<ReferenceDescription> children(OpcUaClient client, NodeId nodeId) throws Exception {
        BrowseDescription browse = new BrowseDescription(
                nodeId,
                BrowseDirection.Forward,
                Identifiers.HierarchicalReferences,
                true,
                uint(NodeClass.Object.getValue() | NodeClass.Variable.getValue()),
                uint(BrowseResultMask.All.getValue()));
        BrowseResult result = client.browse(browse).get(10, TimeUnit.SECONDS);
        List<ReferenceDescription> refs = new ArrayList<>();
        collect(client, result, refs);
        return refs;
    }

    private static void collect(OpcUaClient client, BrowseResult result, List<ReferenceDescription> refs) throws Exception {
        if (result == null) {
            return;
        }
        if (result.getReferences() != null) {
            refs.addAll(List.of(result.getReferences()));
        }
        ByteString more = result.getContinuationPoint();
        if (more == null || more.isNull()) {
            return;
        }
        BrowseResult next = client.browseNext(false, more).get(10, TimeUnit.SECONDS);
        collect(client, next, refs);
    }
}
