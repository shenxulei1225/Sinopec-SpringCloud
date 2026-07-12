package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "数据管理 - 实体浏览维度响应")
@Data
public class DmEntityDimensionRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "数据类型编码")
    private String entityTypeCode;

    @Schema(description = "维度种类 CATEGORY|MODEL|ENTITY|DETAIL")
    private String dimensionKind;

    @Schema(description = "分类维视角 id，仅 CATEGORY")
    private String perspectiveId;

    @Schema(description = "组件配置 propsId")
    private Long propsId;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "分类维元数据 JSON 对象")
    private Object categoryDimensionMeta;

    @Schema(description = "模型管理 Tab 分类栏元数据 JSON 对象，仅 MODEL")
    private Object modelAdminCategoryMeta;
}
