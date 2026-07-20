package cn.iocoder.yudao.module.emergency.controller.admin.backup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 恢复备份 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - 恢复备份 Request VO")
@Data
public class BackupRestoreReqVO {

    @Schema(description = "是否确认恢复（安全措施）", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean confirmRestore;
}








