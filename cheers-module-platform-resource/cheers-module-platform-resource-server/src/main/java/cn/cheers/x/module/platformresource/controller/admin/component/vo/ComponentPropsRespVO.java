package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 组件配置响应")
@Data
public class ComponentPropsRespVO {

    private Long propsId;
    private Boolean isTemplate;
    private Long componentId;
    private String componentCode;
    private ComponentDataSourceVO dataSource;
    private Long templateId;
    private String schemaVersion;
    private Map<String, Object> props;
    private Map<String, Object> propsOverride;
    private String name;
    private Integer status;
    private Integer sort;
    private String description;
}
