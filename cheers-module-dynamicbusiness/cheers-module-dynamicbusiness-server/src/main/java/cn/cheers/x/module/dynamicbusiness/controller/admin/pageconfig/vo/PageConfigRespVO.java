package cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 管理后台 - 页面配置信息 Response VO
 */
@Schema(description = "管理后台 - 页面配置信息 Response VO")
@Data
public class PageConfigRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "关联菜单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long menuId;

    @Schema(description = "页面类型：data_management-数据管理, dashboard-驾驶舱, statistics-统计, monitor-实时监控",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "data_management")
    private String pageType;

    @Schema(description = "页面代码（唯一标识），用于页面加载", example = "region-management-default")
    private String pageCode;

    @Schema(description = "配置代码（唯一标识）")
    private String configCode;

    @Schema(description = "配置内容(JSON)，结构取决于page_type", example = "{\"pattern\": \"B\", \"leftTreeType\": \"category-model\"}")
    private Map<String, Object> config;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;
}
