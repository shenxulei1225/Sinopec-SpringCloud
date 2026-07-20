package cn.iocoder.yudao.module.emergency.service.backup;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.emergency.dal.dataobject.backup.BackupRecordDO;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.io.File;

/**
 * 数据库恢复服务实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class DatabaseRestoreServiceImpl implements DatabaseRestoreService {

    @Resource
    private DatabaseBackupService backupService;


    @Value("${spring.datasource.druid.url:}")
    private String jdbcUrl;

    @Value("${spring.datasource.druid.username:}")
    private String username;

    @Value("${spring.datasource.druid.password:}")
    private String password;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long restoreBackup(Long backupId, boolean confirmRestore) {
        if (!confirmRestore) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESTORE_FAILED, "恢复操作需要确认");
        }

        log.warn("开始恢复备份: backupId={}", backupId);
        
        // 获取备份记录
        BackupRecordDO backupRecord = backupService.getBackupRecord(backupId);
        
        // 验证备份文件
        if (!backupService.validateBackupFile(backupRecord)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.BACKUP_FILE_INVALID);
        }

        // 恢复前创建当前数据备份（安全措施）
        Long preRestoreBackupId = createPreRestoreBackup();
        log.info("恢复前备份已创建: backupId={}", preRestoreBackupId);

        try {
            // 执行恢复
            executeRestore(backupRecord);
            log.info("备份恢复完成: backupId={}", backupId);
            return preRestoreBackupId;
        } catch (Exception e) {
            log.error("备份恢复失败", e);
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESTORE_FAILED, e.getMessage());
        }
    }

    @Override
    public Long createPreRestoreBackup() {
        return backupService.createFullBackup("恢复前自动备份");
    }

    /**
     * 执行恢复
     */
    private void executeRestore(BackupRecordDO backupRecord) {
        try {
            File backupFile = new File(backupRecord.getBackupPath());
            if (!backupFile.exists()) {
                throw new RuntimeException("备份文件不存在: " + backupRecord.getBackupPath());
            }

            // 提取数据库连接信息
            String databaseName = extractDatabaseName(jdbcUrl);
            String host = extractHost(jdbcUrl);
            String port = extractPort(jdbcUrl);

            // 构建pg_restore命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "pg_restore",
                    "-h", host,
                    "-p", port,
                    "-U", username,
                    "-d", databaseName,
                    "-c",  // 清理（删除）现有对象
                    "-v",  // 详细模式
                    backupFile.getAbsolutePath()
            );
            
            // 设置环境变量（密码）
            processBuilder.environment().put("PGPASSWORD", password);
            
            // 执行恢复
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("pg_restore执行失败，退出码: " + exitCode);
            }

            log.info("数据恢复成功: backupId={}", backupRecord.getId());

        } catch (Exception e) {
            log.error("恢复执行异常", e);
            throw new RuntimeException("恢复执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从JDBC URL提取数据库名
     */
    private String extractDatabaseName(String jdbcUrl) {
        int lastSlash = jdbcUrl.lastIndexOf('/');
        if (lastSlash > 0) {
            String dbPart = jdbcUrl.substring(lastSlash + 1);
            int paramIndex = dbPart.indexOf('?');
            return paramIndex > 0 ? dbPart.substring(0, paramIndex) : dbPart;
        }
        throw new IllegalArgumentException("无法从JDBC URL提取数据库名: " + jdbcUrl);
    }

    /**
     * 从JDBC URL提取主机
     */
    private String extractHost(String jdbcUrl) {
        int start = jdbcUrl.indexOf("//") + 2;
        int end = jdbcUrl.indexOf(':', start);
        if (end < 0) {
            end = jdbcUrl.indexOf('/', start);
        }
        return end > start ? jdbcUrl.substring(start, end) : "localhost";
    }

    /**
     * 从JDBC URL提取端口
     */
    private String extractPort(String jdbcUrl) {
        int colonIndex = jdbcUrl.indexOf(':', jdbcUrl.indexOf("//") + 2);
        if (colonIndex > 0) {
            int end = jdbcUrl.indexOf('/', colonIndex);
            return end > colonIndex ? jdbcUrl.substring(colonIndex + 1, end) : "5432";
        }
        return "5432";
    }
}

