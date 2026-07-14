package cn.cheers.x.module.platform.routing.planner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ShortestPathResult {

    /**
     * 规划路径上的节点 id。单网时为本地 nodeId；多网联程超级图为 {@code networkRef::localNodeId}。
     */
    private List<String> nodeIds;
    private List<String> edgeIds;
    private double totalCost;

    /**
     * 与 {@link #nodeIds} 同索引：各节点所属 networkRef（多网联程时有值）。
     */
    private List<String> networkRefsByNode;

    /**
     * 与 {@link #nodeIds} 同索引：超级图 id 中的本地 nodeId（多网联程时有值）。
     */
    private List<String> localNodeIds;

    public static final String NODE_REF_SEPARATOR = "::";

    public static String prefixNodeId(String networkRef, String localNodeId) {
        return networkRef + NODE_REF_SEPARATOR + localNodeId;
    }

    public static String networkRefFromNodeId(String nodeId) {
        if (!StringUtils.hasText(nodeId)) {
            return null;
        }
        int idx = nodeId.indexOf(NODE_REF_SEPARATOR);
        if (idx < 0) {
            return null;
        }
        return nodeId.substring(0, idx);
    }

    public static String localNodeId(String nodeId) {
        if (!StringUtils.hasText(nodeId)) {
            return nodeId;
        }
        int idx = nodeId.indexOf(NODE_REF_SEPARATOR);
        if (idx < 0) {
            return nodeId;
        }
        return nodeId.substring(idx + NODE_REF_SEPARATOR.length());
    }

    public static boolean isPrefixedNodeId(String nodeId) {
        return StringUtils.hasText(nodeId) && nodeId.contains(NODE_REF_SEPARATOR);
    }

    public ShortestPathResult withMultimodalNodeMapping() {
        if (nodeIds == null || nodeIds.isEmpty() || !isPrefixedNodeId(nodeIds.get(0))) {
            return this;
        }
        List<String> refs = new ArrayList<>(nodeIds.size());
        List<String> locals = new ArrayList<>(nodeIds.size());
        for (String nodeId : nodeIds) {
            refs.add(networkRefFromNodeId(nodeId));
            locals.add(localNodeId(nodeId));
        }
        return toBuilder()
                .networkRefsByNode(refs)
                .localNodeIds(locals)
                .build();
    }
}
