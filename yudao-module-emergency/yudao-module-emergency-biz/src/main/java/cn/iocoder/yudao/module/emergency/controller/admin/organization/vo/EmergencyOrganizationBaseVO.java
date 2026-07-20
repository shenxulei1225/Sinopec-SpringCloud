package cn.iocoder.yudao.module.emergency.controller.admin.organization.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 应急组织架构 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class EmergencyOrganizationBaseVO {

    @Schema(description = "组织名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "应急管理部门")
    private String name;

    @Schema(description = "组织编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "EMERGENCY_DEPT_001")
    private String code;

    @Schema(description = "组织类型（GOVERNMENT政府/ENTERPRISE企业/INSTITUTION事业单位/SOCIAL社会团体）", example = "GOVERNMENT")
    private String type;

    @Schema(description = "上级组织ID", example = "1")
    private Long parentId;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "负责人", example = "张三")
    private String leader;

    @Schema(description = "联系电话", example = "13800138000")
    private String contactPhone;

    @Schema(description = "联系地址", example = "北京市朝阳区")
    private String contactAddress;

    @Schema(description = "组织描述", example = "负责应急管理工作")
    private String description;

    @Schema(description = "状态（ENABLE启用/DISABLE禁用）", example = "ENABLE")
    private String status;
}








