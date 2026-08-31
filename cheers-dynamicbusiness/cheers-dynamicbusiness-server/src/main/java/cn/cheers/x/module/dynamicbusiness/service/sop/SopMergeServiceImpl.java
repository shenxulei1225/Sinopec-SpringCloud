package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopActionTreeNode;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopEffectiveConfig;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopMergeResult;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopTemplateSnapshot;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopTreeOverride;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SOP merge 实现（动作树 + 按节点参数）。
 *
 * <p><b>权威</b>：生效配置 = merge(模板动作树/按节点参数, 树差量, 参数差量)。</p>
 * <p><b>禁止</b>：静默补参、解析业务巡检点 / 停靠站、跨实例共享参数。</p>
 */
@Service
public class SopMergeServiceImpl implements SopMergeService {

    @Override
    public SopMergeResult merge(SopTemplateSnapshot template,
                                SopTreeOverride treeOverride,
                                Map<String, Map<String, Object>> paramOverride,
                                boolean requireParamValues) {
        if (template == null) {
            return SopMergeResult.failure(List.of("MISSING_TEMPLATE"));
        }
        List<SopActionTreeNode> nodes = resolveEffectiveNodes(template, treeOverride);
        Map<String, Map<String, Object>> paramsByNode = mergeParamsByNode(template, nodes, paramOverride);
        if (requireParamValues) {
            List<String> gapCodes = validateRequiredParams(nodes, paramsByNode);
            if (!gapCodes.isEmpty()) {
                return SopMergeResult.failure(gapCodes);
            }
        }
        SopEffectiveConfig effective = new SopEffectiveConfig();
        effective.setNodes(nodes);
        effective.setParamsByNode(paramsByNode);
        return SopMergeResult.success(effective);
    }

    private List<SopActionTreeNode> resolveEffectiveNodes(SopTemplateSnapshot template,
                                                          SopTreeOverride treeOverride) {
        List<SopActionTreeNode> source;
        if (treeOverride != null
                && treeOverride.getReplaceTree() != null
                && !treeOverride.getReplaceTree().isEmpty()) {
            source = treeOverride.getReplaceTree();
        } else {
            source = template.getActionTree() != null ? template.getActionTree() : List.of();
        }
        List<SopActionTreeNode> result = new ArrayList<>(source.size());
        for (int i = 0; i < source.size(); i++) {
            SopActionTreeNode node = source.get(i);
            SopActionTreeNode copy = new SopActionTreeNode();
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
            SopTemplateSnapshot template,
            List<SopActionTreeNode> nodes,
            Map<String, Map<String, Object>> paramOverride) {
        Map<String, Map<String, Object>> templateParams =
                template.getParamsByNode() != null ? template.getParamsByNode() : Map.of();
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        for (SopActionTreeNode node : nodes) {
            Map<String, Object> merged = new LinkedHashMap<>();
            Map<String, Object> defaults = templateParams.get(node.getNodeKey());
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

    private List<String> validateRequiredParams(List<SopActionTreeNode> nodes,
                                                Map<String, Map<String, Object>> paramsByNode) {
        List<String> gapCodes = new ArrayList<>();
        for (SopActionTreeNode node : nodes) {
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
