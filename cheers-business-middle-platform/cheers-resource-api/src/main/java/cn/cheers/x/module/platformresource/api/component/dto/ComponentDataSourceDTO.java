package cn.cheers.x.module.platformresource.api.component.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 组件数据来源（RPC）：与配置库 data_source 结构一致。
 */
@Schema(description = "RPC - 组件数据来源")
@Data
public class ComponentDataSourceDTO {

    @Schema(description = "业务分类：dynamic / system / category", example = "dynamic")
    private String businessCategory;

    @Schema(description = "实体/分类类型编码", example = "equipment")
    private String entityTypeCode;

    @Schema(description = "数据类型：model / entity", example = "entity")
    private String dataKind;
}
