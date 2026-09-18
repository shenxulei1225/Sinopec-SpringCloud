package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.service.sop.dto.FlowActionTreeNode;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.FlowEffectiveConfig;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.FlowMergeResult;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.FlowStandardSnapshot;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.FlowTreeOverride;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 标准流程 merge 实现（动作树 + 按节点参数）。
 *
 * <p><b>权威</b>：生效配置 = merge(标准动作树/按节点参数, 树差量, 参数差量)。</p>
 * <p><b>禁止</b>：静默补参、解析业务巡检点 / 停靠站、跨实例共享参数。</p>
 */
@Service
public class FlowMergeServiceImpl implements FlowMergeService {

    @Override
    public FlowMergeResult merge(FlowStandardSnapshot standard,
                                FlowTreeOverride treeOverride,
                                Map<String, Map<String, Object>> paramOverride,
                                boolean requireParamValues) {
        if (standard == null) {
            return FlowMergeResult.failure(List.of("MISSING_STANDARD_SOP"));
        }
        List<FlowActionTreeNode> nodes = resolveEffectiveNodes(standard, treeOverride);
        Map<String, Map<String, Object>> paramsByNode = mergeParamsByNode(standard, nodes, paramOverride);
        if (requireParamValues) {
            List<String> gapCodes = validateRequiredParams(nodes, paramsByNode);
            if (!gapCodes.isEmpty()) {
                return FlowMergeResult.failure(gapCodes);
            }
        }
        FlowEffectiveConfig effective = new FlowEffectiveConfig();
        effective.setNodes(nodes);
        effective.setParamsByNode(paramsByNode);
        return FlowMergeResult.success(effective);
    }

    private List<FlowActionTreeNode> resolveEffectiveNodes(FlowStandardSnapshot standard,
                                                          FlowTreeOverride treeOverride) {
        List<FlowActionTreeNode> source;
        if (treeOverride != null
                && treeOverride.getReplaceTree() != null
                && !treeOverride.getReplaceTree().isEmpty()) {
            source = treeOverride.getReplaceTree();
        } else {
            source = standard.getActionTree() != null ? standard.getActionTree() : List.of();
        }
        List<FlowActionTreeNode> result = new ArrayList<>(source.size());
        for (int i = 0; i < source.size(); i++) {
            FlowActionTreeNode node = source.get(i);
            FlowActionTreeNode copy = new FlowActionTreeNode();
            copy.setNodeKey(node.getNodeKey());
            copy.setActionId(node.getActionId());
            copy.setOrder(node.getOrder() != null ? node.getOrder() : i + 1);
            copy.setParentNodeKey(node.getParentNodeKey());
            copy.setParamSlots(node.getParamSlots() != null
                    ? new ArrayList<>(node.getParamSlots())
                    : new ArrayList<>());
            result.add(copy);
        }
        return result;
    }

    private Map<String, Map<String, Object>> mergeParamsByNode(
            FlowStandardSnapshot standard,
            List<FlowActionTreeNode> nodes,
            Map<String, Map<String, Object>> paramOverride) {
        Map<String, Map<String, Object>> standardParams =
                standard.getParamsByNode() != null ? standard.getParamsByNode() : Map.of();
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        for (FlowActionTreeNode node : nodes) {
            Map<String, Object> merged = new LinkedHashMap<>();
            Map<String, Object> defaults = standardParams.get(node.getNodeKey());
            if (defaults != null) {
                merged.putAll(defaults);
            }
            if (paramOverride != null) {
                Map<String, Object> override = paramOverride.get(node.getNodeKey());
                if (override != null) {
                    merged.putAll(override);
                }
            }
            result.put(node.getNodeKey(), merged);
        }
        return result;
    }

    private List<String> validateRequiredParams(List<FlowActionTreeNode> nodes,
                                                Map<String, Map<String, Object>> paramsByNode) {
        List<String> gapCodes = new ArrayList<>();
        for (FlowActionTreeNode node : nodes) {
            Map<String, Object> params = paramsByNode.getOrDefault(node.getNodeKey(), Map.of());
            if (node.getParamSlots() == null) {
                continue;
            }
            for (String slot : node.getParamSlots()) {
                if (!isParamValuePresent(params.get(slot))) {
                    gapCodes.add("MISSING_PARAM:" + node.getNodeKey() + ":" + slot);
                }
            }
        }
        return gapCodes;
    }

    private boolean isParamValuePresent(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof String s) {
            return !s.trim().isEmpty();
        }
        return true;
    }
}
