package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "数据管理 - 数据 Tab 布局响应")
@Data
public class DmDataTabLayoutRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "所属工作台布局 id")
    private Long layoutId;

    @Schema(description = "数据类型编码（实例可有；模版可空）")
    private String entityTypeCode;

    @Schema(description = "列种类 CATEGORY|MODEL|ENTITY|DETAIL")
    private String columnKind;

    @Schema(description = "列顶标签页（Tab）编号：CATEGORY / MODEL / ENTITY 多列")
    private String tabId;

    @Schema(description = "组件配置 propsId")
    private Long propsId;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "列扩展 JSON：按 columnKind 为分类栏 / 型号栏 / 实体栏扩展")
    private Object columnMeta;
}
