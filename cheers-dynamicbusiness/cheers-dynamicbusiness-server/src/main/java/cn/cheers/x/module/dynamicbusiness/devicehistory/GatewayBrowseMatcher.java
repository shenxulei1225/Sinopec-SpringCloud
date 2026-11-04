package cn.cheers.x.module.dynamicbusiness.devicehistory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 把浏览到的节点对上点位对照。
 * 浏览名是完整点位编码，或浏览名是完整标签名并且挂在这个分区下，才算对上。
 * 对上不止一个，或一个都对不上，这个点不订。禁止编一个节点标识。
 */
public final class GatewayBrowseMatcher {

    /** 旧采集程序导入标签时，快捷方式名是分区编号加这一段。文件夹叫这个名字也算这个分区。 */
    static final String SHORTCUT_SUFFIX = "_ACU";

    private GatewayBrowseMatcher() {
    }

    /**
     * 返回能唯一对上的节点。键是浏览得到的节点标识。
     */
    public static Map<String, GatewayPointRef> match(List<GatewayPointRef> points, List<GatewaySeenNode> nodes) {
        Map<String, List<GatewayPointRef>> hitsByNode = new LinkedHashMap<>();
        for (GatewaySeenNode node : nodes) {
            List<GatewayPointRef> hits = hits(points, node);
            if (hits.size() == 1) {
                hitsByNode.computeIfAbsent(node.getNodeId(), key -> new ArrayList<>()).add(hits.get(0));
            }
        }
        return unique(hitsByNode);
    }

    private static List<GatewayPointRef> hits(List<GatewayPointRef> points, GatewaySeenNode node) {
        List<GatewayPointRef> hits = new ArrayList<>();
        for (GatewayPointRef point : points) {
            if (belongs(point, node)) {
                hits.add(point);
            }
        }
        return hits;
    }

    private static Map<String, GatewayPointRef> unique(Map<String, List<GatewayPointRef>> hitsByNode) {
        Map<String, Integer> pointHits = new LinkedHashMap<>();
        for (List<GatewayPointRef> hits : hitsByNode.values()) {
            if (hits.size() == 1) {
                pointHits.merge(hits.get(0).getBindingCode(), 1, Integer::sum);
            }
        }
        Map<String, GatewayPointRef> unique = new LinkedHashMap<>();
        for (Map.Entry<String, List<GatewayPointRef>> entry : hitsByNode.entrySet()) {
            if (entry.getValue().size() != 1) {
                continue;
            }
            GatewayPointRef point = entry.getValue().get(0);
            if (pointHits.get(point.getBindingCode()) == 1) {
                unique.put(entry.getKey(), point);
            }
        }
        return unique;
    }

    private static boolean belongs(GatewayPointRef point, GatewaySeenNode node) {
        if (point.getBindingCode().equals(node.getBrowseName())) {
            return true;
        }
        if (!point.getTagName().equals(node.getBrowseName())) {
            return false;
        }
        if (idHasCode(node.getNodeId(), point.getBindingCode())) {
            return true;
        }
        for (String ancestor : node.getAncestors()) {
            if (ancestor.equals(point.getZoneCode()) || ancestor.equals(point.getZoneCode() + SHORTCUT_SUFFIX)) {
                return true;
            }
        }
        return false;
    }

    private static boolean idHasCode(String nodeId, String code) {
        if (nodeId == null || code == null || code.isEmpty()) {
            return false;
        }
        int at = nodeId.indexOf(code);
        if (at < 0) {
            return false;
        }
        int end = at + code.length();
        if (end < nodeId.length()) {
            char next = nodeId.charAt(end);
            if (Character.isLetterOrDigit(next) || next == '_') {
                return false;
            }
        }
        return true;
    }
}
