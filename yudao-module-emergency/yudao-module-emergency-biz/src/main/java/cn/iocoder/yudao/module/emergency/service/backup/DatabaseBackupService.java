package cn.iocoder.yudao.module.emergency.service.backup;

import cn.iocoder.yudao.module.emergency.dal.dataobject.backup.BackupRecordDO;

/**
 * 数据库备份服务接口
 *
 * @author 芋道源码
 */
public interface DatabaseBackupService {

    /**
     * 创建全量备份
     *
     * @param description 备份描述
     * @return 备份记录ID
     */
    Long createFullBackup(String description);

    /**
     * 创建租户备份
     *
     * @param tenantId 租户ID
     * @param description 备份描述
     * @return 备份记录ID
     */
    Long createTenantBackup(Long tenantId, String description);

    /**
     * 创建模块备份
     *
     * @param moduleName 模块名称
     * @param description 备份描述
     * @return 备份记录ID
     */
    Long createModuleBackup(String moduleName, String description);

    /**
     * 获取备份记录
     *
     * @param id 备份记录ID
     * @return 备份记录
     */
    BackupRecordDO getBackupRecord(Long id);

    /**
     * 验证备份文件
     *
     * @param backupRecord 备份记录
     * @return 是否有效
     */
    boolean validateBackupFile(BackupRecordDO backupRecord);

    /**
     * 删除备份文件
     *
     * @param id 备份记录ID
     */
    void deleteBackup(Long id);
}








