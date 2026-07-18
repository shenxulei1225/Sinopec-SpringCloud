package cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 应用模板创建页面配置 Response VO
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 应用模板创建页面配置 Response VO")
@Data
public class ApplyTemplateRespVO {

    @Schema(description = "页面配置ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    private Long pageConfigId;

    @Schema(description = "菜单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long menuId;

    @Schema(description = "是否创建了新菜单", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean menuCreated;

    @Schema(description = "菜单路径（如果创建了新菜单）", example = "/equipment/test-equipment")
    private String menuPath;

    @Schema(description = "菜单名称", example = "测试设备管理")
    private String menuName;
}
