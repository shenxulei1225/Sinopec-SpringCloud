package cn.cheers.x.module.dynamicbusiness.devicehistory;

import java.util.List;

/**
 * Gateway 这次浏览到的一个变量节点。
 * 节点标识来自服务器。本对象不编标识。
 */
public class GatewaySeenNode {

    private final String nodeId;
    private final String browseName;
    private final List<String> ancestors;

    public GatewaySeenNode(String nodeId, String browseName, List<String> ancestors) {
        this.nodeId = nodeId;
        this.browseName = browseName;
        this.ancestors = List.copyOf(ancestors);
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getBrowseName() {
        return browseName;
    }

    public List<String> getAncestors() {
        return ancestors;
    }
}
