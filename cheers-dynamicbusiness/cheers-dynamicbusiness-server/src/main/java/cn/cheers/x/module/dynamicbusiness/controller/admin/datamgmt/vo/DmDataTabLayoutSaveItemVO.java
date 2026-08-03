package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "数据管理 - 数据 Tab 布局保存项")
@Data
public class DmDataTabLayoutSaveItemVO {

    @Schema(description = "列种类 CATEGORY|MODEL|ENTITY|DETAIL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "columnKind 不能为空")
    private String columnKind;

    @Schema(description = "视角 id：CATEGORY 必填；MODEL/ENTITY 多列时填写；DETAIL 勿填")
    private String perspectiveId;

    @Schema(description = "组件配置 propsId")
    private Long propsId;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "列扩展 JSON：CATEGORY 为分类列设置；MODEL 多 Tab 时可存 { label }")
    private Object categoryColumn;
}
