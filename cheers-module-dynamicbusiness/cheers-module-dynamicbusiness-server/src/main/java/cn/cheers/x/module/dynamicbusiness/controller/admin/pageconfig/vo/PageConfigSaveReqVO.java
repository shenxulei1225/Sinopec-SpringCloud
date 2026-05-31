package cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * 管理后台 - 页面配置创建/修改 Request VO
 */
@Schema(description = "管理后台 - 页面配置创建/修改 Request VO")
@Data
public class PageConfigSaveReqVO {

    @Schema(description = "主键ID（更新时必填）", example = "1024")
    private Long id;

    @Schema(description = "关联菜单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "菜单ID不能为空")
    private Long menuId;

    @Schema(description = "配置代码（唯一标识），格式：{businessType}-{pageNameSlug}；为空时后端自动生成", example = "equipment-shebei-guanli")
    private String configCode;

    @Schema(description = "页面代码（唯一标识），用于页面加载", example = "region-management-default")
    private String pageCode;

    @Schema(description = "页面类型：data_management-数据管理, dashboard-驾驶舱, statistics-统计, monitor-实时监控", 
            requiredMode = Schema.RequiredMode.REQUIRED, example = "data_management")
    @NotNull(message = "页面类型不能为空")
    private String pageType;

    @Schema(description = "配置内容(JSON)，结构取决于page_type", example = "{\"pattern\": \"B\", \"leftTreeType\": \"category-model\"}")
    private Map<String, Object> config;
}
