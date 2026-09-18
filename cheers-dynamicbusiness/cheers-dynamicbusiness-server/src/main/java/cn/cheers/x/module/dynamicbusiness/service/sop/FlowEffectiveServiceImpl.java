package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.FlowActionTreeNode;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.FlowMergeResult;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.FlowStandardSnapshot;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 标准流程生效配置查询实现。
 *
 * <p><b>权威</b>：当前流程行的 {@code action_tree_json} 与 {@code default_params_by_node_json}。</p>
 * <p><b>禁止</b>：依赖模板/实例双轨字段；在读路径补一份假配置。</p>
 */
@Service
public class FlowEffectiveServiceImpl implements FlowEffectiveService {

    @Resource
    private EntityService entityService;

    @Resource
    private FlowMergeService flowMergeService;

    @Override
    public FlowMergeResult getEffective(long flowId) {
        EntityRespVO row = requireFlow(flowId);
        Map<String, Object> base = emptyIfNull(row.getBaseFields());
        FlowStandardSnapshot snapshot = toStandardSnapshot(base);
        return flowMergeService.merge(snapshot, null, null, false);
    }

    private EntityRespVO requireFlow(long flowId) {
        EntityRespVO row = entityService.get(flowId, FlowFieldCodes.ENTITY_TYPE_CODE);
        if (row == null || row.getId() == null) {
            throw new ServiceException(404, "标准流程不存在：" + flowId);
        }
        return row;
    }

    private FlowStandardSnapshot toStandardSnapshot(Map<String, Object> base) {
        FlowStandardSnapshot snapshot = new FlowStandardSnapshot();
        snapshot.setActionTree(parseActionTree(base.get(FlowFieldCodes.ACTION_TREE_JSON)));
        snapshot.setParamsByNode(parseParamsByNode(base.get(FlowFieldCodes.DEFAULT_PARAMS_BY_NODE_JSON)));
        return snapshot;
    }

    private List<FlowActionTreeNode> parseActionTree(Object raw) {
        if (raw == null) {
            return new ArrayList<>();
        }
        if (raw instanceof List<?> list) {
            return JSON.parseObject(JSON.toJSONString(list), new TypeReference<List<FlowActionTreeNode>>() {
            });
        }
        if (raw instanceof String s) {
            if (!StringUtils.hasText(s) || "null".equalsIgnoreCase(s.trim())) {
                return new ArrayList<>();
            }
            return JSON.parseObject(s, new TypeReference<List<FlowActionTreeNode>>() {
            });
        }
        return JSON.parseObject(JSON.toJSONString(raw), new TypeReference<List<FlowActionTreeNode>>() {
        });
    }

    private Map<String, Map<String, Object>> parseParamsByNode(Object raw) {
        if (raw == null) {
            return new LinkedHashMap<>();
        }
        if (raw instanceof Map<?, ?> map) {
            Map<String, Map<String, Object>> out = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : map.entrySet()) {
                if (e.getKey() == null) {
                    continue;
                }
                Object val = e.getValue();
                if (val instanceof Map<?, ?> nested) {
                    Map<String, Object> nodeParams = new LinkedHashMap<>();
                    for (Map.Entry<?, ?> ne : nested.entrySet()) {
                        if (ne.getKey() != null) {
                            nodeParams.put(String.valueOf(ne.getKey()), ne.getValue());
                        }
                    }
                    out.put(String.valueOf(e.getKey()), nodeParams);
                }
            }
            return out;
        }
        if (raw instanceof String s) {
            if (!StringUtils.hasText(s) || "null".equalsIgnoreCase(s.trim())) {
                return new LinkedHashMap<>();
            }
            Map<String, Map<String, Object>> parsed = JSON.parseObject(s,
                    new TypeReference<Map<String, Map<String, Object>>>() {
                    });
            return parsed != null ? parsed : new LinkedHashMap<>();
        }
        Map<String, Map<String, Object>> parsed = JSON.parseObject(JSON.toJSONString(raw),
                new TypeReference<Map<String, Map<String, Object>>>() {
                });
        return parsed != null ? parsed : new LinkedHashMap<>();
    }

    private Map<String, Object> emptyIfNull(Map<String, Object> map) {
        return map != null ? map : Map.of();
    }
}
