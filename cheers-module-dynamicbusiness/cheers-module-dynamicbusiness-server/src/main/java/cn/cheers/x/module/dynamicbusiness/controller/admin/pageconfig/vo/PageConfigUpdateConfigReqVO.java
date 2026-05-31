package cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * 管理后台 - 更新页面配置内容 Request VO
 * 
 * 用于只更新 config 字段的场景（如修改 Pattern）
 */
@Schema(description = "管理后台 - 更新页面配置内容 Request VO")
@Data
public class PageConfigUpdateConfigReqVO {

    @Schema(description = "关联菜单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "菜单ID不能为空")
    private Long menuId;

    @Schema(description = "配置内容(JSON)，结构取决于page_type", 
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "{\"pattern\": \"B\", \"leftTreeType\": \"category-model\"}")
    @NotNull(message = "配置内容不能为空")
    private Map<String, Object> config;
}
