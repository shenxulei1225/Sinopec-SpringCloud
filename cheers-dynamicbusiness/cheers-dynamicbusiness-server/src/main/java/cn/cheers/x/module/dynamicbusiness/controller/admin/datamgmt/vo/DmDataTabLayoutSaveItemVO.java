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

    @Schema(description = "分类列视角 id，仅 CATEGORY")
    private String perspectiveId;

    @Schema(description = "组件配置 propsId")
    private Long propsId;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "「数据」Tab 分类列设置 JSON，仅 CATEGORY")
    private Object categoryColumn;
}
