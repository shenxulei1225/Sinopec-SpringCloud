package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 根据数据能力契约生成 Props")
@Data
public class ComponentPropsGenerateFromContractReqVO {

    @NotBlank(message = "dataSourceKey 不能为空")
    @Schema(description = "数据来源能力键，如 system:dept", example = "system:dept")
    private String dataSourceKey;

    @Schema(description = "兼容旧字段名 queryContractKey")
    private String queryContractKey;

    @NotBlank(message = "componentCode 不能为空")
    @Schema(description = "组件编码：list | tree", example = "list")
    private String componentCode;

    @Schema(description = "保存为模板时的名称；预览时可空")
    private String name;

    @Schema(description = "schemaVersion，默认 list@1 / tree@1")
    private String schemaVersion;

    @Schema(description = "保存时使用的固定 propsId；空则自增")
    private Long propsId;

    @Schema(description = "list 模板风格：full | minimal，默认 full")
    private String variant;

    @Schema(description = "仅预览不落库，默认 false")
    private Boolean previewOnly;

    @Schema(description = "契约缺失时是否触发 dynamicbusiness rebuild，默认 true")
    private Boolean rebuildIfMissing;

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
