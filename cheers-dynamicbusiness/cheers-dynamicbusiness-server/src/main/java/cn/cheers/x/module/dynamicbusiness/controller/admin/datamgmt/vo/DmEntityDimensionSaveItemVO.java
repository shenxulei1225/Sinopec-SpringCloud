package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "数据管理 - 实体浏览维度保存项")
@Data
public class DmEntityDimensionSaveItemVO {

    @Schema(description = "维度种类 CATEGORY|MODEL|ENTITY|DETAIL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "dimensionKind 不能为空")
    private String dimensionKind;

    @Schema(description = "分类维视角 id，仅 CATEGORY")
    private String perspectiveId;

    @Schema(description = "组件配置 propsId")
    private Long propsId;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "分类维元数据 JSON 对象，仅 CATEGORY")
    private Object categoryDimensionMeta;

    @Schema(description = "模型管理 Tab 分类栏元数据 JSON 对象，仅 MODEL")
    private Object modelAdminCategoryMeta;
}
