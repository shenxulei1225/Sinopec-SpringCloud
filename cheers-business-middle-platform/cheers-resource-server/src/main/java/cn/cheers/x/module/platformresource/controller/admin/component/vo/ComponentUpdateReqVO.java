package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import lombok.Data;

@Data
public class ComponentUpdateReqVO {

    private String type;
    private String name;
    private String icon;
    private Integer status;
    private Integer sort;
    private String description;
}
