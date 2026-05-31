package cn.cheers.x.module.dynamicbusiness.controller.admin.dynamictable.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 动态表审计日志响应 VO
 */
@Schema(description = "管理后台 - 动态表审计日志响应")
@Data
public class DynamicTableAuditLogRespVO {

    @Schema(description = "日志ID", example = "1")
    private Long id;

    @Schema(description = "动态表ID", example = "1")
    private Long dynamicTableId;

    @Schema(description = "操作类型", example = "CREATE_TABLE")
    private String operationType;

    @Schema(description = "操作描述", example = "创建动态表: biz_task_001")
    private String operationDesc;

    @Schema(description = "执行的SQL")
    private String executedSql;

    @Schema(description = "执行结果", example = "SUCCESS")
    private String executeResult;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "操作时间")
    private LocalDateTime operationTime;

    @Schema(description = "操作人名称")
    private String operatorName;
}
