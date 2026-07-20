package cn.iocoder.yudao.module.emergency.service.backup;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.emergency.dal.dataobject.backup.BackupRecordDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.backup.BackupRecordMapper;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

/**
 * 数据库备份服务实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class DatabaseBackupServiceImpl implements DatabaseBackupService {

    @Resource
    private BackupRecordMapper backupRecordMapper;

    @Value("${spring.datasource.druid.url:}")
    private String jdbcUrl;

    @Value("${spring.datasource.druid.username:}")
    private String username;

    @Value("${spring.datasource.druid.password:}")
    private String password;

    @Value("${emergency.backup.directory:./backups}")
    private String backupDirectory;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFullBackup(String description) {
        log.info("开始创建全量备份: {}", description);
        
        // 创建备份记录
        BackupRecordDO backupRecord = BackupRecordDO.builder()
                .backupName("FULL_" + LocalDateTime.now().format(DATE_FORMATTER))
                .backupType("FULL")
                .status("IN_PROGRESS")
                .description(description)
                .build();
        backupRecordMapper.insert(backupRecord);

        // 异步执行备份
        CompletableFuture.runAsync(() -> {
            try {
                executeBackup(backupRecord);
            } catch (Exception e) {
                log.error("备份执行失败", e);
                updateBackupStatus(backupRecord.getId(), "FAILED", e.getMessage());
            }
        });

        return backupRecord.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTenantBackup(Long tenantId, String description) {
        log.info("开始创建租户备份: tenantId={}, description={}", tenantId, description);
        
        BackupRecordDO backupRecord = BackupRecordDO.builder()
                .backupName("TENANT_" + tenantId + "_" + LocalDateTime.now().format(DATE_FORMATTER))
                .backupType("TENANT")
                .tenantId(tenantId)
                .status("IN_PROGRESS")
                .description(description)
                .build();
        backupRecordMapper.insert(backupRecord);

        CompletableFuture.runAsync(() -> {
            try {
                executeBackup(backupRecord);
            } catch (Exception e) {
                log.error("租户备份执行失败", e);
                updateBackupStatus(backupRecord.getId(), "FAILED", e.getMessage());
            }
        });

        return backupRecord.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createModuleBackup(String moduleName, String description) {
        log.info("开始创建模块备份: moduleName={}, description={}", moduleName, description);
        
        BackupRecordDO backupRecord = BackupRecordDO.builder()
                .backupName("MODULE_" + moduleName + "_" + LocalDateTime.now().format(DATE_FORMATTER))
                .backupType("MODULE")
                .moduleName(moduleName)
                .status("IN_PROGRESS")
                .description(description)
                .build();
        backupRecordMapper.insert(backupRecord);

        CompletableFuture.runAsync(() -> {
            try {
                executeBackup(backupRecord);
            } catch (Exception e) {
                log.error("模块备份执行失败", e);
                updateBackupStatus(backupRecord.getId(), "FAILED", e.getMessage());
            }
        });

        return backupRecord.getId();
    }

    @Override
    public BackupRecordDO getBackupRecord(Long id) {
        BackupRecordDO backupRecord = backupRecordMapper.selectById(id);
        if (backupRecord == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.BACKUP_NOT_EXISTS);
        }
        return backupRecord;
    }

    @Override
    public boolean validateBackupFile(BackupRecordDO backupRecord) {
        if (backupRecord == null || backupRecord.getBackupPath() == null) {
            return false;
        }
        
        File backupFile = new File(backupRecord.getBackupPath());
        return backupFile.exists() && backupFile.isFile() && backupFile.length() > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBackup(Long id) {
        BackupRecordDO backupRecord = getBackupRecord(id);
        
        // 删除文件
        if (backupRecord.getBackupPath() != null) {
            File backupFile = new File(backupRecord.getBackupPath());
            if (backupFile.exists()) {
                boolean deleted = backupFile.delete();
                if (!deleted) {
                    log.warn("备份文件删除失败: {}", backupRecord.getBackupPath());
                }
            }
        }
        
        // 逻辑删除记录
        backupRecordMapper.deleteById(id);
        log.info("备份记录已删除: id={}", id);
    }

    /**
     * 执行备份
     */
    private void executeBackup(BackupRecordDO backupRecord) {
        try {
            // 确保备份目录存在
            Path backupDir = Paths.get(backupDirectory);
            if (!Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
            }

            // 构建备份文件路径
            String fileName = backupRecord.getBackupName() + ".sql.gz";
            Path backupPath = backupDir.resolve(fileName);

            // 提取数据库连接信息
            String databaseName = extractDatabaseName(jdbcUrl);
            String host = extractHost(jdbcUrl);
            String port = extractPort(jdbcUrl);

            // 构建pg_dump命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "pg_dump",
                    "-h", host,
                    "-p", port,
                    "-U", username,
                    "-d", databaseName,
                    "-F", "c",  // 自定义格式
                    "-f", backupPath.toString()
            );
            
            // 设置环境变量（密码）
            processBuilder.environment().put("PGPASSWORD", password);
            
            // 执行备份
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("pg_dump执行失败，退出码: " + exitCode);
            }

            // 更新备份记录
            File backupFile = backupPath.toFile();
            updateBackupStatus(backupRecord.getId(), "SUCCESS", null, 
                    backupPath.toString(), backupFile.length());

            log.info("备份完成: id={}, path={}, size={}", 
                    backupRecord.getId(), backupPath, backupFile.length());

        } catch (Exception e) {
            log.error("备份执行异常", e);
            throw new RuntimeException("备份执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新备份状态
     */
    private void updateBackupStatus(Long id, String status, String errorMessage) {
        updateBackupStatus(id, status, errorMessage, null, null);
    }

    /**
     * 更新备份状态
     */
    private void updateBackupStatus(Long id, String status, String errorMessage, 
                                   String backupPath, Long fileSize) {
        BackupRecordDO updateObj = new BackupRecordDO();
        updateObj.setId(id);
        updateObj.setStatus(status);
        updateObj.setErrorMessage(errorMessage);
        if (backupPath != null) {
            updateObj.setBackupPath(backupPath);
        }
        if (fileSize != null) {
            updateObj.setFileSize(fileSize);
        }
        backupRecordMapper.updateById(updateObj);
    }

    /**
     * 从JDBC URL提取数据库名
     */
    private String extractDatabaseName(String jdbcUrl) {
        // jdbc:postgresql://host:port/database
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
        // jdbc:postgresql://host:port/database
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
        // jdbc:postgresql://host:port/database
        int colonIndex = jdbcUrl.indexOf(':', jdbcUrl.indexOf("//") + 2);
        if (colonIndex > 0) {
            int end = jdbcUrl.indexOf('/', colonIndex);
            return end > colonIndex ? jdbcUrl.substring(colonIndex + 1, end) : "5432";
        }
        return "5432";
    }
}








