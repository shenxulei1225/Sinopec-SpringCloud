package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryDeleteReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.business.BusinessDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.business.BusinessEntryDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabColumnRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabLayoutDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmCatalogOrchestrationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.business.BusinessEntryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.business.BusinessMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmDataTabColumnRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmDataTabLayoutMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmCatalogOrchestrationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.EntityTypeEntryKindEnum;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeContext;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 下线子数据类型（DOMAIN）门面。
 * <p>
 * 管什么：用户不要某扇业务域门时，拆掉注册项/门户/布局/域分组，并把该域下型号归回底座未划域。
 * 不管什么：不删型号、不删实例、不删底座 NATIVE 台账；不处理 SCOPE/REUSE/NATIVE 删除。
 * 禁止：软删/硬删型号；把多型号并成某一个「标准型号」；因下线 DOMAIN 而删 {@code ent_*} 行。
 * </p>
 * <p>
 * 权威路径：型号 {@code changeDomain(__none__)}（级联实体行 domain 与分类关联镜像）→ 软删域分组 →
 * 软删本目录布局/栏关系/编排头 → 软删同编码门户 → 注册项 status=inactive 后逻辑删。
 * </p>
 */
@Service
@Slf4j
public class DomainEntityTypeRetireService {

    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ModelService modelService;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private CategoryService categoryService;
    @Resource
    private BusinessMapper businessMapper;
    @Resource
    private BusinessEntryMapper businessEntryMapper;
    @Resource
    private DmDataTabLayoutMapper dmDataTabLayoutMapper;
    @Resource
    private DmDataTabColumnRelationMapper dmDataTabColumnRelationMapper;
    @Resource
    private DmCatalogOrchestrationMapper catalogOrchestrationMapper;
    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 下线一扇 DOMAIN 门。调用方须已确认目标是 DOMAIN 且非系统级。
     *
     * @param domainType 侧边栏子数据类型注册项（含 registry code、底座编码、业务域）
     */
    @Transactional(rollbackFor = Exception.class)
    public void retire(EntityTypeDO domainType) {
        if (domainType == null || domainType.getId() == null) {
            throw new ServiceException(400, "子数据类型不存在");
        }
        EntityTypeEntryKindEnum kind = EntityTypeEntryKindEnum.fromCode(domainType.getEntryKind());
        if (!kind.isDomainEntry()) {
            throw new ServiceException(400, "仅子数据类型支持按业务域下线");
        }
        if (!StringUtils.hasText(domainType.getBaseEntityTypeCode())) {
            throw new ServiceException(400, "子数据类型缺少基础数据类型编码，无法下线");
        }
        String storage = domainType.getBaseEntityTypeCode().trim();
        String domain = EntityTypeScopeContext.normalizeDomain(domainType.getDomain());
        if (!StringUtils.hasText(domain)) {
            throw new ServiceException(400, "子数据类型缺少业务域标识，无法下线");
        }
        String registryCode = domainType.getCode().trim();

        // 1) 型号归位：该底座 + 该业务域下的型号清空 domain；实例随型号保留
        undomainModels(storage, domain);

        // 2) 域分组节点 {registry}_dir（级联软删，含 status=0）
        softDeleteDomainCategoryFolder(registryCode, storage);

        // 3) 本目录数据页 / 模型管理布局 / 栏关系 / 编排头
        softDeleteCatalogLayouts(
                registryCode, domainType.getDataLayoutId(), domainType.getModelLayoutId());

        // 4) 同编码门户叶子（若有）
        softDeletePortalByCode(registryCode);

        // 5) 可能存在的类型配置行（DOMAIN 通常无独立建表配置）
        softDeleteEntityTypeConfig(registryCode);

        // 6) 注册项：先停用再逻辑删
        EntityTypeDO patch = new EntityTypeDO();
        patch.setId(domainType.getId());
        patch.setStatus(EntityTypeDO.STATUS_INACTIVE);
        patch.setDataLayoutId(null);
        patch.setModelLayoutId(null);
        entityTypeMapper.updateById(patch);
        entityTypeMapper.deleteById(domainType.getId());

        log.info("DOMAIN 下线完成: registry={}, storage={}, domain={}, id={}",
                registryCode, storage, domain, domainType.getId());
    }

    private void undomainModels(String storageEntityTypeCode, String domain) {
        List<ModelDO> models = modelMapper.selectList(new LambdaQueryWrapperX<ModelDO>()
                .eq(ModelDO::getEntityTypeCode, storageEntityTypeCode));
        int moved = 0;
        for (ModelDO model : models) {
            if (!EntityTypeScopeContext.domainsEqual(model.getDomain(), domain)) {
                continue;
            }
            // 复用型号改业务域权威链；__none__ → 清空 domain，级联实体与分类关联
            modelService.changeDomain(model.getId(), EntityTypeScopeContext.DOMAIN_FILTER_NONE);
            moved++;
        }
        log.info("DOMAIN 下线·型号归位: storage={}, domain={}, modelCount={}",
                storageEntityTypeCode, domain, moved);
    }

