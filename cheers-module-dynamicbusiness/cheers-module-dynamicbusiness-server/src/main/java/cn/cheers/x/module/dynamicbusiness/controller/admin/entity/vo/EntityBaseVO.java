package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class EntityBaseVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "业务类型编码不能为空")
    private String businessTypeCode;

    @Schema(description = "模型ID（必填）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模型ID不能为空")
    private Long modelId;

    @Schema(description = "父实体ID（用于支持实体层级关系，为null或0表示根实体）", example = "1000")
    private Long parentId;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "示例实体")
    @NotBlank(message = "名称不能为空")
    @Size(max = 200, message = "名称长度不能超过200")
    private String name;

    @Schema(description = "固定列字段JSON（BaseField），key 使用字段编码 fieldCode（下划线列名）。例如：{\"area_id\":1,\"manufacturer\":\"ACME\"}", example = "{\"area_id\":1,\"manufacturer\":\"ACME\"}")
    private String baseFields;

    @Schema(description = "自定义字段JSON（CustomField），key 使用字段编码 fieldCode（下划线）。例如：{\"equipment_no\":\"EQ-001\",\"power_kw\":100}", example = "{\"equipment_no\":\"EQ-001\",\"power_kw\":100}")
    private String customFields;

    @Schema(description = "状态", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;
}

