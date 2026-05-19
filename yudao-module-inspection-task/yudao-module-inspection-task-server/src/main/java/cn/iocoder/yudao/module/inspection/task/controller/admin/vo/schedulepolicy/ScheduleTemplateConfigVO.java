package cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 排期模板组合配置 VO
 *
 * <p>一个排期需求可包含多个模板组合，每个模板组合记录：
 * 1. 原始模板的完整配置
 * 2. 用户修改的字段
 * 3. 是否启用</p>
 */
@Schema(description = "排期模板组合配置 VO")
@Data
public class ScheduleTemplateConfigVO {

    @Schema(description = "模板ID", example = "1")
    private Long templateId;

    @Schema(description = "模板名称", example = "日常巡检")
    private String templateName;

    @Schema(description = "原始模板完整配置（Map 结构，用于恢复模板配置）")
    private Map<String, Object> originalConfig;

    @Schema(description = "用户修改的字段（key为字段名，value为修改后的值）")
    private Map<String, Object> changedFields;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;
}
