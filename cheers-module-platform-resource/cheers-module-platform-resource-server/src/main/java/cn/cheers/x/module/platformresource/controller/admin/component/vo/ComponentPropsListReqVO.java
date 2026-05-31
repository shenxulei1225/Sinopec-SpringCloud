package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 组件 Props 列表查询")
@Data
public class ComponentPropsListReqVO {

    @Schema(description = "组件编码，如 list / tree")
    private String componentCode;

    @Schema(description = "是否模板")
    private Boolean isTemplate;

    @Schema(description = "数据来源能力键")
    private String dataSourceKey;

    @Schema(description = "数据来源能力键（兼容旧参数名 queryContractKey）")
    private String queryContractKey;

    @Schema(description = "仅返回启用项，默认 true")
    private Boolean onlyEnabled;

    public String resolveDataSourceKeyFilter() {
        if (dataSourceKey != null && !dataSourceKey.isBlank()) {
            return dataSourceKey.trim();
        }
        if (queryContractKey != null && !queryContractKey.isBlank()) {
            return queryContractKey.trim();
        }
        return null;
    }
}
