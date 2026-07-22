package cn.iocoder.yudao.module.emergency.controller.admin.plan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 应急预案 Base VO，提供给添加、修改、详情的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class EmergencyPlanBaseVO {

    @Schema(description = "预案编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "PLAN-2023001")
    private String planNo;

    @Schema(description = "预案名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "火灾应急预案")
    private String planName;

    @Schema(description = "预案类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer planType;

    @Schema(description = "预案级别列表（可选，存储该预案支持的所有级别，如 [\"I\", \"II\", \"III\"]，引用数据字典：emergency_plan_level）", example = "[\"I\", \"II\", \"III\"]")
    private List<String> planLevels;

    @Schema(description = "预案分组ID（可选，引用system_category表，business_type='emergency_plan_group'）", example = "1024")
    private Long planGroupId;

    @Schema(description = "自定义配置")
    private Map<String, Object> customConfig;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "published")
    private String status;

}


