package cn.cheers.x.module.dynamicbusiness.service.migration.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据迁移结果 VO
 * 
 * @author yudao
 * @since 2026-01-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "数据迁移结果")
public class MigrationResultVO {

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "是否为试运行模式")
    private Boolean dryRun;

    @Schema(description = "迁移类型", example = "RELATION_FIELD_MIGRATION")
    private String migrationType;

    @Schema(description = "处理的记录总数")
    private Integer totalCount;

    @Schema(description = "成功处理的记录数")
    private Integer successCount;

    @Schema(description = "跳过的记录数（已迁移或不需要迁移）")
    private Integer skippedCount;

    @Schema(description = "失败的记录数")
    private Integer failedCount;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "耗时（毫秒）")
    private Long durationMs;

    @Schema(description = "迁移详情列表")
    @Builder.Default
    private List<MigrationDetailVO> details = new ArrayList<>();

    @Schema(description = "错误信息列表")
    @Builder.Default
    private List<String> errors = new ArrayList<>();

    @Schema(description = "警告信息列表")
    @Builder.Default
    private List<String> warnings = new ArrayList<>();

    /**
     * 迁移详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "迁移详情")
    public static class MigrationDetailVO {

        @Schema(description = "记录ID")
        private Long recordId;

        @Schema(description = "记录类型", example = "FIELD")
        private String recordType;

        @Schema(description = "记录标识", example = "safety_manager")
        private String recordIdentifier;

        @Schema(description = "操作类型", example = "CREATE/UPDATE/SKIP")
        private String action;

        @Schema(description = "操作描述")
        private String description;

        @Schema(description = "是否成功")
        private Boolean success;

        @Schema(description = "错误信息")
        private String errorMessage;
    }

    /**
     * 迁移类型枚举
     */
    public enum MigrationType {
        /** 关联字段迁移 */
        RELATION_FIELD_MIGRATION,
        /** 智能默认应用 */
        SMART_DEFAULTS_APPLICATION,
        /** 完整迁移 */
        FULL_MIGRATION,
        /** 回滚 */
        ROLLBACK,
        /** 验证 */
        VALIDATION
    }

    /**
     * 操作类型枚举
     */
    public enum ActionType {
        /** 创建 */
        CREATE,
        /** 更新 */
        UPDATE,
        /** 跳过 */
        SKIP,
        /** 删除 */
        DELETE,
        /** 验证通过 */
        VALID,
        /** 验证失败 */
        INVALID
    }

    /**
     * 添加成功详情
     */
    public void addSuccessDetail(Long recordId, String recordType, String identifier, 
                                  String action, String description) {
        if (this.details == null) {
            this.details = new ArrayList<>();
        }
        this.details.add(MigrationDetailVO.builder()
                .recordId(recordId)
                .recordType(recordType)
                .recordIdentifier(identifier)
                .action(action)
                .description(description)
                .success(true)
                .build());
    }

    /**
     * 添加失败详情
     */
    public void addFailureDetail(Long recordId, String recordType, String identifier,
                                  String action, String errorMessage) {
        if (this.details == null) {
            this.details = new ArrayList<>();
        }
        this.details.add(MigrationDetailVO.builder()
                .recordId(recordId)
                .recordType(recordType)
                .recordIdentifier(identifier)
                .action(action)
                .description(null)
                .success(false)
                .errorMessage(errorMessage)
                .build());
    }

    /**
     * 添加错误信息
     */
    public void addError(String error) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(error);
    }

    /**
     * 添加警告信息
     */
    public void addWarning(String warning) {
        if (this.warnings == null) {
            this.warnings = new ArrayList<>();
        }
        this.warnings.add(warning);
    }

    /**
     * 计算耗时
     */
    public void calculateDuration() {
        if (this.startTime != null && this.endTime != null) {
            this.durationMs = java.time.Duration.between(startTime, endTime).toMillis();
        }
    }
}
