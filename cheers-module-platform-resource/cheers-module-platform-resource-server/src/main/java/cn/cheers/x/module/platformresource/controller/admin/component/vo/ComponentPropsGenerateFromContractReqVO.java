package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 根据数据能力契约生成 Props")
@Data
public class ComponentPropsGenerateFromContractReqVO {

    @NotNull(message = "dataSource 不能为空")
    @Valid
    @Schema(description = "组件数据来源结构体")
    private ComponentDataSourceVO dataSource;

    @NotBlank(message = "componentCode 不能为空")
    @Schema(description = "组件编码：list | tree | table | card", example = "list")
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
}
