package cn.iocoder.yudao.module.emergency.service.backup;

/**
 * 数据库恢复服务接口
 *
 * @author 芋道源码
 */
public interface DatabaseRestoreService {

    /**
     * 恢复备份
     *
     * @param backupId 备份记录ID
     * @param confirmRestore 是否确认恢复（安全措施）
     * @return 恢复记录ID
     */
    Long restoreBackup(Long backupId, boolean confirmRestore);

    /**
     * 恢复前创建当前数据备份（安全措施）
     *
     * @return 备份记录ID
     */
    Long createPreRestoreBackup();
}








