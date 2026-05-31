package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 基于模板创建组件 Props 实例")
@Data
public class ComponentPropsCreateInstanceReqVO {

    @NotNull(message = "templateId 不能为空")
    private Long templateId;

    @Schema(description = "数据来源能力键，默认继承模板")
    private String dataSourceKey;

    @Schema(description = "兼容旧字段名")
    private String queryContractKey;

    private String name;

    private String description;

    public String resolveDataSourceKey() {
        if (dataSourceKey != null && !dataSourceKey.isBlank()) {
            return dataSourceKey.trim();
        }
        if (queryContractKey != null && !queryContractKey.isBlank()) {
            return queryContractKey.trim();
        }
        return null;
    }
}
