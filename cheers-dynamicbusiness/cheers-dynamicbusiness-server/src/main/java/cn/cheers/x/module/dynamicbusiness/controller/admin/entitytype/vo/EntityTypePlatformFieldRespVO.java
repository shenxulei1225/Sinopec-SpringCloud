package cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 业务类型平台系统字段响应 VO（如 name、status）。
 */
@Schema(description = "管理后台 - 业务类型平台系统字段 Response VO")
@Data
public class EntityTypePlatformFieldRespVO {

    @Schema(description = "字段编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "name")
    private String fieldCode;

    @Schema(description = "默认显示名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "名称")
    private String defaultLabel;

    @Schema(description = "当前显示名称（含别名）", requiredMode = Schema.RequiredMode.REQUIRED, example = "名称")
    private String fieldName;

    @Schema(description = "数据类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "TEXT")
    private String dataType;

    @Schema(description = "字段来源", requiredMode = Schema.RequiredMode.REQUIRED, example = "SYSTEM")
    private String fieldSource;

    @Schema(description = "是否可删除", example = "false")
    private Boolean deletable;

    @Schema(description = "别名是否可编辑", example = "true")
    private Boolean aliasEditable;

    @Schema(description = "是否必填", example = "true")
    private Boolean required;

    @Schema(description = "是否可搜索", example = "true")
    private Boolean isSearchable;

    @Schema(description = "是否可筛选", example = "false")
    private Boolean isFilterable;

    @Schema(description = "是否可排序", example = "true")
    private Boolean isSortable;
}
