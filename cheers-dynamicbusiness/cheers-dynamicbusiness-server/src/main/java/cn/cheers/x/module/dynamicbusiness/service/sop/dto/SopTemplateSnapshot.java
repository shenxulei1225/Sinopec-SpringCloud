package cn.cheers.x.module.dynamicbusiness.service.sop.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 读 merge 用的 SOP 模板快照（动作树 + 按节点默认参数）。
 */
@Data
public class SopTemplateSnapshot {

    private List<SopActionTreeNode> actionTree;
    private Map<String, Map<String, Object>> paramsByNode = new LinkedHashMap<>();
}
