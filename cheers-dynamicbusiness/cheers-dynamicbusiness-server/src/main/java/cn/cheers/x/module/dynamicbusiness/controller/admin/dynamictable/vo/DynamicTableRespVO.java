package cn.cheers.x.module.dynamicbusiness.controller.admin.dynamictable.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 动态表响应 VO
 */
@Schema(description = "管理后台 - 动态表响应")
@Data
public class DynamicTableRespVO {

    @Schema(description = "动态表ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "模型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long modelId;

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "task")
    private String entityTypeCode;

    @Schema(description = "表名", requiredMode = Schema.RequiredMode.REQUIRED, example = "ent_task_001")
    private String tableName;

    @Schema(description = "表注释", example = "任务数据表")
    private String tableComment;

    @Schema(description = "状态（1-启用，0-禁用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "版本号", example = "1")
    private Integer version;

    @Schema(description = "最后同步时间")
    private LocalDateTime lastSyncTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
