package cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 关联字段库 响应 VO
 */
@Schema(description = "管理后台 - 关联字段库 Response VO")
@Data
public class RelationFieldLibraryRespVO {

    @Schema(description = "字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "字段名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "安全负责人")
    private String fieldName;

    @Schema(description = "字段编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "safety_manager")
    private String fieldCode;

    @Schema(description = "关联业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "personnel")
    private String refEntityType;

    @Schema(description = "展示字段编码", example = "name")
    private String displayFieldCode;

    @Schema(description = "是否启用约束器", example = "true")
    private Boolean constraintEnabled;

    @Schema(description = "约束器类型", example = "ROLE_DEPT")
    private String constraintType;

    @Schema(description = "字段说明", example = "负责安全的人员")
    private String description;

    @Schema(description = "使用次数", example = "5")
    private Integer usageCount;

    @Schema(description = "是否系统预置", example = "false")
    private Boolean isSystem;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
