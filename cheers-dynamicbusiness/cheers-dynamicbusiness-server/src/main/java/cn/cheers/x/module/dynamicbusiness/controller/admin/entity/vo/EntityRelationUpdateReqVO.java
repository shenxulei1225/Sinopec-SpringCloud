package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新实体关联关系请求 VO
 */
@Schema(description = "管理后台 - 更新实体关联关系请求 VO")
@Data
public class EntityRelationUpdateReqVO {

    @Schema(description = "关联关系ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "关联关系ID不能为空")
    private Long id;

    @Schema(description = "源实体业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "源实体业务类型编码不能为空")
    private String sourceEntityTypeCode;

    @Schema(description = "目标实体业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "task")
    @NotBlank(message = "目标实体业务类型编码不能为空")
    private String targetEntityTypeCode;

    @Schema(description = "关联类型（ONE_TO_ONE/ONE_TO_MANY/MANY_TO_MANY）", example = "ONE_TO_MANY")
    private String relationType;

    @Schema(description = "关联名称", example = "所属设备")
    private String relationName;

    @Schema(description = "关联描述", example = "该任务关联的设备")
    private String description;

    @Schema(description = "关联属性（JSON格式）", example = "{\"priority\": 1}")
    private String relationAttributes;

    @Schema(description = "状态（1-启用，0-禁用）", example = "1")
    private Integer status;
}
