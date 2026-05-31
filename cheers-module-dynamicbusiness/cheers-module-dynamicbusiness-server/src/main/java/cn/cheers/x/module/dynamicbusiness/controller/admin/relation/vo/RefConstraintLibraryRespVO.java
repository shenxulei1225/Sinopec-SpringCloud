package cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - Ref 约束器库 Response VO")
@Data
public class RefConstraintLibraryRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "personnel")
    private String businessTypeCode;

    @Schema(description = "Ref 目标类型", example = "personnel")
    private String refTargetType;

    @Schema(description = "约束器类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "ROLE_DEPT")
    private String constraintType;

    @Schema(description = "约束器名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "角色+部门")
    private String constraintName;

    @Schema(description = "状态：0-开启，1-关闭", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "排序值", example = "0")
    private Integer sort;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
