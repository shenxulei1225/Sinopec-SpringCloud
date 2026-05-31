package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 创建组件 Props 模板")
@Data
public class ComponentPropsCreateTemplateReqVO {

    @NotBlank(message = "componentCode 不能为空")
    private String componentCode;

    @NotBlank(message = "dataSourceKey 不能为空")
    @Schema(description = "数据来源能力键，如 system:dept")
    private String dataSourceKey;

    @Schema(description = "兼容旧字段名 queryContractKey")
    private String queryContractKey;

    @NotBlank(message = "schemaVersion 不能为空")
    private String schemaVersion;

    @NotNull(message = "propsJson 不能为空")
    private Map<String, Object> propsJson;

    @NotBlank(message = "name 不能为空")
    private String name;

    private Integer status;
    private Integer sort;
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
