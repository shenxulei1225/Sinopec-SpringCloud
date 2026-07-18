package cn.cheers.x.module.dynamicbusiness.controller.admin.page.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 页面管理创建/修改 Request VO")
@Data
public class PageSaveReqVO {

    @Schema(description = "主键ID（更新时必填）", example = "1024")
    private Long id;

    @Schema(description = "页面代码（唯一标识）", requiredMode = Schema.RequiredMode.REQUIRED, example = "region-management-default")
    @NotBlank(message = "页面代码不能为空")
    private String pageCode;

    @Schema(description = "页面名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "设备管理")
    @NotBlank(message = "页面名称不能为空")
    private String pageName;

    @Schema(description = "页面类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "data_management")
    @NotBlank(message = "页面类型不能为空")
    private String pageType;

    @Schema(description = "页面描述", example = "设备管理页面")
    private String description;

    @Schema(description = "页面状态（1-发布，0-停用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "页面状态不能为空")
    private Integer status;

    @Schema(description = "业务归属父菜单ID", example = "100")
    private Long parentMenuId;

    @Schema(description = "页面挂载菜单ID", example = "200")
    private Long menuId;

    @Schema(description = "关联页面配置ID", example = "1024")
    private Long pageConfigId;

    @Schema(description = "页面图标", example = "icon-equipment")
    private String icon;

    @Schema(description = "页面标签（逗号分隔）", example = "巡检,任务")
    private String tags;

    @Schema(description = "路由地址", example = "/inspection/content")
    private String routePath;

    @Schema(description = "前端组件路径", example = "inspection/content/index")
    private String component;

    @Schema(description = "页面布局", example = "default")
    private String layout;

    @Schema(description = "A2UI Schema")
    private Map<String, Object> uiSchema;

    @Schema(description = "A2UI Schema 版本", example = "1.0.0")
    private String uiVersion;

    @Schema(description = "A2UI 数据源配置")
    private Map<String, Object> dataSource;

    @Schema(description = "备注")
    private String remark;
}
