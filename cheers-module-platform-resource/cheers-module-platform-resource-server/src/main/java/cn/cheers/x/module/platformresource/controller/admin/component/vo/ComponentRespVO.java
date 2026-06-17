package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import lombok.Data;

/**
 * 组件库目录返回值：仅登记信息，不含默认 props / 接口契约。
 */
@Data
public class ComponentRespVO {

    /** 表主键 */
    private Long id;
    /** 组件唯一编码 */
    private String componentCode;
    private String type;
    private String name;
    private String icon;
    private Integer status;
    private Integer sort;
    private String description;
}
