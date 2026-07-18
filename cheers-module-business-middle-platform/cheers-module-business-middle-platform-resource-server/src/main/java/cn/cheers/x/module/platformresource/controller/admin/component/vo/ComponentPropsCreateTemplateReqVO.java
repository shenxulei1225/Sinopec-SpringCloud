package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class ComponentPropsCreateTemplateReqVO {

    @NotBlank(message = "componentCode 不能为空")
    private String componentCode;

    @NotBlank(message = "schemaVersion 不能为空")
    private String schemaVersion;

    private Map<String, Object> props;
    private ComponentDataSourceVO dataSource;
    private String name;
    private Integer status;
    private Integer sort;
    private String description;
}
