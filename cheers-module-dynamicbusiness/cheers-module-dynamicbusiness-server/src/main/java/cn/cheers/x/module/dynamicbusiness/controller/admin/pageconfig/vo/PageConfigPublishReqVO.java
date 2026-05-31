package cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 页面配置发布为菜单 Request VO")
@Data
public class PageConfigPublishReqVO {

    @Schema(description = "页面配置ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "页面配置ID不能为空")
    private Long pageConfigId;

    @Schema(description = "页面名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "设备管理")
    @NotBlank(message = "页面名称不能为空")
    private String pageName;

    @Schema(description = "页面代码（唯一标识），用于页面加载", example = "region-management-default")
    private String pageCode;

    @Schema(description = "业务类型代码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "业务类型不能为空")
    private String businessType;

    @Schema(description = "父菜单ID（可选，不传则自动推断）", example = "100")
    private Long parentMenuId;

    @Schema(description = "菜单图标", example = "icon-equipment")
    private String icon;
}

