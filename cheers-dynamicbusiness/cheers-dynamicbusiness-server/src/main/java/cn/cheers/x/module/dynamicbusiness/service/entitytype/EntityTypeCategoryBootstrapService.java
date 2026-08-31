package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTypeCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.EntityTypeEntryKindEnum;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryModeSupport;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryTypeService;
import cn.cheers.x.module.dynamicbusiness.service.datamgmt.DmWorkbenchLayoutService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 为数据类型自动创建默认分类体系，并从工作台布局模版生成「数据」页签实例。
 */
@Service
@Slf4j
public class EntityTypeCategoryBootstrapService {

    @Resource
    private EntityTypeMapper entityTypeMapper;

    @Resource
    private CategoryTypeService categoryTypeService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private CategoryTypeMapper categoryTypeMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private DmWorkbenchLayoutService dmWorkbenchLayoutService;

    @Transactional(rollbackFor = Exception.class)
    public void ensureForEntityTypeCode(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            return;
        }
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode.trim());
        if (entityType == null) {
            return;
        }
        ensureCategoryType(entityType);
        ensureCatalogDataLayout(entityType);
    }

    private void ensureCategoryType(EntityTypeDO entityType) {
        EntityTypeEntryKindEnum kind = EntityTypeEntryKindEnum.fromCode(entityType.getEntryKind());
        if (kind.isCategory()) {
            ensureAdvancedCategoryType(entityType);
            return;
        }
        // DOMAIN / SCOPE / REUSE：共用底座分类种类，不为注册项另建 categoryType
        if (kind.reusesBaseStorage()) {
            EntityTypeDO baseType = requireBaseEntityType(entityType, kind);
            if (baseType == null) {
                return;
            }
            ensureNativeCategoryType(baseType);
            // 仅 DOMAIN 在底座分类根下建域分组；SCOPE / REUSE 直接用底座树，不新建根
            if (kind.isDomainEntry()) {
                ensureDomainCategoryFolder(entityType, baseType);
            }
            return;
        }
        ensureNativeCategoryType(entityType);
    }

    /**
     * 解析复用底座存储的入口（DOMAIN / SCOPE / REUSE）的基础数据类型；缺编码或底座不存在时打日志并返回 null。
     */
    private EntityTypeDO requireBaseEntityType(EntityTypeDO entityType, EntityTypeEntryKindEnum kind) {
        String baseCode = entityType.getBaseEntityTypeCode();
        if (!StringUtils.hasText(baseCode)) {
            log.warn("{} {} 缺少基础数据类型编码，跳过分类 bootstrap",
                    kindLabel(kind), entityType.getCode());
            return null;
        }
        EntityTypeDO baseType = entityTypeMapper.selectByCode(baseCode.trim());
        if (baseType == null) {
            log.warn("{} {} 的基础类型 {} 不存在，跳过分类 bootstrap",
                    kindLabel(kind), entityType.getCode(), baseCode);
            return null;
        }
        return baseType;
    }

    private static String kindLabel(EntityTypeEntryKindEnum kind) {
        if (kind.isDomainEntry()) {
            return "子数据类型";
        }
        if (kind.isScopeEntry()) {
            return "划分数据";
        }
        if (kind.isReuseEntry()) {
            return "使用已有数据";
        }
        return kind.getCode();
    }

    private void ensureNativeCategoryType(EntityTypeDO entityType) {
        ensureCategoryTypeRecord(entityType, CategoryModeSupport.SIMPLE, "默认分类");
    }

    private void ensureAdvancedCategoryType(EntityTypeDO entityType) {
        ensureCategoryTypeRecord(entityType, CategoryModeSupport.ADVANCED, "分类绑定实体默认高级分类");
    }

    private void ensureCategoryTypeRecord(EntityTypeDO entityType, String categoryMode, String descSuffix) {
        String code = entityType.getCode();
        if (categoryTypeService.existsByCategoryTypeCode(code, null)) {
            return;
        }
        boolean hasTopLevelCategory = categoryMapper.selectByCategoryTypeCode(code).stream()
                .anyMatch(item -> item.getParentId() == null);
        if (hasTopLevelCategory) {
            log.warn("数据类型 {} 已有顶层分类节点但缺少分类类型记录，跳过自动创建分类类型", code);
            return;
        }

        CategoryTypeCreateReqVO req = new CategoryTypeCreateReqVO();
        req.setCategoryTypeCode(code);
        req.setName(entityType.getName());
        req.setDescription("数据类型「" + entityType.getName() + "」" + descSuffix);
        req.setStatus(1);
        req.setCategoryMode(categoryMode);
        try {
            categoryTypeService.createCategoryType(req);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("[ensureCategoryTypeRecord] 自动创建分类类型失败, entityTypeCode={}, mode={}",
                    code, categoryMode, e);
            throw new ServiceException(500, "自动创建默认分类失败：" + e.getMessage());
        }
    }

    private void ensureDomainCategoryFolder(EntityTypeDO domainType, EntityTypeDO baseType) {
        String baseCategoryTypeCode = baseType.getCode().trim();
        String folderCode = domainType.getCode().trim() + "_dir";
        boolean folderExists = categoryMapper.selectByCategoryTypeCode(baseCategoryTypeCode).stream()
                .anyMatch(item -> folderCode.equals(item.getCode()));
        if (folderExists) {
            return;
        }

        CategoryTypeDO categoryType = categoryTypeMapper.selectByCategoryTypeCode(baseCategoryTypeCode);
        if (categoryType == null || categoryType.getTopLevelCategoryId() == null) {
            log.warn("DOMAIN 数据类型 {} 的基础分类 {} 未配置顶层节点，跳过域分组创建",
                    domainType.getCode(), baseCategoryTypeCode);
            return;
        }

        CategoryCreateReqVO req = new CategoryCreateReqVO();
        req.setCategoryTypeCode(baseCategoryTypeCode);
        req.setParentId(categoryType.getTopLevelCategoryId());
        req.setCode(folderCode);
        req.setName(domainType.getName());
        req.setStatus(1);
        try {
            categoryService.createCategory(req);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("[ensureDomainCategoryFolder] 创建域分组失败, domain={}, base={}",
                    domainType.getCode(), baseCategoryTypeCode, e);
            throw new ServiceException(500, "自动创建域分类分组失败：" + e.getMessage());
        }
    }

    /**
     * 从通用台账模版生成「数据」页签布局实例，写回 {@code data_layout_id}。
     * 已有实例则不覆盖。
     * <p>
     * NATIVE 用本注册码作同类类型；DOMAIN / SCOPE / REUSE 用底座编码，保证分类/型号/实体一次写全类型码。
     */
    private void ensureCatalogDataLayout(EntityTypeDO entityType) {
        String code = entityType.getCode();
        EntityTypeEntryKindEnum kind = EntityTypeEntryKindEnum.fromCode(entityType.getEntryKind());
        boolean categoryAsEntity = kind.isCategory() || isCategoryAsEntityType(code);
        // 默认布局分类栏：NATIVE/CATEGORY 用本注册项；DOMAIN/SCOPE/REUSE 用底座分类种类
        String categoryTypeCode = code;
        if (kind.reusesBaseStorage()) {
            String baseCode = entityType.getBaseEntityTypeCode();
            if (!StringUtils.hasText(baseCode)) {
                log.warn("{} {} 缺少基础数据类型编码，跳过布局 bootstrap",
                        kindLabel(kind), code);
                return;
            }
            categoryTypeCode = baseCode.trim();
        }
        try {
            dmWorkbenchLayoutService.ensureCatalogDataLayout(entityType, categoryTypeCode, categoryAsEntity);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("[ensureCatalogDataLayout] 从模版生成布局失败, entityTypeCode={}", code, e);
            throw new ServiceException(500, "自动创建数据管理工作台布局失败：" + e.getMessage());
        }
    }

    private boolean isCategoryAsEntityType(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            return false;
        }
        String code = entityTypeCode.trim();
        if ("region".equalsIgnoreCase(code)
                || "facility".equalsIgnoreCase(code)
                || "zone".equalsIgnoreCase(code)) {
            return true;
        }
        CategoryTypeDO categoryType = categoryTypeMapper.selectByCategoryTypeCode(code);
        if (categoryType == null || !StringUtils.hasText(categoryType.getCategoryMode())) {
            return false;
        }
        String mode = categoryType.getCategoryMode().trim().toUpperCase();
        return "ADVANCED".equals(mode) || "ORG_RECORD".equals(mode) || "PATTERN_C".equals(mode);
    }

    /**
     * 新建分类栏默认 tabId：与实例化布局一致，{种类码}-1；禁止 {种类码}-default。
     */
    public static String defaultTabId(String entityTypeCode) {
        return entityTypeCode.trim() + "-1";
    }
}