    private void softDeleteDomainCategoryFolder(String registryCode, String storageCategoryTypeCode) {
        String folderCode = registryCode + "_dir";
        CategoryDO folder = categoryMapper.selectOne(new LambdaQueryWrapperX<CategoryDO>()
                .eq(CategoryDO::getCode, folderCode)
                .eq(CategoryDO::getCategoryTypeCode, storageCategoryTypeCode)
                .eq(CategoryDO::getDeleted, false));
        if (folder == null) {
            return;
        }
        CategoryDeleteReqVO req = new CategoryDeleteReqVO();
        req.setId(folder.getId());
        req.setCategoryTypeCode(storageCategoryTypeCode);
        req.setCascade(true);
        req.setForceDelete(true);
        categoryService.deleteCategory(req);
    }

    private void softDeleteCatalogLayouts(String registryCode, Long dataLayoutId, Long modelLayoutId) {
        List<DmDataTabLayoutDO> layouts = dmDataTabLayoutMapper.selectList(new LambdaQueryWrapperX<DmDataTabLayoutDO>()
                .eq(DmDataTabLayoutDO::getEntityTypeCode, registryCode)
                .eq(DmDataTabLayoutDO::getDeleted, false));
        appendLayoutsByLayoutId(layouts, dataLayoutId);
        appendLayoutsByLayoutId(layouts, modelLayoutId);
        for (DmDataTabLayoutDO row : layouts) {
            dmDataTabLayoutMapper.deleteById(row.getId());
        }

        List<DmDataTabColumnRelationDO> relations = dmDataTabColumnRelationMapper.selectList(
                new LambdaQueryWrapperX<DmDataTabColumnRelationDO>()
                        .eq(DmDataTabColumnRelationDO::getEntityTypeCode, registryCode)
                        .eq(DmDataTabColumnRelationDO::getDeleted, false));
        appendRelationsByLayoutId(relations, dataLayoutId);
        appendRelationsByLayoutId(relations, modelLayoutId);
        for (DmDataTabColumnRelationDO row : relations) {
            dmDataTabColumnRelationMapper.deleteById(row.getId());
        }

        DmCatalogOrchestrationDO orchestration = catalogOrchestrationMapper.selectByEntityTypeCode(registryCode);
        if (orchestration != null) {
            catalogOrchestrationMapper.deleteById(orchestration.getId());
        }
    }

    private void appendLayoutsByLayoutId(List<DmDataTabLayoutDO> layouts, Long layoutId) {
        if (layoutId == null) {
            return;
        }
        for (DmDataTabLayoutDO row : dmDataTabLayoutMapper.selectListByLayoutId(layoutId)) {
            if (layouts.stream().noneMatch(existing -> Objects.equals(existing.getId(), row.getId()))) {
                layouts.add(row);
            }
        }
    }

    private void appendRelationsByLayoutId(List<DmDataTabColumnRelationDO> relations, Long layoutId) {
        if (layoutId == null) {
            return;
        }
        for (DmDataTabColumnRelationDO row : dmDataTabColumnRelationMapper.selectListByLayoutId(layoutId)) {
            if (relations.stream().noneMatch(existing -> Objects.equals(existing.getId(), row.getId()))) {
                relations.add(row);
            }
        }
    }

    private void softDeletePortalByCode(String registryCode) {
        BusinessDO business = businessMapper.selectByCode(registryCode);
        if (business == null) {
            return;
        }
        if (businessMapper.selectCountByParentId(business.getId()) > 0) {
            throw new ServiceException(400, "门户业务「" + registryCode + "」下仍有子节点，请先处理后再删除子数据类型");
        }
        List<BusinessEntryDO> entries = businessEntryMapper.selectByBusinessId(business.getId());
        for (BusinessEntryDO entry : entries) {
            BusinessEntryDO patch = new BusinessEntryDO();
            patch.setId(entry.getId());
            patch.setStatus(BusinessEntryDO.STATUS_INACTIVE);
            businessEntryMapper.updateById(patch);
            businessEntryMapper.deleteById(entry.getId());
        }
        BusinessDO patch = new BusinessDO();
        patch.setId(business.getId());
        patch.setStatus(BusinessDO.STATUS_INACTIVE);
        businessMapper.updateById(patch);
        businessMapper.deleteById(business.getId());
    }

    private void softDeleteEntityTypeConfig(String registryCode) {
        // 无独立 DO；与废止脚本同口径，仅软删可能存在的配置行
        jdbcTemplate.update(
                """
                        UPDATE dynamicbusiness.dynamic_entity_type_config
                        SET deleted = true, status = 0,
                            updater = 'domain-retire', update_time = CURRENT_TIMESTAMP
                        WHERE entity_type_code = ? AND deleted = false
                        """,
                registryCode);
    }
}
