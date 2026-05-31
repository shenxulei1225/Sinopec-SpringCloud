package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 保存组件 Props（模板 propsJson / 实例 propsOverride）")
@Data
public class ComponentPropsSaveReqVO {

    @Schema(description = "模板：完整 props（保存时与 dataSourceKey 列同步）")
    private Map<String, Object> propsJson;

    @Schema(description = "实例：相对模板的差异")
    private Map<String, Object> propsOverride;

    @Schema(description = "数据来源能力键（模板必填；与 props_json.dataSourceKey 同步）")
    private String dataSourceKey;

    @Schema(description = "数据来源能力键（兼容旧字段名）")
    private String queryContractKey;

    @Schema(description = "展示名称")
    private String name;

    @Schema(description = "状态：0 禁用 1 启用")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "Schema 版本（仅模板可改）")
    private String schemaVersion;

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
