package cn.cheers.x.module.dynamicbusiness.service.sop.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * merge 后的生效配置：动作树节点 + 按节点参数。
 */
@Data
public class SopEffectiveConfig {

    private List<SopActionTreeNode> nodes;
    private Map<String, Map<String, Object>> paramsByNode = new LinkedHashMap<>();
}
