package cn.cheers.x.module.platformresource.controller.admin.view.vo;

import lombok.Data;

import java.util.Map;

@Data
public class ViewConfigSaveReqVO {

    /** 模板：更新完整 configJson */
    private Map<String, Object> configJson;

    /** 实例：更新差量 configOverride */
    private Map<String, Object> configOverride;

    private String name;
    private String description;
    private String viewCode;
    private Long categoryId;
    /** 为 true 时清空 categoryId（与 categoryId 互斥使用） */
    private Boolean clearCategoryId;
    private Integer status;
    private Integer sort;
}
