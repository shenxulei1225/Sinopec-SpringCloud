package cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Data
public class BusinessEntryBaseVO {

    @Schema(description = "入口编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment-admin")
    @NotBlank(message = "入口编码不能为空")
    @Size(max = 64)
    private String code;

    @Schema(description = "入口名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "设备台账")
    @NotBlank(message = "入口名称不能为空")
    @Size(max = 200)
    private String name;

    @Schema(description = "入口类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "ENTITY_ADMIN")
    @NotBlank(message = "入口类型不能为空")
    private String entryType;

    @Schema(description = "绑定的实体类型编码 entityTypeCode", example = "equipment")
    private String entityTypeCode;

    @Schema(description = "scoped 规则等")
    private Map<String, Object> scopeConfig;

    @Schema(description = "关联页面配置 id")
    private Long pageConfigId;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "排序不能为空")
    private Integer sort;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "active")
    @NotNull(message = "状态不能为空")
    private String status;
}
