package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "数据管理 - 数据 Tab 布局保存项")
@Data
public class DmDataTabLayoutSaveItemVO {

    @Schema(description = "已有布局行主键；改 tabId 时必传，否则会当成删旧插新并误删边")
    private Long id;

    @Schema(description = "列种类 CATEGORY|MODEL|ENTITY|DETAIL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "columnKind 不能为空")
    private String columnKind;

    @Schema(description = "列顶标签页编号：CATEGORY 必填；MODEL/ENTITY 必填且为底座类型编码（禁止空与字面 default）；DETAIL 勿填")
    private String tabId;

    @Schema(description = "组件配置 propsId")
    private Long propsId;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "列扩展 JSON：按 columnKind 为分类栏 / 型号栏 / 实体栏扩展（含栏所在区段、标准宽等）")
    private Object columnMeta;
}
