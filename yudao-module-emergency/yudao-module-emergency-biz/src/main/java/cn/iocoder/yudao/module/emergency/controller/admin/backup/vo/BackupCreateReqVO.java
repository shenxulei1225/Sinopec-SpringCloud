package cn.iocoder.yudao.module.emergency.controller.admin.backup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 创建备份 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - 创建备份 Request VO")
@Data
public class BackupCreateReqVO {

    @Schema(description = "备份类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "FULL")
    private String backupType;  // FULL/TENANT/MODULE

    @Schema(description = "租户ID（当backupType为TENANT时必填）", example = "1")
    private Long tenantId;

    @Schema(description = "模块名称（当backupType为MODULE时必填）", example = "event")
    private String moduleName;

    @Schema(description = "备份描述", example = "每日自动备份")
    private String description;
}








