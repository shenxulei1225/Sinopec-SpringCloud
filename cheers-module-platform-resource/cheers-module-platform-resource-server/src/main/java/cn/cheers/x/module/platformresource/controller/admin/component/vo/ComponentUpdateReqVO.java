package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import lombok.Data;

import java.util.Map;

@Data
public class ComponentUpdateReqVO {

    private String type;
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