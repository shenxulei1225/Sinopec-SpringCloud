package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建实体关联关系请求 VO
 */
@Schema(description = "管理后台 - 创建实体关联关系请求 VO")
@Data
public class EntityRelationCreateReqVO {

    @Schema(description = "源实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "源实体ID不能为空")
    private Long sourceEntityId;

    @Schema(description = "源实体业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "源实体业务类型编码不能为空")
    private String sourceEntityTypeCode;

    @Schema(description = "目标实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "目标实体ID不能为空")
    private Long targetEntityId;

    @Schema(description = "目标实体业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "task")
    @NotBlank(message = "目标实体业务类型编码不能为空")
    private String targetEntityTypeCode;

    @Schema(description = "关联类型（ONE_TO_ONE/ONE_TO_MANY/MANY_TO_MANY）", requiredMode = Schema.RequiredMode.REQUIRED, example = "ONE_TO_MANY")
    @NotNull(message = "关联类型不能为空")
    private String relationType;

    @Schema(description = "关联名称", example = "所属设备")
    private String relationName;

    @Schema(description = "关联描述", example = "该任务关联的设备")
    private String description;

    @Schema(description = "关联属性（JSON格式）", example = "{\"priority\": 1}")
    private String relationAttributes;

    @Schema(description = "关联来源字段编码（有 REF/MultiRef 时写入，便于按字段反查）", example = "patrol_equipment")
    private String fieldCode;
}
