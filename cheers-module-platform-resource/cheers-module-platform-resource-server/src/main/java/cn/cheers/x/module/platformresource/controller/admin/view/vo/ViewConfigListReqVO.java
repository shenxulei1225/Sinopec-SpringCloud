package cn.cheers.x.module.platformresource.controller.admin.view.vo;

import lombok.Data;

@Data
public class ViewConfigListReqVO {

    private String viewType;
    private Boolean isTemplate;
    /** 按视图分类节点 id 过滤 */
    private Long categoryId;
    /** 默认只返回启用记录（true），传 false 返回全部 */
    private Boolean onlyEnabled;
}
