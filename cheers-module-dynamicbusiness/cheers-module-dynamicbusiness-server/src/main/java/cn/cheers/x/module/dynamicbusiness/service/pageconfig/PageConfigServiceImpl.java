package cn.cheers.x.module.dynamicbusiness.service.pageconfig;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.controller.admin.page.vo.PageSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.ApplyTemplateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.ApplyTemplateRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigDraftCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigPublishReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigUpdateConfigByIdReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.page.PageDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.pageconfig.PageConfigDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.page.PageMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.pageconfig.PageConfigMapper;
import cn.cheers.x.module.dynamicbusiness.service.page.PageService;
import cn.cheers.x.module.dynamicbusiness.service.pagetemplate.PageTemplateService;
import cn.iocoder.yudao.module.system.api.permission.MenuApi;
import cn.iocoder.yudao.module.system.api.permission.dto.MenuRespDTO;
import cn.iocoder.yudao.module.system.api.permission.dto.MenuSaveReqDTO;
import cn.iocoder.yudao.module.system.enums.permission.MenuTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.MENU_NOT_EXISTS;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.PAGE_CONFIG_MENU_ID_DUPLICATE;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.PAGE_CONFIG_NOT_EXISTS;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.PAGE_CONFIG_PAGE_CODE_EXISTS;

/**
 * 页面配置 Service 实现
 */
@Service
@Validated
@Slf4j
@SuppressWarnings("deprecation")
public class PageConfigServiceImpl implements PageConfigService {

