package cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 业务类型基础 VO
 * 
 * 包含业务类型最基础的核心字段及通用校验规则。
 */
@Data
public class BusinessTypeBaseVO {

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    @Schema(description = "业务类型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "设备管理")
    @NotBlank(message = "名称不能为空")
    @Size(max = 100, message = "名称长度不能超过100")
    private String name;

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "编码不能为空")
    @Size(max = 100, message = "编码长度不能超过100")
    private String code;

    @Schema(description = "父级业务类型编号", example = "1024")
    private Long parentId;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "active")
    @NotNull(message = "状态不能为空")
    private String status;

    @Schema(description = "描述", example = "设备管理业务类型")
    private String description;

    @Schema(description = "图标", example = "device")
    private String icon;

    @Schema(description = "别名（业务数据是什么，如：产品、设备、人员等）", example = "设备")
    private String alias;

    @Schema(description = "关联字段定义（JSON格式）", example = "[{\"name\":\"关联设备\",\"type\":\"REF_SINGLE\"}]")
    private String associationFields;

    // ========== 存储配置扁平化字段 ==========

    @Schema(description = "存储类型：GENERIC-通用，DEDICATED-专用", requiredMode = Schema.RequiredMode.REQUIRED, example = "DEDICATED")
    @NotBlank(message = "存储类型不能为空")
    private String storageType;

    @Schema(description = "专用表名", example = "biz_equipment")
    private String dedicatedTableName;

    @Schema(description = "物理列映射配置（JSON格式）", example = "{\"code\":{\"column\":\"code\",\"type\":\"VARCHAR\"}}")
    private String physicalColumnMapping;

    @Schema(description = "是否启用规则引擎", example = "false")
    private Boolean enableRuleEngine;

}
