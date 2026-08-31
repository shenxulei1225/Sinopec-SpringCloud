package cn.cheers.x.module.dynamicbusiness.service.sop.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * SOP 动作树节点（action_tree_json 元素）。
 */
@Data
public class SopActionTreeNode {

    private String nodeKey;
    private String actionId;
    private Integer order;
    private String parentNodeKey;
    private List<String> paramSlots = new ArrayList<>();
}