    @Resource
    private PageConfigMapper pageConfigMapper;
    @Resource
    private PageMapper pageMapper;
    @Resource
    private MenuApi menuApi;
    @Resource
    private PageService pageService;
    @Resource
    private PageTemplateService pageTemplateService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPageConfig(PageConfigSaveReqVO createReqVO) {
        MenuRespDTO menu = validateMenuExists(createReqVO.getMenuId());
        validateMenuConfigNotExists(menu);
        validateConfigCodeUnique(createReqVO.getConfigCode(), null);
        validatePageCodeUnique(createReqVO.getPageCode(), null);

        PageConfigDO pageConfig = BeanUtils.toBean(createReqVO, PageConfigDO.class);
        pageConfig.setConfig(castConfig(createReqVO.getConfig()));
        if (StrUtil.isBlank(pageConfig.getConfigCode())) {
            pageConfig.setConfigCode(generateConfigCode(createReqVO.getPageCode(), null));
        }
        pageConfigMapper.insert(pageConfig);

        updateMenuPageConfigId(menu.getId(), pageConfig.getId());
        return pageConfig.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePageConfig(PageConfigSaveReqVO updateReqVO) {
        PageConfigDO existing = validatePageConfigExists(updateReqVO.getId());
        validateConfigCodeUnique(updateReqVO.getConfigCode(), existing.getId());
        validatePageCodeUnique(updateReqVO.getPageCode(), existing.getId());

        if (!ObjUtil.equal(existing.getMenuId(), updateReqVO.getMenuId())) {
            MenuRespDTO newMenu = validateMenuExists(updateReqVO.getMenuId());
            validateMenuConfigNotExists(newMenu);
            clearMenuPageConfig(existing.getId());
            updateMenuPageConfigId(newMenu.getId(), existing.getId());
        }

        PageConfigDO updateObj = BeanUtils.toBean(updateReqVO, PageConfigDO.class);
        updateObj.setConfig(castConfig(updateReqVO.getConfig()));
        pageConfigMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePageConfigByMenuId(Long menuId, Map<String, Object> config) {
        PageConfigDO pageConfig = getPageConfigByMenuId(menuId);
        if (pageConfig == null) {
            throw exception(PAGE_CONFIG_NOT_EXISTS);
        }
        PageConfigDO updateObj = new PageConfigDO();
        updateObj.setId(pageConfig.getId());
        updateObj.setConfig(castConfig(config));
        pageConfigMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePageConfig(Long id) {
        PageConfigDO pageConfig = validatePageConfigExists(id);
        pageConfigMapper.deleteById(id);
        clearMenuPageConfig(pageConfig.getId());
        clearPageRelation(pageConfig.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePageWithMenu(Long pageConfigId) {
        PageConfigDO pageConfig = validatePageConfigExists(pageConfigId);
        PageDO page = pageMapper.selectOne(new LambdaQueryWrapperX<PageDO>()
                .eq(PageDO::getPageConfigId, pageConfigId));
        Long menuId = page != null ? page.getMenuId() : null;
        if (menuId == null && pageConfig.getMenuId() != null && pageConfig.getMenuId() > 0) {
            menuId = pageConfig.getMenuId();
        }
        if (menuId != null) {
            menuApi.deleteMenu(menuId);
            return;
        }
        deletePageConfig(pageConfigId);
    }

    @Override
    public void deletePageConfigByMenuId(Long menuId) {
        PageConfigDO pageConfig = getPageConfigByMenuId(menuId);
        if (pageConfig != null) {
            deletePageConfig(pageConfig.getId());
        }
    }

    @Override
    public PageConfigDO getPageConfig(Long id) {
        return pageConfigMapper.selectById(id);
    }

    @Override
    public PageConfigDO getPageConfigByMenuId(Long menuId) {
        PageDO page = pageMapper.selectOne(new LambdaQueryWrapperX<PageDO>()
                .eq(PageDO::getMenuId, menuId));
        if (page != null && page.getPageConfigId() != null) {
            return pageConfigMapper.selectById(page.getPageConfigId());
        }
        return pageConfigMapper.selectOne(new LambdaQueryWrapperX<PageConfigDO>()
                .eq(PageConfigDO::getMenuId, menuId));
    }

    @Override
    public PageConfigDO getPageConfigByPageCode(String pageCode) {
        return pageConfigMapper.selectByPageCode(pageCode);
    }

    @Override
    public List<PageConfigDO> getPageConfigListByPageType(String pageType) {
        return pageConfigMapper.selectListByPageType(pageType);
    }

    @Override
    public List<PageConfigDO> getPageConfigListByBusinessType(String businessType) {
        return pageConfigMapper.selectListByBusinessType(businessType);
    }

    @Override
    public PageResult<PageConfigDO> getPageConfigPage(PageConfigPageReqVO pageReqVO) {
        return pageConfigMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long savePageConfig(PageConfigSaveReqVO saveReqVO) {
        PageConfigDO existing = getPageConfigByMenuId(saveReqVO.getMenuId());
        if (existing == null) {
            return createPageConfig(saveReqVO);
        }
        saveReqVO.setId(existing.getId());
        updatePageConfig(saveReqVO);
        return existing.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDraft(PageConfigDraftCreateReqVO createReqVO) {
        PageConfigDO pageConfig = new PageConfigDO();
        pageConfig.setMenuId(0L);
        pageConfig.setPageType(createReqVO.getPageType());
        pageConfig.setPageCode(createReqVO.getPageCode());
        pageConfig.setConfig(castConfig(createReqVO.getConfig()));
        pageConfig.setConfigCode(StrUtil.blankToDefault(createReqVO.getConfigCode(),
                generateConfigCode(createReqVO.getPageCode(), null)));
        validateConfigCodeUnique(pageConfig.getConfigCode(), null);
        validatePageCodeUnique(pageConfig.getPageCode(), null);
        pageConfigMapper.insert(pageConfig);
        return pageConfig.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePageConfigById(PageConfigUpdateConfigByIdReqVO updateReqVO) {
        PageConfigDO pageConfig = validatePageConfigExists(updateReqVO.getId());
        PageConfigDO updateObj = new PageConfigDO();
        updateObj.setId(pageConfig.getId());
        updateObj.setConfig(castConfig(updateReqVO.getConfig()));
        pageConfigMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publish(PageConfigPublishReqVO reqVO) {
        PageConfigDO pageConfig = validatePageConfigExists(reqVO.getPageConfigId());
        String pageCode = resolvePageCode(reqVO.getPageCode(), pageConfig.getPageCode(), reqVO.getBusinessType(), pageConfig.getId());
        validatePageCodeUnique(pageCode, pageConfig.getId());
        validatePageCodeUniqueForPage(pageCode, null);

        Long parentMenuId = resolveParentMenuId(reqVO.getParentMenuId(), reqVO.getBusinessType());
        String menuPath = buildMenuPath(parentMenuId, pageCode, reqVO.getBusinessType());

        MenuSaveReqDTO menuSaveReqDTO = new MenuSaveReqDTO();
        menuSaveReqDTO.setName(reqVO.getPageName());
        menuSaveReqDTO.setType(MenuTypeEnum.MENU.getType());
        menuSaveReqDTO.setSort(0);
        menuSaveReqDTO.setParentId(parentMenuId);
        menuSaveReqDTO.setPath(menuPath);
        menuSaveReqDTO.setIcon(reqVO.getIcon());
        menuSaveReqDTO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        menuSaveReqDTO.setVisible(true);
        menuSaveReqDTO.setAlwaysShow(false);
        Long menuId = menuApi.createMenu(menuSaveReqDTO).getData();

        updateMenuPageConfigId(menuId, pageConfig.getId());
        updatePageConfigLink(pageConfig.getId(), menuId, pageCode);
        createOrUpdatePage(pageConfig, reqVO, menuId, parentMenuId, menuPath, pageCode);

        return menuId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApplyTemplateRespVO applyTemplate(ApplyTemplateReqVO reqVO) {
        Map<String, Object> config = buildTemplateConfig(reqVO);
        String pageType = resolveTemplatePageType(reqVO.getTemplateId());
        String pageCode = resolvePageCode(reqVO.getPageCode(), null, reqVO.getBusinessType(), null);
        validatePageCodeUnique(pageCode, null);
        validatePageCodeUniqueForPage(pageCode, null);

        Long menuId;
        boolean menuCreated = false;
        String menuPath;

        if (reqVO.getMenuId() != null && reqVO.getMenuId() > 0) {
            MenuRespDTO menu = validateMenuExists(reqVO.getMenuId());
            validateMenuConfigNotExists(menu);
            menuId = menu.getId();
            menuPath = menu.getPath();
        } else {
            Long parentMenuId = resolveParentMenuId(reqVO.getParentMenuId(), reqVO.getBusinessType());
            menuPath = buildMenuPath(parentMenuId, pageCode, reqVO.getBusinessType());
            MenuSaveReqDTO menuSaveReqDTO = new MenuSaveReqDTO();
            menuSaveReqDTO.setName(reqVO.getPageName());
            menuSaveReqDTO.setType(MenuTypeEnum.MENU.getType());
            menuSaveReqDTO.setSort(0);
            menuSaveReqDTO.setParentId(parentMenuId);
            menuSaveReqDTO.setPath(menuPath);
            menuSaveReqDTO.setStatus(CommonStatusEnum.ENABLE.getStatus());
            menuSaveReqDTO.setVisible(true);
            menuSaveReqDTO.setAlwaysShow(false);
            menuId = menuApi.createMenu(menuSaveReqDTO).getData();
            menuCreated = true;
        }

        PageConfigDO pageConfig = new PageConfigDO();
        pageConfig.setMenuId(menuId);
        pageConfig.setPageType(pageType);
        pageConfig.setPageCode(pageCode);
        pageConfig.setConfig(castConfig(config));
        pageConfig.setConfigCode(generateConfigCode(pageCode, reqVO.getBusinessType()));
        pageConfigMapper.insert(pageConfig);

        updateMenuPageConfigId(menuId, pageConfig.getId());
        createOrUpdatePage(pageConfig, reqVO.getPageName(), reqVO.getBusinessType(), menuId,
                resolveParentMenuId(reqVO.getParentMenuId(), reqVO.getBusinessType()), menuPath, pageCode, 0);

        ApplyTemplateRespVO respVO = new ApplyTemplateRespVO();
        respVO.setMenuId(menuId);
        respVO.setMenuCreated(menuCreated);
        respVO.setMenuPath(menuPath);
        respVO.setMenuName(reqVO.getPageName());
        respVO.setPageConfigId(pageConfig.getId());
        return respVO;
    }

    private Map<String, Object> buildTemplateConfig(ApplyTemplateReqVO reqVO) {
        Map<String, Object> config = new HashMap<>();
        config.put("businessType", reqVO.getBusinessType());
        config.put("pageName", reqVO.getPageName());
        config.put("tabs", reqVO.getTabs());
        if (CollUtil.isNotEmpty(reqVO.getFields())) {
            config.put("fields", reqVO.getFields());
        }
        if (reqVO.getAdditionalConfig() != null) {
            config.putAll(reqVO.getAdditionalConfig());
        }
        return config;
    }

    private String resolveTemplatePageType(String templateId) {
        if (StrUtil.isBlank(templateId)) {
            return PageConfigDO.PAGE_TYPE_DATA_MANAGEMENT;
        }
        var template = pageTemplateService.getTemplateById(templateId);
        if (template == null || StrUtil.isBlank(template.getPageType())) {
            return PageConfigDO.PAGE_TYPE_DATA_MANAGEMENT;
        }
        return template.getPageType();
    }

    private void createOrUpdatePage(PageConfigDO pageConfig, PageConfigPublishReqVO reqVO, Long menuId,
                                    Long parentMenuId, String menuPath, String pageCode) {
        createOrUpdatePage(pageConfig, reqVO.getPageName(), reqVO.getBusinessType(), menuId, parentMenuId, menuPath, pageCode, 1);
    }

    private void createOrUpdatePage(PageConfigDO pageConfig, String pageName, String businessType,
                                    Long menuId, Long parentMenuId, String menuPath, String pageCode, Integer status) {
        PageDO existing = pageMapper.selectOne(new LambdaQueryWrapperX<PageDO>()
                .eq(PageDO::getPageConfigId, pageConfig.getId()));

        PageSaveReqVO saveReqVO = new PageSaveReqVO();
        if (existing != null) {
            saveReqVO.setId(existing.getId());
        }
        saveReqVO.setPageCode(pageCode);
        saveReqVO.setPageName(pageName);
        saveReqVO.setPageType(pageConfig.getPageType());
        saveReqVO.setStatus(status);
        saveReqVO.setParentMenuId(parentMenuId);
        saveReqVO.setMenuId(menuId);
        saveReqVO.setPageConfigId(pageConfig.getId());
        saveReqVO.setIcon(pageConfig.getConfig() != null && pageConfig.getConfig().get("icon") != null
                ? pageConfig.getConfig().get("icon").toString()
                : null);
        saveReqVO.setRoutePath(menuPath);
        saveReqVO.setRemark(StrUtil.format("businessType:{}", businessType));

        if (existing == null) {
            pageService.create(saveReqVO);
        } else {
            pageService.update(saveReqVO);
        }
    }

    private PageConfigDO validatePageConfigExists(Long id) {
        PageConfigDO pageConfig = pageConfigMapper.selectById(id);
        if (pageConfig == null) {
            throw exception(PAGE_CONFIG_NOT_EXISTS);
        }
        return pageConfig;
    }

    private MenuRespDTO validateMenuExists(Long menuId) {
        MenuRespDTO menu = menuApi.getMenu(menuId).getData();
        if (menu == null) {
            throw exception(MENU_NOT_EXISTS);
        }
        return menu;
    }

    private void validateMenuConfigNotExists(MenuRespDTO menu) {
        if (menu == null) {
            return;
        }
        PageDO existingPage = pageMapper.selectOne(new LambdaQueryWrapperX<PageDO>()
                .eq(PageDO::getMenuId, menu.getId()));
        if (existingPage != null && existingPage.getPageConfigId() != null) {
            throw exception(PAGE_CONFIG_MENU_ID_DUPLICATE);
        }
        PageConfigDO existingConfig = pageConfigMapper.selectOne(new LambdaQueryWrapperX<PageConfigDO>()
                .eq(PageConfigDO::getMenuId, menu.getId()));
        if (existingConfig != null) {
            throw exception(PAGE_CONFIG_MENU_ID_DUPLICATE);
        }
    }

    private void validateConfigCodeUnique(String configCode, Long excludeId) {
        if (StrUtil.isBlank(configCode)) {
            return;
        }
        if (pageConfigMapper.existsByConfigCode(configCode, excludeId)) {
            throw exception(PAGE_CONFIG_MENU_ID_DUPLICATE);
        }
    }

    private void validatePageCodeUnique(String pageCode, Long excludeId) {
        if (StrUtil.isBlank(pageCode)) {
            return;
        }
        if (pageConfigMapper.existsByPageCode(pageCode, excludeId)) {
            throw exception(PAGE_CONFIG_PAGE_CODE_EXISTS, pageCode);
        }
    }

    private void validatePageCodeUniqueForPage(String pageCode, Long excludeId) {
        if (StrUtil.isBlank(pageCode)) {
            return;
        }
        if (pageMapper.existsByPageCode(pageCode, excludeId)) {
            throw exception(PAGE_CONFIG_PAGE_CODE_EXISTS, pageCode);
        }
    }

    private void updateMenuPageConfigId(Long menuId, Long pageConfigId) {
        PageConfigDO updateConfig = new PageConfigDO();
        updateConfig.setId(pageConfigId);
        updateConfig.setMenuId(menuId);
        pageConfigMapper.updateById(updateConfig);
        PageDO page = pageMapper.selectOne(new LambdaQueryWrapperX<PageDO>()
                .eq(PageDO::getMenuId, menuId));
        if (page != null) {
            PageDO updatePage = new PageDO();
            updatePage.setId(page.getId());
            updatePage.setPageConfigId(pageConfigId);
            pageMapper.updateById(updatePage);
        }
    }

    private void clearMenuPageConfig(Long pageConfigId) {
        PageConfigDO updateConfig = new PageConfigDO();
        updateConfig.setId(pageConfigId);
        updateConfig.setMenuId(0L);
        pageConfigMapper.updateById(updateConfig);
        List<PageDO> pages = pageMapper.selectList(new LambdaQueryWrapperX<PageDO>()
                .eq(PageDO::getPageConfigId, pageConfigId));
        if (CollUtil.isEmpty(pages)) {
            return;
        }
        for (PageDO page : pages) {
            PageDO updatePage = new PageDO();
            updatePage.setId(page.getId());
            updatePage.setPageConfigId(null);
            pageMapper.updateById(updatePage);
        }
    }

    private void clearPageRelation(Long pageConfigId) {
        List<PageDO> pages = pageMapper.selectList(new LambdaQueryWrapperX<PageDO>()
                .eq(PageDO::getPageConfigId, pageConfigId));
        if (CollUtil.isEmpty(pages)) {
            return;
        }
        for (PageDO page : pages) {
            pageMapper.deleteById(page.getId());
        }
    }

    private void updatePageConfigLink(Long pageConfigId, Long menuId, String pageCode) {
        PageConfigDO updateObj = new PageConfigDO();
        updateObj.setId(pageConfigId);
        updateObj.setMenuId(menuId);
        updateObj.setPageCode(pageCode);
        pageConfigMapper.updateById(updateObj);
    }

    private Long resolveParentMenuId(Long parentMenuId, String businessType) {
        if (parentMenuId != null) {
            return parentMenuId;
        }
        return 0L;
    }

    private String buildMenuPath(Long parentMenuId, String pageCode, String businessType) {
        String suffix = StrUtil.isNotBlank(pageCode) ? pageCode : "page";
        if (parentMenuId == null || ObjUtil.equal(parentMenuId, 0L)) {
            return "/" + StrUtil.blankToDefault(businessType, "page") + "/" + suffix;
        }
        return "/" + StrUtil.blankToDefault(businessType, "page") + "/" + suffix;
    }

    private String resolvePageCode(String requestCode, String existingCode, String businessType, Long pageConfigId) {
        if (StrUtil.isNotBlank(requestCode)) {
            return requestCode;
        }
        if (StrUtil.isNotBlank(existingCode)) {
            return existingCode;
        }
        if (pageConfigId != null) {
            return StrUtil.format("{}-{}", StrUtil.blankToDefault(businessType, "page"), pageConfigId);
        }
        return StrUtil.format("{}-{}", StrUtil.blankToDefault(businessType, "page"), System.currentTimeMillis());
    }

    private String generateConfigCode(String pageCode, String businessType) {
        if (StrUtil.isNotBlank(pageCode)) {
            return StrUtil.format("{}-{}", StrUtil.blankToDefault(businessType, "page"), pageCode);
        }
        return StrUtil.format("page_config_{}", System.currentTimeMillis());
    }

    private Map<String, Serializable> castConfig(Map<String, Object> config) {
        if (config == null) {
            return null;
        }
        Map<String, Serializable> result = new HashMap<>();
        config.forEach((key, value) -> result.put(key, (Serializable) value));
        return result;
    }
}
