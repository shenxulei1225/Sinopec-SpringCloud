package cn.iocoder.yudao.module.emergency.dal.dataobject.backup;

import cn.iocoder.yudao.module.emergency.dal.dataobject.EmergencyBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 数据备份记录 DO
 *
 * @author 芋道源码
 */
@TableName("emergency_backup_record")
@KeySequence("emergency_backup_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackupRecordDO extends EmergencyBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 备份名称
     */
    private String backupName;

    /**
     * 备份类型（FULL-全量备份，TENANT-按租户备份，MODULE-按模块备份）
     */
    private String backupType;

    /**
     * 备份文件路径
     */
    private String backupPath;

    /**
     * 备份文件大小（字节）
     */
    private Long fileSize;

    /**
     * 租户ID（当backup_type为TENANT时）
     */
    private Long tenantId;

    /**
     * 模块名称（当backup_type为MODULE时）
     */
    private String moduleName;

    /**
     * 备份状态（SUCCESS-成功，FAILED-失败，IN_PROGRESS-进行中）
     */
    private String status;

    /**
     * 错误信息（备份失败时）
     */
    private String errorMessage;

    /**
     * 备份描述
     */
    private String description;
}








