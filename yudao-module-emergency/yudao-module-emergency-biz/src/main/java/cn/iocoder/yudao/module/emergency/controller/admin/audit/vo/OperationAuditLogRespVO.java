package cn.iocoder.yudao.module.emergency.controller.admin.audit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户操作审计日志 Response VO")
@Data
public class OperationAuditLogRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名称", example = "张三")
    private String userName;

    @Schema(description = "操作类型", example = "CREATE")
    private String operationType;

    @Schema(description = "业务模块", example = "event")
    private String businessModule;

    @Schema(description = "业务对象ID", example = "1")
    private Long businessId;

    @Schema(description = "业务对象名称", example = "应急事件")
    private String businessName;

    @Schema(description = "操作内容", example = "创建应急事件")
    private String operationContent;

    @Schema(description = "操作前数据", example = "{}")
    private String beforeData;

    @Schema(description = "操作后数据", example = "{}")
    private String afterData;

    @Schema(description = "操作IP", example = "192.168.1.1")
    private String operationIp;

    @Schema(description = "操作时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime operationTime;

    @Schema(description = "操作结果", example = "SUCCESS")
    private String operationResult;

    @Schema(description = "错误信息", example = "")
    private String errorMessage;

    @Schema(description = "请求路径", example = "/emergency/events")
    private String requestPath;

    @Schema(description = "请求方法", example = "POST")
    private String requestMethod;
}








