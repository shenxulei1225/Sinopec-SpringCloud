package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 关联 Entity 响应 VO
 * 
 * 用于反向查询 API 返回的关联实体信息
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 关联 Entity 响应 VO")
@Data
public class RelatedEntityRespVO {

    @Schema(description = "Entity ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "Entity 名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "任务1")
    private String name;

    @Schema(description = "Model 编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "task")
    private String modelCode;

    @Schema(description = "Model 名称", example = "任务")
    private String modelName;

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "task-management")
    private String entityTypeCode;

    @Schema(description = "关联字段编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "plan_id")
    private String relationFieldCode;

    @Schema(description = "关联名称", example = "所属计划")
    private String relationName;

    @Schema(description = "状态（1-启用，0-禁用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "自定义字段", example = "{\"priority\": \"high\"}")
    private Map<String, Object> customFields;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;
}
