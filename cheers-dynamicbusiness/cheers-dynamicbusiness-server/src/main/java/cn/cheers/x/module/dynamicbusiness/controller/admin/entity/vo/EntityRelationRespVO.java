package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实体关联关系响应 VO
 */
@Schema(description = "管理后台 - 实体关联关系响应 VO")
@Data
public class EntityRelationRespVO {

    @Schema(description = "关联关系ID", example = "1")
    private Long id;

    @Schema(description = "源实体ID", example = "1")
    private Long sourceEntityId;

    @Schema(description = "源实体名称", example = "设备A")
    private String sourceEntityName;

    @Schema(description = "源实体业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    private String sourceEntityTypeCode;

    @Schema(description = "目标实体ID", example = "2")
    private Long targetEntityId;

    @Schema(description = "目标实体名称", example = "任务B")
    private String targetEntityName;

    @Schema(description = "目标实体业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "task")
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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
