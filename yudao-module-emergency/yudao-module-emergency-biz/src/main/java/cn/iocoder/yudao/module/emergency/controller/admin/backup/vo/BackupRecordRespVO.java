package cn.iocoder.yudao.module.emergency.controller.admin.backup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 备份记录 Response VO
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - 备份记录 Response VO")
@Data
public class BackupRecordRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "备份名称", example = "FULL_20241226_020000")
    private String backupName;

    @Schema(description = "备份类型", example = "FULL")
    private String backupType;

    @Schema(description = "备份文件路径", example = "/backups/FULL_20241226_020000.sql.gz")
    private String backupPath;

    @Schema(description = "备份文件大小（字节）", example = "1048576")
    private Long fileSize;

    @Schema(description = "租户ID", example = "1")
    private Long tenantId;

    @Schema(description = "模块名称", example = "event")
    private String moduleName;

    @Schema(description = "备份状态", example = "SUCCESS")
    private String status;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "备份描述")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人", example = "admin")
    private String creator;
}








