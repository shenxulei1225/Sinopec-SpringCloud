package cn.cheers.x.module.dynamicbusiness.dal.dataobject.migration;

import cn.iocoder.yudao.framework.mybatis.core.type.JsonbStringTypeHandler;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 数据迁移日志 DO
 * 
 * 业务含义：记录每次数据迁移的执行情况，包括迁移类型、执行结果、耗时等信息。
 * 用于追踪迁移历史和问题排查。
 * 
 * @author yudao
 * @since 2026-01-07
 */
@TableName(value = "dynamic_data_migration_log", autoResultMap = true)
@KeySequence("dynamic_data_migration_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataMigrationLogDO extends TenantBaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 迁移类型
     * 
     * 可选值：
     * - RELATION_FIELD_MIGRATION: 关联字段迁移
     * - SMART_DEFAULTS_APPLICATION: 智能默认应用
     * - FULL_MIGRATION: 完整迁移
     * - ROLLBACK: 回滚
     * - VALIDATION: 验证
     */
    private String migrationType;

    /**
     * 迁移版本
     */
    private String migrationVersion;

    /**
     * 是否试运行
     */
    private Boolean dryRun;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 耗时（毫秒）
     */
    private Long durationMs;

    /**
     * 处理总数
     */
    private Integer totalCount;

    /**
     * 成功数
     */
    private Integer successCount;

    /**
     * 跳过数
     */
    private Integer skippedCount;

    /**
     * 失败数
     */
    private Integer failedCount;

    /**
     * 状态
     * 
     * 可选值：
     * - RUNNING: 执行中
     * - SUCCESS: 成功
     * - FAILED: 失败
     * - ROLLED_BACK: 已回滚
     */
    private String status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 迁移详情（JSON 格式）
     */
    @TableField(value = "details", typeHandler = JsonbStringTypeHandler.class)
    private String details;

    /**
     * 迁移状态枚举
     */
    public enum MigrationStatus {
        RUNNING("RUNNING", "执行中"),
        SUCCESS("SUCCESS", "成功"),
        FAILED("FAILED", "失败"),
        ROLLED_BACK("ROLLED_BACK", "已回滚");

        private final String code;
        private final String name;

        MigrationStatus(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }
}
