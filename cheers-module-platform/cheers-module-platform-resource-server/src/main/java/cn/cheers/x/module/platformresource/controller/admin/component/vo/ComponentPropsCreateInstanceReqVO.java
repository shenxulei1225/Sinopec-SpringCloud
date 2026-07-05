package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComponentPropsCreateInstanceReqVO {

    @NotNull(message = "templateId 不能为空")
    private Long templateId;

    private String name;
    private String description;
    private ComponentDataSourceVO dataSource;
}
