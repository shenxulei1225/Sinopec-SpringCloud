package cn.cheers.x.module.dynamicbusiness.service.sop.dto;

import lombok.Data;

import java.util.List;

/**
 * 实例动作树差量：整树替换。
 */
@Data
public class SopTreeOverride {

    private List<SopActionTreeNode> replaceTree;
}
