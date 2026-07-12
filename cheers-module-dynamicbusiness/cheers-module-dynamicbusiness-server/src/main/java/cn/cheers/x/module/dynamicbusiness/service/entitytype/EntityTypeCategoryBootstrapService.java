package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTypeCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmEntityDimensionDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmEntityDimensionMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.enums.datamgmt.DmDimensionKindEnum;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryTypeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 为数据类型自动创建默认分类体系与数据管理浏览维度，避免用户手工初始化。
 */
@Service
@Slf4j
public class EntityTypeCategoryBootstrapService {

    @Resource
    private EntityTypeMapper entityTypeMapper;

    @Resource
    private CategoryTypeService categoryTypeService;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private DmEntityDimensionMapper dmEntityDimensionMapper;

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
        ensureDimensionRows(entityType);
    }

    private void ensureCategoryType(EntityTypeDO entityType) {
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
        req.setDescription("数据类型「" + entityType.getName() + "」默认分类");
        req.setStatus(1);
        try {
            categoryTypeService.createCategoryType(req);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("[ensureCategoryType] 自动创建分类类型失败, entityTypeCode={}", code, e);
            throw new ServiceException(500, "自动创建默认分类失败：" + e.getMessage());
        }
    }

    private void ensureDimensionRows(EntityTypeDO entityType) {
        String code = entityType.getCode();
        // 分类维由「选用分类」显式保存；不在 bootstrap 自动插入，避免与用户删除/保存打架。
        ensureDimensionRow(code, DmDimensionKindEnum.MODEL.getCode(), null, null, null);
        ensureDimensionRow(code, DmDimensionKindEnum.ENTITY.getCode(), null, null, null);
    }

    private void ensureDimensionRow(String entityTypeCode, String kind, String perspectiveId,
                                    Map<String, Object> categoryMeta, Map<String, Object> modelAdminMeta) {
        if (DmDimensionKindEnum.CATEGORY.getCode().equals(kind)) {
            return;
        }
        if (dmEntityDimensionMapper.existsByKind(entityTypeCode, kind)) {
            return;
        }
        try {
            insertDimensionRow(entityTypeCode, kind, perspectiveId, categoryMeta, modelAdminMeta);
        } catch (DuplicateKeyException e) {
            log.debug("[ensureDimensionRow] 并发初始化已存在, entityTypeCode={}, kind={}, perspectiveId={}",
                    entityTypeCode, kind, perspectiveId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("[ensureDimensionRow] 自动创建浏览维度失败, entityTypeCode={}, kind={}", entityTypeCode, kind, e);
            throw new ServiceException(500, "自动创建数据管理浏览维度失败：" + e.getMessage());
        }
    }

    private void insertDimensionRow(String entityTypeCode, String kind, String perspectiveId,
                                    Map<String, Object> categoryMeta, Map<String, Object> modelAdminMeta) {
        DmEntityDimensionDO row = new DmEntityDimensionDO();
        row.setEntityTypeCode(entityTypeCode);
        row.setDimensionKind(kind);
        row.setPerspectiveId(perspectiveId);
        row.setEnabled(true);
        row.setCategoryDimensionMeta(categoryMeta);
        row.setModelAdminCategoryMeta(modelAdminMeta);
        dmEntityDimensionMapper.insert(row);
    }

    public static String defaultPerspectiveId(String entityTypeCode) {
        return entityTypeCode.trim() + "-default";
    }
}
