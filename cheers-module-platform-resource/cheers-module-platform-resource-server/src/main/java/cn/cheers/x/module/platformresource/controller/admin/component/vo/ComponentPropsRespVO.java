package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 组件 Props 行（模板或实例），字段与前端 ComponentProps 对齐。
 */
@Data
public class ComponentPropsRespVO {

    private Long propsId;
    private Boolean isTemplate;
    private Long componentId;
    private String componentCode;

    @Schema(description = "数据来源能力键，如 system:dept")
    private String dataSourceKey;

    private Long templateId;
    private String schemaVersion;
    private Map<String, Object> propsJson;
    private Map<String, Object> propsOverride;
    private String name;
    private Integer status;
    private Integer sort;
    private String description;
}
