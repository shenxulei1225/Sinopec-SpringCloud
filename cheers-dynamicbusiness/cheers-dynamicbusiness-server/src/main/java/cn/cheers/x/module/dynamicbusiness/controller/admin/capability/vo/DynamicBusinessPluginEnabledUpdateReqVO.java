package cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 动态业务插件启停请求。
 */
@Data
@Schema(description = "管理后台 - 动态业务插件启停 Request VO")
public class DynamicBusinessPluginEnabledUpdateReqVO {

    @NotNull(message = "enabled 不能为空")
    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean enabled;
}
