package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTypeCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabLayoutDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmDataTabLayoutMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.enums.datamgmt.DmDataTabLayoutKindEnum;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.EntityTypeEntryKindEnum;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryModeSupport;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryTypeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 为数据类型自动创建默认分类体系与数据管理数据 Tab 布局，避免用户手工初始化。
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
    private DmDataTabLayoutMapper dmDataTabLayoutMapper;

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
        ensureLayoutRows(entityType);
    }

    private void ensureCategoryType(EntityTypeDO entityType) {
        EntityTypeEntryKindEnum kind = EntityTypeEntryKindEnum.fromCode(entityType.getEntryKind());
        // 分类绑定实体：同编码高级分类（节点即台账）
        if (kind.isCategory()) {
            ensureAdvancedCategoryType(entityType);
            return;
        }
        if (kind.isDomainEntry()) {
            String baseCode = entityType.getBaseEntityTypeCode();
            if (!StringUtils.hasText(baseCode)) {
                log.warn("子数据类型 {} 缺少基础数据类型编码，跳过分类 bootstrap", entityType.getCode());
                return;
            }
            EntityTypeDO baseType = entityTypeMapper.selectByCode(baseCode.trim());
            if (baseType == null) {
                log.warn("子数据类型 {} 的基础类型 {} 不存在，跳过分类 bootstrap",
                        entityType.getCode(), baseCode);
                return;
            }
            ensureNativeCategoryType(baseType);
            ensureDomainCategoryFolder(entityType, baseType);
            return;
        }
        ensureNativeCategoryType(entityType);
    }

    /** NATIVE 数据类型：按 registry code 自动创建默认分类体系（简单分类）。 */
    private void ensureNativeCategoryType(EntityTypeDO entityType) {
        ensureCategoryTypeRecord(entityType, CategoryModeSupport.SIMPLE, "默认分类");
    }

    /** CATEGORY 入口：同编码高级分类，供树节点 1:1 绑实体。 */
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

    /**
     * DOMAIN 域入口：在主数据分类根下创建域分组节点（code={registryCode}_dir），
     * 供该域下子分类挂载，总入口可按域并列统计。
     */
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

    private void ensureLayoutRows(EntityTypeDO entityType) {
        String code = entityType.getCode();
        EntityTypeEntryKindEnum kind = EntityTypeEntryKindEnum.fromCode(entityType.getEntryKind());
        if (kind.isCategory() || isCategoryAsEntityType(code)) {
            ensureCategoryAsEntityLayoutRows(code, entityType.getName());
            return;
        }
        // DOMAIN：分类树挂在基础类型种类上（与 ensureCategoryType / 域分组一致），不得写注册编码
        if (kind.isDomainEntry()) {
            String baseCode = entityType.getBaseEntityTypeCode();
            if (!StringUtils.hasText(baseCode)) {
                log.warn("子数据类型 {} 缺少基础数据类型编码，跳过布局 bootstrap", code);
                return;
            }
            ensureNativeFourColumnLayoutRows(code, entityType.getName(), baseCode.trim());
            return;
        }
        // 基础数据 / 划分数据：分类 | 型号 | 实体 | 详情（自带同编码分类维）
        ensureNativeFourColumnLayoutRows(code, entityType.getName(), code);
    }

    /**
     * 默认四栏。空布局或旧「仅型号+实体」种子可一次写入；已有 CATEGORY 等不覆盖。
     *
     * @param categoryTypeCode 分类列绑定的种类编码；DOMAIN 必须传基础类型编码，NATIVE/SCOPE 传自身编码
     */
    private void ensureNativeFourColumnLayoutRows(String entityTypeCode, String typeName,
                                                  String categoryTypeCode) {
        List<DmDataTabLayoutDO> existing =
                dmDataTabLayoutMapper.selectListByEntityTypeCode(entityTypeCode);
        if (!existing.isEmpty() && !isLegacyModelEntityOnlySeed(existing)) {
            // 已有非旧种子配置：仅补缺失的 MODEL/ENTITY（不碰 CATEGORY）
            ensureLayoutRow(entityTypeCode, DmDataTabLayoutKindEnum.MODEL.getCode(),
                    null, null, true);
            ensureLayoutRow(entityTypeCode, DmDataTabLayoutKindEnum.ENTITY.getCode(),
                    null, null, true);
            return;
        }
        if (!existing.isEmpty()) {
            for (DmDataTabLayoutDO row : existing) {
                dmDataTabLayoutMapper.deleteById(row.getId());
            }
        }
        String perspectiveId = defaultPerspectiveId(entityTypeCode);
        String resolvedCategoryTypeCode = StringUtils.hasText(categoryTypeCode)
                ? categoryTypeCode.trim() : entityTypeCode;
        Map<String, Object> categoryMeta = Map.of(
                "label", StringUtils.hasText(typeName) ? typeName : entityTypeCode,
                "categoryTypeCode", resolvedCategoryTypeCode
        );
        insertLayoutRow(entityTypeCode, DmDataTabLayoutKindEnum.CATEGORY.getCode(),
                perspectiveId, categoryMeta, true);
        insertLayoutRow(entityTypeCode, DmDataTabLayoutKindEnum.MODEL.getCode(),
                null, null, true);
        insertLayoutRow(entityTypeCode, DmDataTabLayoutKindEnum.ENTITY.getCode(),
                null, null, true);
        insertLayoutRow(entityTypeCode, DmDataTabLayoutKindEnum.DETAIL.getCode(),
                null, null, true);
    }

    /**
     * 分类即实体（如 region）：默认分类栏 + 详情栏；型号/实体默认关。
     * 已有非「旧种子」布局时不覆盖，允许用户用顶栏改显隐。
     */
    private void ensureCategoryAsEntityLayoutRows(String entityTypeCode, String typeName) {
        List<DmDataTabLayoutDO> existing =
                dmDataTabLayoutMapper.selectListByEntityTypeCode(entityTypeCode);
        if (!existing.isEmpty() && !isLegacyModelEntityOnlySeed(existing)) {
            return;
        }
        if (!existing.isEmpty()) {
            // 旧 bootstrap 仅 MODEL+ENTITY：逻辑删除后写入标准两栏默认
            for (DmDataTabLayoutDO row : existing) {
                dmDataTabLayoutMapper.deleteById(row.getId());
            }
        }
        String perspectiveId = defaultPerspectiveId(entityTypeCode);
        Map<String, Object> categoryMeta = Map.of(
                "label", StringUtils.hasText(typeName) ? typeName : entityTypeCode,
                "categoryTypeCode", entityTypeCode
        );
        insertLayoutRow(entityTypeCode, DmDataTabLayoutKindEnum.CATEGORY.getCode(),
                perspectiveId, categoryMeta, true);
        insertLayoutRow(entityTypeCode, DmDataTabLayoutKindEnum.MODEL.getCode(),
                null, null, false);
        insertLayoutRow(entityTypeCode, DmDataTabLayoutKindEnum.ENTITY.getCode(),
                null, null, false);
        insertLayoutRow(entityTypeCode, DmDataTabLayoutKindEnum.DETAIL.getCode(),
                null, null, true);
    }

    /** 与前端 isCategoryAsEntityDefaultType 对齐的已知编码；种类 ADVANCED 同构亦可扩展。 */
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

    private boolean isLegacyModelEntityOnlySeed(List<DmDataTabLayoutDO> rows) {
        boolean hasCategory = false;
        boolean hasModel = false;
        boolean hasEntity = false;
        boolean detailEnabled = false;
        for (DmDataTabLayoutDO row : rows) {
            String kind = row.getColumnKind();
            if (DmDataTabLayoutKindEnum.CATEGORY.getCode().equals(kind)) {
                hasCategory = true;
            } else if (DmDataTabLayoutKindEnum.MODEL.getCode().equals(kind)) {
                hasModel = row.getEnabled() == null || Boolean.TRUE.equals(row.getEnabled());
            } else if (DmDataTabLayoutKindEnum.ENTITY.getCode().equals(kind)) {
                hasEntity = row.getEnabled() == null || Boolean.TRUE.equals(row.getEnabled());
            } else if (DmDataTabLayoutKindEnum.DETAIL.getCode().equals(kind)) {
                detailEnabled = row.getEnabled() == null || Boolean.TRUE.equals(row.getEnabled());
            }
        }
        return !hasCategory && hasModel && hasEntity && !detailEnabled;
    }

    private void ensureLayoutRow(String entityTypeCode, String kind, String perspectiveId,
                                    Map<String, Object> categoryMeta, boolean enabled) {
        if (DmDataTabLayoutKindEnum.CATEGORY.getCode().equals(kind)) {
            return;
        }
        if (dmDataTabLayoutMapper.existsByKind(entityTypeCode, kind)) {
            return;
        }
        try {
            insertLayoutRow(entityTypeCode, kind, perspectiveId, categoryMeta, enabled);
        } catch (DuplicateKeyException e) {
            log.debug("[ensureLayoutRow] 并发初始化已存在, entityTypeCode={}, kind={}, perspectiveId={}",
                    entityTypeCode, kind, perspectiveId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("[ensureLayoutRow] 自动创建数据 Tab 布局失败, entityTypeCode={}, kind={}", entityTypeCode, kind, e);
            throw new ServiceException(500, "自动创建数据管理数据 Tab 布局失败：" + e.getMessage());
        }
    }

    private void insertLayoutRow(String entityTypeCode, String kind, String perspectiveId,
                                    Map<String, Object> categoryMeta, boolean enabled) {
        DmDataTabLayoutDO row = new DmDataTabLayoutDO();
        row.setEntityTypeCode(entityTypeCode);
        row.setColumnKind(kind);
        row.setPerspectiveId(perspectiveId);
        row.setEnabled(enabled);
        row.setCategoryColumn(categoryMeta);
        // propsId 由前端创建成功后的 seedDataMgmtColumnProps 写入（各列独占一份 component-props）。
        // 此处只保证布局行存在；勿在未分配 propsId 时假定列配置已就绪。
        dmDataTabLayoutMapper.insert(row);
    }

    public static String defaultPerspectiveId(String entityTypeCode) {
        return entityTypeCode.trim() + "-default";
    }
}
