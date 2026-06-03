package cn.cheers.x.module.platformresource.controller.admin.view.vo;

import lombok.Data;

import java.util.Map;

@Data
public class ViewConfigRespVO {

    private Long viewId;
    private Boolean isTemplate;
    private Long templateId;
    private String viewType;
    private String viewCode;
    private String name;
    private String description;
    /** 模板完整配置（slots + relations），反序列化为 Map */
    private Map<String, Object> configJson;
    /** 实例差量配置，反序列化为 Map */
    private Map<String, Object> configOverride;
    /**
     * 运行时合并结果：merge(template.configJson, instance.configOverride)。
     * 调用方直接使用此字段渲染视图，无需自行合并。
     */
    private Map<String, Object> resolvedConfig;
    private Integer status;
    private Integer sort;
}
