package cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.ApplyTemplateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.ApplyTemplateRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigDraftCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigPublishReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigSummaryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigUpdateConfigByIdReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigUpdateConfigReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.pageconfig.PageConfigDO;
import cn.cheers.x.module.dynamicbusiness.service.pageconfig.PageConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 页面配置 Controller
 * 
 * 用于管理页面的配置信息，支持不同页面类型的配置。
 * 
 * 主要功能：
 * 1. 数据管理页面的 Pattern 配置（A/B/C/D 四种模式）
 * 2. 驾驶舱、统计、监控等页面的配置
 * 
 * @author yudao
 */
@Tag(name = "管理后台 - 页面配置", description = "提供页面配置的创建、更新、删除、查询等功能")
@RestController
@RequestMapping("/dynamicbusiness/page-config")
@Validated
public class PageConfigController {

    @Resource
    private PageConfigService pageConfigService;

    @PostMapping("/create")
    @Operation(summary = "创建页面配置", description = "创建一个新的页面配置")
    @PreAuthorize("@ss.hasPermission('system:page-config:create')")
    public CommonResult<Long> createPageConfig(@Valid @RequestBody PageConfigSaveReqVO createReqVO) {
        return success(pageConfigService.createPageConfig(createReqVO));
    }

    @PostMapping("/create-draft")
    @Operation(summary = "创建页面配置草稿", description = "创建一个不关联菜单的页面配置草稿")
    @PreAuthorize("@ss.hasPermission('system:page-config:create')")
    public CommonResult<Long> createDraft(@Valid @RequestBody PageConfigDraftCreateReqVO createReqVO) {
        return success(pageConfigService.createDraft(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新页面配置", description = "更新页面配置的完整信息")
    @PreAuthorize("@ss.hasPermission('system:page-config:update')")
    public CommonResult<Boolean> updatePageConfig(@Valid @RequestBody PageConfigSaveReqVO updateReqVO) {
        pageConfigService.updatePageConfig(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-config")
    @Operation(summary = "更新页面配置内容", description = "仅更新页面的 config 字段（如修改 Pattern）")
    @PreAuthorize("@ss.hasPermission('system:page-config:update')")
    public CommonResult<Boolean> updatePageConfigContent(@Valid @RequestBody PageConfigUpdateConfigReqVO updateReqVO) {
        pageConfigService.updatePageConfigByMenuId(updateReqVO.getMenuId(), updateReqVO.getConfig());
        return success(true);
    }

    @PutMapping("/update-config-by-id")
    @Operation(summary = "按ID更新配置内容", description = "仅根据配置ID更新页面的 config 字段")
    @PreAuthorize("@ss.hasPermission('system:page-config:update')")
    public CommonResult<Boolean> updatePageConfigById(@Valid @RequestBody PageConfigUpdateConfigByIdReqVO updateReqVO) {
        pageConfigService.updatePageConfigById(updateReqVO);
        return success(true);
    }

    @PostMapping("/save")
    @Operation(summary = "保存页面配置", description = "根据 menuId 判断：存在则更新，不存在则创建")
    @PreAuthorize("@ss.hasPermission('system:page-config:create')")
    public CommonResult<Long> savePageConfig(@Valid @RequestBody PageConfigSaveReqVO saveReqVO) {
        return success(pageConfigService.savePageConfig(saveReqVO));
    }

    @PostMapping("/publish")
    @Operation(summary = "发布页面配置", description = "为已有的页面配置草稿创建菜单并绑定关联关系")
    @PreAuthorize("@ss.hasPermission('system:page-config:update')")
    public CommonResult<Long> publish(@Valid @RequestBody PageConfigPublishReqVO publishReqVO) {
        return success(pageConfigService.publish(publishReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除页面配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:page-config:delete')")
    public CommonResult<Boolean> deletePageConfig(@RequestParam("id") Long id) {
        pageConfigService.deletePageConfig(id);
        return success(true);
    }

    @DeleteMapping("/delete-page")
    @Operation(summary = "删除页面（配置 + 菜单）", description = "根据页面配置ID，删除页面配置并级联删除对应菜单")
    @Parameter(name = "pageConfigId", description = "页面配置ID", required = true, example = "2048")
    @PreAuthorize("@ss.hasPermission('system:page-config:delete')")
    public CommonResult<Boolean> deletePageWithMenu(@RequestParam("pageConfigId") Long pageConfigId) {
        pageConfigService.deletePageWithMenu(pageConfigId);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得页面配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:page-config:query')")
    public CommonResult<PageConfigRespVO> getPageConfig(@RequestParam("id") Long id) {
        PageConfigDO pageConfig = pageConfigService.getPageConfig(id);
        return success(BeanUtils.toBean(pageConfig, PageConfigRespVO.class));
    }
    
    @GetMapping("/get-by-menu")
    @Operation(summary = "根据菜单ID获得页面配置", description = """
            前端页面加载时调用，获取当前页面的配置
            - 入口：用户点击侧边栏/目录树里的某个菜单项
            - 前端拥有的信息：menuId（通常在路由 meta.id 里）
            - 用途：根据 menuId 找到 dynamic_menu.page_config_id，再拿到对应 PageConfigDO
            - 典型调用链：
              - 路由生成（后端菜单 -> 前端路由，routerHelper.ts 把 route.id 写入 meta.id）
              - 页面渲染器启动时 getPageConfigByMenuId(menuId) 拉配置
            """)
    @Parameter(name = "menuId", description = "菜单ID", required = true, example = "100")
    @PreAuthorize("@ss.hasPermission('system:page-config:query')")
    public CommonResult<PageConfigRespVO> getPageConfigByMenuId(@RequestParam("menuId") Long menuId) {
        PageConfigDO pageConfig = pageConfigService.getPageConfigByMenuId(menuId);
        return success(BeanUtils.toBean(pageConfig, PageConfigRespVO.class));
    }

    @GetMapping("/get-by-page-code")
    @Operation(summary = "根据页面代码获得页面配置", description = """
            前端页面加载时调用，获取当前页面的配置
            - 入口：用户访问页面的 pageCode
            - 前端拥有的信息：pageCode（页面唯一标识）
            - 用途：根据 pageCode 找到对应 PageConfigDO
            """)
    @Parameter(name = "pageCode", description = "页面代码", required = true, example = "region-management-default")
    @PreAuthorize("@ss.hasPermission('system:page-config:query')")
    public CommonResult<PageConfigRespVO> getPageConfigByPageCode(@RequestParam("pageCode") String pageCode) {
        PageConfigDO pageConfig = pageConfigService.getPageConfigByPageCode(pageCode);
        return success(BeanUtils.toBean(pageConfig, PageConfigRespVO.class));
    }
    
    @DeleteMapping("/delete-by-menu")
    @Operation(summary = "根据菜单ID删除页面配置", description = "场景 A：菜单解绑")
    @Parameter(name = "menuId", description = "菜单ID", required = true, example = "100")
    @PreAuthorize("@ss.hasPermission('system:page-config:delete')")
    public CommonResult<Boolean> deletePageConfigByMenuId(@RequestParam("menuId") Long menuId) {
        pageConfigService.deletePageConfigByMenuId(menuId);
        return success(true);
    }
    @GetMapping("/list-by-type")
    @Operation(summary = "根据页面类型获得页面配置列表")
    @Parameter(name = "pageType", description = "页面类型", required = true, example = "data_management")
    @PreAuthorize("@ss.hasPermission('system:page-config:query')")
    public CommonResult<List<PageConfigRespVO>> getPageConfigListByPageType(@RequestParam("pageType") String pageType) {
        List<PageConfigDO> list = pageConfigService.getPageConfigListByPageType(pageType);
        return success(BeanUtils.toBean(list, PageConfigRespVO.class));
    }

    @GetMapping("/list-by-business")
    @Operation(summary = "按门户业务 id 获得功能页面摘要列表")
    @Parameter(name = "businessId", description = "门户业务 id", required = true, example = "100")
    @PreAuthorize("@ss.hasPermission('system:page-config:query')")
    public CommonResult<List<PageConfigSummaryRespVO>> getPageConfigListByBusiness(
            @RequestParam("businessId") Long businessId) {
        return success(pageConfigService.getPageConfigSummaryListByBusinessId(businessId));
    }

    @GetMapping("/list-by-entity-type")
    @Operation(summary = "根据业务类型获得页面配置列表", description = "用于动态业务管理，获取某个业务类型的所有页面配置")
    @Parameter(name = "entityTypeCode", description = "业务类型代码", required = true, example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:page-config:query')")
    public CommonResult<List<PageConfigRespVO>> getPageConfigListByEntityType(@RequestParam("entityTypeCode") String entityTypeCode) {
        List<PageConfigDO> list = pageConfigService.getPageConfigListByEntityType(entityTypeCode);
        return success(BeanUtils.toBean(list, PageConfigRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得页面配置分页")
    @PreAuthorize("@ss.hasPermission('system:page-config:query')")
    public CommonResult<PageResult<PageConfigRespVO>> getPageConfigPage(@Valid PageConfigPageReqVO pageReqVO) {
        PageResult<PageConfigDO> pageResult = pageConfigService.getPageConfigPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PageConfigRespVO.class));
    }

    @PostMapping("/apply-template")
    @Operation(summary = "应用模板创建页面配置", description = "根据模板配置和用户参数生成完整的PageConfig并保存，支持自动创建菜单。如果 menuId 为 0 或 null，将自动创建新菜单")
    @PreAuthorize("@ss.hasPermission('system:page-config:create')")
    public CommonResult<ApplyTemplateRespVO> applyTemplate(@Valid @RequestBody ApplyTemplateReqVO reqVO) {
        ApplyTemplateRespVO respVO = pageConfigService.applyTemplate(reqVO);
        return success(respVO);
    }
}
