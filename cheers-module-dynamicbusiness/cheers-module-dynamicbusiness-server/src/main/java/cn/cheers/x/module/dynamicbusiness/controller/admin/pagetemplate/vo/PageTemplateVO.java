package cn.cheers.x.module.dynamicbusiness.controller.admin.pagetemplate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 页面模板响应 VO
 * 
 * 用于从 JSON 文件加载的页面模板配置
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 页面模板响应")
@Data
public class PageTemplateVO {

    @Schema(description = "模板ID", example = "multi-tab-data-management-page")
    private String templateId;

    @Schema(description = "模板名称", example = "多Tab数据管理页面模板")
    private String templateName;

    @Schema(description = "模板版本", example = "1.0.0")
    private String templateVersion;

    @Schema(description = "模板类型", example = "page")
    private String templateType;

    @Schema(description = "页面类型", example = "multi-tab-data-management")
    private String pageType;

    @Schema(description = "页面名称", example = "数据管理")
    private String pageName;

    @Schema(description = "页面描述", example = "支持多维度查看数据的通用页面模板")
    private String pageDescription;

    @Schema(description = "页面图标", example = "icon-data")
    private String pageIcon;

    @Schema(description = "适用场景列表")
    private List<String> applicableScenarios;

    @Schema(description = "模板配置（JSON对象）")
    private Map<String, Object> config;

    @Schema(description = "配置说明（JSON对象）")
    private Map<String, Object> configDescription;

    @Schema(description = "作者", example = "系统预置")
    private String author;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "创建时间", example = "2026-01-17T10:00:00Z")
    private String createTime;

    @Schema(description = "更新时间", example = "2026-01-17T10:00:00Z")
    private String updateTime;
}
