package cn.cheers.x.module.platformresource.controller.admin.view.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class ViewConfigCreateTemplateReqVO {

    @NotBlank(message = "视图类型不能为空")
    private String viewType;

    private String viewCode;

    @NotBlank(message = "名称不能为空")
    private String name;

    private String description;

    @NotNull(message = "configJson 不能为空")
    private Map<String, Object> configJson;

    private Integer status;
    private Integer sort;
}
