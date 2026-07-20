package cn.iocoder.yudao.module.emergency.controller.admin.backup.vo;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 备份记录分页查询 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - 备份记录分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BackupRecordPageReqVO extends PageParam {

    @Schema(description = "备份类型", example = "FULL")
    private String backupType;

    @Schema(description = "备份状态", example = "SUCCESS")
    private String status;

    @Schema(description = "租户ID", example = "1")
    private Long tenantId;
}








