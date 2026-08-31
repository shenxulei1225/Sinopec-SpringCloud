package cn.cheers.x.module.platformresource.api.component.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * RPC：创建组件 props 模板（与后台 createTemplate 同语义）。
 */
@Schema(description = "RPC - 创建组件 Props 模板")
@Data
public class ComponentPropsCreateTemplateReqDTO {

    @Schema(description = "组件编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "list")
    @NotBlank(message = "componentCode 不能为空")
    private String componentCode;

    @Schema(description = "schema 版本", requiredMode = Schema.RequiredMode.REQUIRED, example = "list@1")
    @NotBlank(message = "schemaVersion 不能为空")
    private String schemaVersion;

    @Schema(description = "展示配置 props JSON 对象")
    private Map<String, Object> props;

    @Schema(description = "数据来源")
    private ComponentDataSourceDTO dataSource;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "状态：1 启用", example = "1")
    private Integer status;

    @Schema(description = "排序", example = "0")
    private Integer sort;

    @Schema(description = "描述")
    private String description;
}
