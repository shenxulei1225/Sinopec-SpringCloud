package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "数据管理 - 数据 Tab 布局响应")
@Data
public class DmDataTabLayoutRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "数据类型编码")
    private String entityTypeCode;

    @Schema(description = "列种类 CATEGORY|MODEL|ENTITY|DETAIL")
    private String columnKind;

    @Schema(description = "分类列视角 id，仅 CATEGORY")
    private String perspectiveId;

    @Schema(description = "组件配置 propsId")
    private Long propsId;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "「数据」Tab 分类列设置 JSON")
    private Object categoryColumn;
}
