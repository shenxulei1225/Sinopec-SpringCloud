package cn.cheers.x.module.dynamicbusiness.controller.admin.pagetemplate;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pagetemplate.vo.PageTemplateVO;
import cn.cheers.x.module.dynamicbusiness.service.pagetemplate.PageTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 页面模板 Controller
 * 
 * 页面模板用于可视化配置界面，提供预定义的页面结构模板。
 * 模板从 JSON 文件加载，包含完整的页面配置（布局、Tab、字段列表等）。
 * 
 * 页面模板与字段模板的区别：
 * - 页面模板：定义完整的页面结构，存储在 JSON 文件中，用于快速创建页面
 * - 字段模板：定义字段组合，存储在数据库中，用于创建 Model
 * 
 * @author yudao
 */
@Tag(name = "管理后台 - 页面模板", description = "提供页面模板的查询功能，用于可视化配置界面")
@RestController
@RequestMapping("/dynamicbusiness/templates")
@Validated
public class PageTemplateController {

    @Resource
    private PageTemplateService pageTemplateService;

    @GetMapping("/page-templates")
    @Operation(
        summary = "获取页面模板列表",
        description = "获取所有可用的页面模板，用于可视化配置界面的模板选择。\n" +
            "- 模板从 JSON 文件加载，包含模板元数据和配置信息\n" +
            "- 返回结果按模板ID排序\n" +
            "- 加载失败的模板会被跳过"
    )
    @PreAuthorize("@ss.hasPermission('system:page-config:query')")
    public CommonResult<List<PageTemplateVO>> getPageTemplates() {
        List<PageTemplateVO> templates = pageTemplateService.loadPageTemplates();
        return success(templates);
    }

    @GetMapping("/page-templates/{templateId}")
    @Operation(
        summary = "获取页面模板详情",
        description = "根据模板ID获取页面模板的详细信息，包含：\n" +
            "- 模板元数据（名称、版本、作者、标签等）\n" +
            "- 页面配置（布局、Tab结构、字段列表等）\n" +
            "- 配置说明（各配置项的说明文档）"
    )
    @Parameter(name = "templateId", description = "模板ID", required = true, example = "multi-tab-data-management-page")
    @PreAuthorize("@ss.hasPermission('system:page-config:query')")
    public CommonResult<PageTemplateVO> getTemplateDetail(@PathVariable("templateId") String templateId) {
        PageTemplateVO template = pageTemplateService.getTemplateById(templateId);
        return success(template);
    }
}

