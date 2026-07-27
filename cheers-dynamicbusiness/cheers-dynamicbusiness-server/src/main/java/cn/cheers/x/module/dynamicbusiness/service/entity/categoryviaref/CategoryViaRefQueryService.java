package cn.cheers.x.module.dynamicbusiness.service.entity.categoryviaref;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryEntityLinkDO;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryEntityLinkService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityRelationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 经 REF 的分类反查编排：分类树 → 目标实体 → 主体实体 id。
 * <p>
 * 只读查询，不写库；路径须在 {@link CategoryViaRefQueryPathRegistry} 白名单内。
 */
@Service
public class CategoryViaRefQueryService {

    @Resource
    private CategoryViaRefQueryPathRegistry pathRegistry;
    @Resource
    private CategoryService categoryService;
    @Resource
    private EntityCategoryRelationService entityCategoryRelationService;
    @Resource
    private CategoryEntityLinkService categoryEntityLinkService;
    @Resource
    private EntityRelationService entityRelationService;

    /**
     * 按白名单反查路径，由用户点选的分类 id 列出主体实体 id。
     *
     * <p><strong>入参</strong>：{@code categoryViaRefPathCode} 为登记的路径编码；
     * {@code categoryIds} 为界面点选的分类节点 id（可多个；空或 null 时直接返回空列表）。</p>
     *
     * <p><strong>出参</strong>：满足路径定义的主体实体 id，去重且保留首次出现顺序。</p>
     *
     * <p><strong>读路径</strong>（不写）：</p>
     * <ul>
     *   <li>分类子树 — {@link CategoryService#getAllCategoryIdsIncludingChildrenBatch}，按路径的维度分类种类展开</li>
     *   <li>目标实体 id — {@link TargetEntityResolveMode#CATEGORY_ENTITY_RELATION} 时读
     *       {@code dynamic_entity_category_relation}（{@link EntityCategoryRelationService#listEntityIdsByCategoryIdsOnly}）；
     *       {@link TargetEntityResolveMode#CATEGORY_ENTITY_LINK} 时读
     *       {@code dynamic_category_entity_link}（{@link CategoryEntityLinkService#getLinksByCategoryIds}）</li>
     *   <li>主体实体 id — {@code dynamic_entity_relation}（或等价 REF 存储），
     *       {@link EntityRelationService#listEntityIdsByRelationFieldAndRelatedIds}</li>
     * </ul>
     *
     * @param categoryViaRefPathCode 白名单反查路径编码
     * @param categoryIds            用户点选的分类 id 列表
     * @return 主体实体 id 列表；无命中时为空列表
     */
    public List<Long> listSubjectEntityIds(String categoryViaRefPathCode, List<Long> categoryIds) {
        CategoryViaRefQueryPath path = requirePath(categoryViaRefPathCode);
        List<Long> expanded = expandCategoryIds(categoryIds, path.dimensionCategoryTypeCode());
        if (expanded.isEmpty()) {
            return List.of();
        }
        List<Long> targetIds = resolveTargetEntityIds(path, expanded);
        if (targetIds.isEmpty()) {
            return List.of();
        }
        List<Long> subjectIds = entityRelationService
                .listEntityIdsByRelationFieldAndRelatedIds(path.refFieldCode(), targetIds);
        if (subjectIds == null || subjectIds.isEmpty()) {
            return List.of();
        }
        return distinctPreserveOrder(subjectIds);
    }

    /**
     * 校验请求侧 {@code entityTypeCode} 与路径登记的主体实体类型一致；不一致时抛出 400。
     *
     * @param categoryViaRefPathCode 白名单反查路径编码
     * @param entityTypeCode         入口传入的实体类型编码（须与路径 {@code subjectEntityTypeCode} 一致）
     */
    public void assertSubjectTypeMatches(String categoryViaRefPathCode, String entityTypeCode) {
        CategoryViaRefQueryPath path = requirePath(categoryViaRefPathCode);
        if (!Objects.equals(path.subjectEntityTypeCode(), entityTypeCode)) {
            throw new ServiceException(400,
                    "反查路径 " + path.pathCode() + " 的主体类型为 "
                            + path.subjectEntityTypeCode() + "，与请求 entityTypeCode 不符");
        }
    }

    private CategoryViaRefQueryPath requirePath(String categoryViaRefPathCode) {
        if (categoryViaRefPathCode == null || categoryViaRefPathCode.isBlank()) {
            throw new ServiceException(400, "反查路径编码不能为空");
        }
        return pathRegistry.require(categoryViaRefPathCode.trim());
    }

    private List<Long> expandCategoryIds(List<Long> categoryIds, String categoryTypeCode) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }
        Map<Long, List<Long>> batch =
                categoryService.getAllCategoryIdsIncludingChildrenBatch(categoryIds, categoryTypeCode);
        Set<Long> out = new LinkedHashSet<>();
        for (Long id : categoryIds) {
            List<Long> desc = batch.get(id);
            if (desc != null) {
                out.addAll(desc);
            } else {
                out.add(id);
            }
        }
        return new ArrayList<>(out);
    }

    private List<Long> resolveTargetEntityIds(CategoryViaRefQueryPath path, List<Long> expandedCategoryIds) {
        if (path.targetResolveMode() == TargetEntityResolveMode.CATEGORY_ENTITY_RELATION) {
            List<Long> ids = entityCategoryRelationService.listEntityIdsByCategoryIdsOnly(
                    expandedCategoryIds, path.targetEntityTypeCode());
            return ids == null || ids.isEmpty() ? List.of() : distinctPreserveOrder(ids);
        }
        List<CategoryEntityLinkDO> links = categoryEntityLinkService.getLinksByCategoryIds(expandedCategoryIds);
        if (links == null || links.isEmpty()) {
            return List.of();
        }
        Set<Long> targetIds = new LinkedHashSet<>();
        for (CategoryEntityLinkDO link : links) {
            if (link.getEntityId() != null
                    && path.targetEntityTypeCode().equals(link.getEntityTypeCode())) {
                targetIds.add(link.getEntityId());
            }
        }
        return new ArrayList<>(targetIds);
    }

    private static List<Long> distinctPreserveOrder(List<Long> ids) {
        return new ArrayList<>(new LinkedHashSet<>(ids));
    }
}
