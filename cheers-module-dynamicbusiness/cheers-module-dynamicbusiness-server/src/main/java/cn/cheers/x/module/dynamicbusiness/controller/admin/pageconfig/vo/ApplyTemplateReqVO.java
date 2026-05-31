package cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 管理后台 - 应用模板创建页面配置 Request VO
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 应用模板创建页面配置 Request VO")
@Data
public class ApplyTemplateReqVO {

    @Schema(description = "关联菜单ID，0 或 null 表示自动创建新菜单", example = "100")
    private Long menuId;

    @Schema(description = "父菜单ID，用于在指定父菜单下创建子菜单", example = "5000")
    private Long parentMenuId;

    @Schema(description = "模板ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "multi-tab-data-management-page")
    @NotBlank(message = "模板ID不能为空")
    private String templateId;

    @Schema(description = "页面名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "设备管理")
    @NotBlank(message = "页面名称不能为空")
    private String pageName;

    @Schema(description = "页面代码（唯一标识），用于页面加载", example = "region-management-default")
    private String pageCode;

    @Schema(description = "业务类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "业务类型不能为空")
    private String businessType;

    @Schema(description = "Tab配置列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Tab配置不能为空")
    private List<TabConfigVO> tabs;

    @Schema(description = "字段列表", example = "[\"name\", \"code\", \"model\", \"status\"]")
    private List<String> fields;

    @Schema(description = "其他配置参数")
    private Map<String, Object> additionalConfig;

    /**
     * Tab配置 VO
     */
    @Schema(description = "Tab配置")
    @Data
    public static class TabConfigVO {

        @Schema(description = "Tab名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "按分类查看")
        @NotBlank(message = "Tab名称不能为空")
        private String name;

        @Schema(description = "分类体系根分类ID（用于左侧分类树过滤）", example = "4787")
        private Long topLevelCategoryId;

        @Schema(description = "分类体系名称（用于标识该 Tab 使用的分类体系）", example = "任务管理")
        private String categorySystemName;

        @Schema(description = "架构模式", example = "B")
        private String pattern;

        @Schema(description = "左侧树业务类型", example = "region")
        private String leftTreeBusinessType;

        @Schema(description = "左侧树分类类型代码（CategoryType.code，用于左树分类加载）", example = "region_category")
        private String leftTreeCategoryTypeCode;

        @Schema(description = "右侧内容业务类型（实体列表的业务类型），如果未设置则使用页面级别的businessType", example = "equipment")
        private String rightContentBusinessType;

        @Schema(description = "显示顺序", example = "1")
        private Integer displayOrder;

        @Schema(description = "是否启用", example = "true")
        private Boolean enabled;

        @Schema(description = "是否默认视图", example = "true")
        private Boolean isDefault;

        @Schema(description = "Tab图标", example = "icon-category")
        private String icon;

        @Schema(description = "默认显示的基础字段 ID 或编码列表", example = "[\"95\", \"code\"]")
        private List<String> defaultDisplayFields;

        @Schema(description = "列别名配置列表")
        private List<Map<String, Object>> aliasOption;

        @Schema(description = "其他配置")
        private Map<String, Object> config;
    }
}
