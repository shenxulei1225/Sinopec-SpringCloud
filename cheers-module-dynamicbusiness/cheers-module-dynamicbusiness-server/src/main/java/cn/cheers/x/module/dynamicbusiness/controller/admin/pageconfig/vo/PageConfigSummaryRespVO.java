package cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 功能页面列表摘要（门户缩略图网格用）
 */
@Schema(description = "管理后台 - 功能页面摘要 Response VO")
@Data
public class PageConfigSummaryRespVO {

    @Schema(description = "页面配置 id")
    private Long id;

    @Schema(description = "所属业务 id")
    private Long businessId;

    @Schema(description = "菜单 id")
    private Long menuId;

    @Schema(description = "页面类型")
    private String pageType;

    @Schema(description = "页面代码")
    private String pageCode;

    @Schema(description = "页面名称")
    private String pageName;

    @Schema(description = "架构模式（如 A/B/C/D）")
    private String pattern;

    @Schema(description = "数据来源 entityTypeCode")
    private String entityTypeCode;

    @Schema(description = "菜单路由（进入页面用）")
    private String menuPath;

    @Schema(description = "配置内容")
    private Map<String, Object> config;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
