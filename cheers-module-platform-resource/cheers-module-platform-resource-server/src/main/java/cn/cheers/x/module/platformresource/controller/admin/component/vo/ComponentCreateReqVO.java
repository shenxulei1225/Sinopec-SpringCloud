package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class ComponentCreateReqVO {

    @NotBlank(message = "组件 key 不能为空")
    private String key;

    @NotBlank(message = "组件类型不能为空")
    private String type;

    @NotBlank(message = "名称不能为空")
    private String name;

    private String icon;
    private Object props;
    private Map<String, Object> dataConfig;
    private Map<String, Map<String, Object>> apiConfig;
    private Map<String, Object> uiConfig;
    private Integer status;
    private Integer sort;
    private String description;
}