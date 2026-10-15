package cn.cheers.x.system.controller.admin.tenant.vo.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 当前业务租户的门户名称和 Logo。
 * 不改登录列表里的租户名。
 */
@Schema(description = "管理后台 - 租户门户 Request VO")
@Data
public class TenantPortalSaveReqVO {

    @Schema(description = "门户中文名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "智慧场站")
    @NotBlank(message = "请填写中文名称")
    @Size(max = 32, message = "中文名称不超过 32 个字")
    private String portalName;

    @Schema(description = "门户英文名称。显示在中文下面", requiredMode = Schema.RequiredMode.REQUIRED, example = "INTELLIGENT STATION")
    @NotBlank(message = "请填写英文名称")
    @Size(max = 48, message = "英文名称不超过 48 个字符")
    @Pattern(regexp = "^[A-Za-z0-9 ]+$", message = "英文名称只能使用字母、数字和空格")
    private String portalNameEn;

    @Schema(description = "门户 Logo 地址。空表示使用默认图标")
    @Size(max = 1024, message = "Logo 地址过长")
    @Pattern(regexp = "^$|^(https?://|/).*", message = "Logo 地址不正确")
    private String logoUrl;

}
