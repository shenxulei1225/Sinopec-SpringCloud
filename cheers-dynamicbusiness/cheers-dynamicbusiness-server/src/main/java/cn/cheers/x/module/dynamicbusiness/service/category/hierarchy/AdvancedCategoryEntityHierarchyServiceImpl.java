package cn.cheers.x.module.dynamicbusiness.service.category.hierarchy;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryEntityLinkDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.framework.hierarchy.IdTreeHierarchy;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryEntityLinkService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryModeSupport;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 高级分类组织层级同步：实体 parentId/treePath 权威，分类树投影对齐。
 */
@Service
@Slf4j
public class AdvancedCategoryEntityHierarchyServiceImpl implements AdvancedCategoryEntityHierarchyService {

    @Resource
    private EntityCoreService entityCoreService;
    @Resource
    private CategoryEntityLinkService categoryEntityLinkService;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private CategoryTypeMapper categoryTypeMapper;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private EntityRepository entityRepository;
    @Lazy
    @Resource
    private CategoryService categoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveCategoryKeepingEntityAuthority(Long categoryId, Long newParentCategoryId, String categoryTypeCode) {
        if (categoryId == null) {
            throw new ServiceException(400, "分类 id 不能为空");
        }
        CategoryEntityLinkDO link = categoryEntityLinkService.getLinkByCategoryId(categoryId);
        if (link == null || link.getEntityId() == null) {
            // 无绑定：仅分类结构（结构根或脏数据由调用方决定）；本方法不静默造实体
            categoryService.moveCategoryStructureOnly(categoryId, newParentCategoryId);
            return;
        }
        String entityTypeCode = resolveEntityTypeCode(link);
        Long parentEntityId = resolveParentEntityIdFromCategory(newParentCategoryId, categoryTypeCode);
        entityCoreService.moveEntity(link.getEntityId(), entityTypeCode, parentEntityId);
        categoryService.moveCategoryStructureOnly(categoryId, newParentCategoryId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncCategoryAfterEntityMove(Long entityId, String entityTypeCode, Long newParentEntityId) {
        if (entityId == null || !StringUtils.hasText(entityTypeCode)) {
            return;
        }
        CategoryEntityLinkDO link = categoryEntityLinkService.getLinkByEntityIdAndEntityTypeCode(entityId, entityTypeCode);
        if (link == null || link.getCategoryId() == null) {
            return;
        }
        CategoryDO category = categoryMapper.selectById(link.getCategoryId());
        if (category == null) {
            throw new ServiceException(404, "实体已绑定的分类节点不存在，无法对齐分类树");
        }
        String categoryTypeCode = category.getCategoryTypeCode();
        Long newParentCategoryId = resolveParentCategoryIdForEntityParent(
                IdTreeHierarchy.normalizeParentId(newParentEntityId), entityTypeCode, categoryTypeCode);
        if (Objects.equals(
                IdTreeHierarchy.normalizeParentId(category.getParentId()),
                IdTreeHierarchy.normalizeParentId(newParentCategoryId))) {
            return;
        }
        categoryService.moveCategoryStructureOnly(link.getCategoryId(), newParentCategoryId);
    }

    @Override
    public Long resolveParentEntityIdFromCategory(Long parentCategoryId, String categoryTypeCode) {
        Long parentId = IdTreeHierarchy.normalizeParentId(parentCategoryId);
        if (parentId == null) {
            return null;
        }
        CategoryTypeDO type = StringUtils.hasText(categoryTypeCode)
                ? categoryTypeMapper.selectByCategoryTypeCode(categoryTypeCode)
                : null;
        CategoryDO parent = categoryMapper.selectById(parentId);
        if (parent == null) {
            throw new ServiceException(404, "父分类不存在：" + parentId);
        }
        if (isStructuralRoot(parent, type)) {
            return null;
        }
        CategoryEntityLinkDO parentLink = categoryEntityLinkService.getLinkByCategoryId(parentId);
        if (parentLink == null || parentLink.getEntityId() == null) {
            throw new ServiceException(400,
                    "父分类「" + parent.getName() + "」未绑定实体，无法作为组织上级写入实体层级");
        }
        return parentLink.getEntityId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int backfillEntityHierarchyFromCategoryTree(String categoryTypeCode) {
        if (!StringUtils.hasText(categoryTypeCode)) {
            throw new ServiceException(400, "categoryTypeCode 不能为空");
        }
        CategoryTypeDO type = categoryTypeMapper.selectByCategoryTypeCode(categoryTypeCode);
        if (type == null) {
            throw new ServiceException(404, "分类种类不存在：" + categoryTypeCode);
        }
        if (!CategoryModeSupport.isAdvanced(CategoryModeSupport.resolveFromType(type))) {
            throw new ServiceException(400, "仅高级分类支持按分类树回填实体层级");
        }

        List<CategoryDO> all = categoryMapper.selectByCategoryTypeCode(categoryTypeCode);
        Map<Long, CategoryDO> byId = new HashMap<>();
        for (CategoryDO c : all) {
            if (c.getId() != null) {
                byId.put(c.getId(), c);
            }
        }

        // 只处理已绑定实体的节点；按分类 tree_path 深度升序，保证先写父再写子
        List<CategoryDO> linkedNodes = new ArrayList<>();
        Map<Long, CategoryEntityLinkDO> linkByCategoryId = new HashMap<>();
        for (CategoryDO c : all) {
            if (isStructuralRoot(c, type)) {
                continue;
            }
            CategoryEntityLinkDO link = categoryEntityLinkService.getLinkByCategoryId(c.getId());
            if (link == null || link.getEntityId() == null) {
                continue;
            }
            linkedNodes.add(c);
            linkByCategoryId.put(c.getId(), link);
        }
        linkedNodes.sort(Comparator
                .comparingInt((CategoryDO c) -> pathDepth(c.getTreePath()))
                .thenComparing(CategoryDO::getId, Comparator.nullsLast(Long::compareTo)));

        int updated = 0;
        Map<Long, String> entityPathByEntityId = new HashMap<>();

        for (CategoryDO node : linkedNodes) {
            CategoryEntityLinkDO link = linkByCategoryId.get(node.getId());
            String entityTypeCode = resolveEntityTypeCode(link);
            Long parentEntityId = resolveParentEntityIdFromCategory(node.getParentId(), categoryTypeCode);

            EntityDO entity = entityRepository.findById(link.getEntityId(), entityTypeCode);
            if (entity == null) {
                throw new ServiceException(404,
                        "分类「" + node.getName() + "」绑定的实体不存在：" + link.getEntityId());
            }

            String parentPath = null;
            if (parentEntityId != null) {
                parentPath = entityPathByEntityId.get(parentEntityId);
                if (parentPath == null) {
                    EntityDO parentEntity = entityRepository.findById(parentEntityId, entityTypeCode);
                    if (parentEntity == null) {
                        throw new ServiceException(404, "父实体不存在：" + parentEntityId);
                    }
                    parentPath = parentEntity.getTreePath();
                    if (parentPath == null || parentPath.isBlank()) {
                        parentPath = IdTreeHierarchy.buildPath(null, parentEntityId);
                    }
                    entityPathByEntityId.put(parentEntityId, parentPath);
                }
                if (IdTreeHierarchy.wouldCreateCycle(entity.getId(), parentPath)) {
                    throw new ServiceException(400,
                            "回填成环：实体 " + entity.getId() + " 不能挂到路径 " + parentPath);
                }
            }

            IdTreeHierarchy.MovePlan plan = IdTreeHierarchy.planMove(entity.getId(), parentEntityId, parentPath);
            boolean changed = !Objects.equals(
                    IdTreeHierarchy.normalizeParentId(entity.getParentId()), plan.newParentId())
                    || !Objects.equals(entity.getTreePath(), plan.newTreePath());
            if (changed) {
                entity.setParentId(plan.newParentId());
                entity.setTreePath(plan.newTreePath());
                entityRepository.update(entity);
                updated++;
            }
            entityPathByEntityId.put(entity.getId(), plan.newTreePath());
        }

        log.info("[backfillEntityHierarchyFromCategoryTree][type={}][linked={}][updated={}]",
                categoryTypeCode, linkedNodes.size(), updated);
        return updated;
    }

    private Long resolveParentCategoryIdForEntityParent(
            Long newParentEntityId, String entityTypeCode, String categoryTypeCode) {
        if (newParentEntityId == null) {
            CategoryTypeDO type = categoryTypeMapper.selectByCategoryTypeCode(categoryTypeCode);
            if (type == null || type.getTopLevelCategoryId() == null) {
                throw new ServiceException(400, "分类种类未配置顶层节点，无法将实体升为根后对齐分类");
            }
            return type.getTopLevelCategoryId();
        }
        CategoryEntityLinkDO parentLink =
                categoryEntityLinkService.getLinkByEntityIdAndEntityTypeCode(newParentEntityId, entityTypeCode);
        if (parentLink == null || parentLink.getCategoryId() == null) {
            throw new ServiceException(400,
                    "父实体未绑定分类节点，无法对齐分类树（entityId=" + newParentEntityId + "）");
        }
        return parentLink.getCategoryId();
    }

    private String resolveEntityTypeCode(CategoryEntityLinkDO link) {
        if (link != null && StringUtils.hasText(link.getEntityTypeCode())) {
            return link.getEntityTypeCode().trim();
        }
        if (link != null && link.getEntityModelId() != null) {
            ModelDO model = modelMapper.selectById(link.getEntityModelId());
            if (model != null && StringUtils.hasText(model.getEntityTypeCode())) {
                return model.getEntityTypeCode().trim();
            }
        }
        throw new ServiceException(400, "分类实体绑定缺少 entityTypeCode，无法调整层级");
    }

    private static boolean isStructuralRoot(CategoryDO category, CategoryTypeDO type) {
        if (category == null) {
            return false;
        }
        if (category.getParentId() == null) {
            return true;
        }
        return type != null
                && type.getTopLevelCategoryId() != null
                && Objects.equals(type.getTopLevelCategoryId(), category.getId());
    }

    private static int pathDepth(String treePath) {
        if (treePath == null || treePath.isBlank()) {
            return 0;
        }
        int depth = 0;
        for (int i = 0; i < treePath.length(); i++) {
            if (treePath.charAt(i) == '/') {
                depth++;
            }
        }
        // "/1/2/" → 段数约 depth-1
        return Math.max(0, depth - 1);
    }
}
