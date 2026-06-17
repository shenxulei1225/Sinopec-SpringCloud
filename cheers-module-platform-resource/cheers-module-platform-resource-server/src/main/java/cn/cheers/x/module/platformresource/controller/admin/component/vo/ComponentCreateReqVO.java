package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ComponentCreateReqVO {

    @NotBlank(message = "组件编码不能为空")
    private String componentCode;

    @NotBlank(message = "组件类型不能为空")
    private String type;

    @NotBlank(message = "名称不能为空")
    private String name;

    private String icon;
    private Integer status;
    private Integer sort;
    private String description;
}
