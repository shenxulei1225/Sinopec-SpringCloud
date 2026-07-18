package cn.cheers.x.module.dynamicbusiness.service.migration.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 数据迁移状态 VO
 * 
 * @author yudao
 * @since 2026-01-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "数据迁移状态")
public class MigrationStatusVO {

    // ========== 关联字段迁移状态 ==========

    @Schema(description = "ENTITY_REF 类型字段总数")
    private Integer totalEntityRefFields;

    @Schema(description = "已迁移到关联字段库的字段数")
    private Integer migratedToLibraryCount;

    @Schema(description = "待迁移的关联字段数")
    private Integer pendingRelationFieldMigration;

    @Schema(description = "关联字段库中的记录总数")
    private Integer relationFieldLibraryCount;

    // ========== 智能默认设置状态 ==========

    @Schema(description = "字段总数")
    private Integer totalFields;

    @Schema(description = "已设置可查询属性的字段数")
    private Integer fieldsWithSearchable;

    @Schema(description = "已设置索引策略的字段数")
    private Integer fieldsWithIndexStrategy;

    @Schema(description = "待应用智能默认的字段数")
    private Integer pendingSmartDefaults;

    // ========== 整体状态 ==========

    @Schema(description = "是否需要迁移")
    private Boolean needsMigration;

    @Schema(description = "迁移完成百分比")
    private Integer completionPercentage;

    @Schema(description = "最后检查时间")
    private LocalDateTime lastCheckTime;

    @Schema(description = "状态描述")
    private String statusDescription;

    /**
     * 计算迁移完成百分比
     */
    public void calculateCompletionPercentage() {
        int totalPending = 0;
        int totalItems = 0;

        // 关联字段迁移
        if (totalEntityRefFields != null && totalEntityRefFields > 0) {
            totalItems += totalEntityRefFields;
            if (migratedToLibraryCount != null) {
                totalPending += (totalEntityRefFields - migratedToLibraryCount);
            } else {
                totalPending += totalEntityRefFields;
            }
        }

        // 智能默认设置
        if (totalFields != null && totalFields > 0) {
            totalItems += totalFields;
            if (pendingSmartDefaults != null) {
                totalPending += pendingSmartDefaults;
            } else {
                totalPending += totalFields;
            }
        }

        if (totalItems > 0) {
            int completed = totalItems - totalPending;
            this.completionPercentage = (int) ((completed * 100.0) / totalItems);
        } else {
            this.completionPercentage = 100;
        }

        // 判断是否需要迁移
        this.needsMigration = totalPending > 0;

        // 生成状态描述
        if (this.needsMigration) {
            StringBuilder sb = new StringBuilder("待处理: ");
            if (pendingRelationFieldMigration != null && pendingRelationFieldMigration > 0) {
                sb.append(pendingRelationFieldMigration).append(" 个关联字段待迁移");
            }
            if (pendingSmartDefaults != null && pendingSmartDefaults > 0) {
                if (sb.length() > 5) {
                    sb.append(", ");
                }
                sb.append(pendingSmartDefaults).append(" 个字段待应用智能默认");
            }
            this.statusDescription = sb.toString();
        } else {
            this.statusDescription = "所有迁移已完成";
        }
    }
}
