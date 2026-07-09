package cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 可关联业务模块（基于 REF_Multi 字段）响应 VO
 */
@Schema(description = "可关联业务模块（基于 REF_Multi 字段）")
@Data
@Builder
public class AssociationTargetRespVO {

    @Schema(description = "业务模块编码", example = "equipment")
    private String entityTypeCode;

    @Schema(description = "业务模块名称", example = "设备管理")
    private String entityTypeName;

    @Schema(description = "关联字段ID", example = "1001")
    private Long fieldId;

    @Schema(description = "关联字段编码", example = "F-xxxx")
    private String fieldCode;

    @Schema(description = "关联字段名称", example = "关联设备")
    private String fieldName;

    @Schema(description = "字段状态（1启用/0禁用）", example = "1")
    private Integer status;
}

