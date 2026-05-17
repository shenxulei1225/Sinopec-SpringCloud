package cn.iocoder.yudao.module.system.service.category;

import cn.iocoder.yudao.framework.category.service.AbstractCategoryService;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryBatchDeleteRespVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryCreateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryDeleteReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryDragReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryRespVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryTreeRespVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryUpdateReqVO;
import cn.iocoder.yudao.module.system.convert.category.CategoryConvert;
import cn.iocoder.yudao.module.system.dal.dataobject.category.CategoryDO;
import cn.iocoder.yudao.module.system.dal.dataobject.category.CategoryTypeDO;
import cn.iocoder.yudao.module.system.dal.mysql.category.CategoryMapper;
import cn.iocoder.yudao.module.system.dal.mysql.category.CategoryTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.CATEGORY_TYPE_NOT_EXISTS;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private static final int DEFAULT_MAX_LEVEL = 5;

    private final CategoryMapper categoryMapper;
    private final CategoryTypeMapper categoryTypeMapper;
    private final StringRedisTemplate stringRedisTemplate;

    private final CategoryCoreService core = new CategoryCoreService();

    @Override
    public boolean existsById(Long categoryId) {
        return categoryId != null && categoryMapper.selectById(categoryId) != null;
    }

    @Override
    public Set<Long> filterExistingCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new HashSet<>();
        }
        return categoryMapper.selectByIdsAndNotDeleted(categoryIds).stream().map(CategoryDO::getId).collect(Collectors.toSet());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(CategoryCreateReqVO reqVO) {
        CategoryTypeDO categoryType = requireCategoryType(reqVO.getCategoryTypeCode());
        CategoryDO category = CategoryConvert.INSTANCE.convert(reqVO);
        category.setCode(reqVO.getCode() == null || reqVO.getCode().isBlank() ? generateCode() : reqVO.getCode());
        if (category.getParentId() == null) {
            category.setParentId(categoryType.getTopLevelCategoryId());
        }
        validateParent(category.getParentId(), category.getCategoryTypeCode());
        if (category.getSort() == null) {
            category.setSort(resolveNextSort(category.getParentId(), category.getCategoryTypeCode()));
        }
        return core.createCategory(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(CategoryUpdateReqVO reqVO) {
        CategoryDO existing = requireCategory(reqVO.getId(), reqVO.getCategoryTypeCode());
        Long newParentId = reqVO.getParentId() != null ? reqVO.getParentId() : existing.getParentId();
        if (Objects.equals(newParentId, existing.getId())) {
            throw new ServiceException(400, "父分类不能是自己");
        }
        validateParent(newParentId, reqVO.getCategoryTypeCode());
        if (newParentId != null && isDescendantOf(existing.getId(), newParentId, categoryMapper.selectByCategoryTypeCode(reqVO.getCategoryTypeCode()))) {
            throw new ServiceException(400, "不能移动到自己的子节点下");
        }
        CategoryDO update = CategoryConvert.INSTANCE.convert(reqVO);
        update.setParentId(newParentId);
        if (!Objects.equals(existing.getParentId(), newParentId) && update.getSort() == null) {
            update.setSort(resolveNextSort(newParentId, reqVO.getCategoryTypeCode()));
        }
        core.updateCategory(reqVO.getId(), update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(CategoryDeleteReqVO reqVO) {
        core.deleteCategory(reqVO.getId(), Boolean.TRUE.equals(reqVO.getCascade()));
    }

    @Override
    public CategoryRespVO getCategoryVO(Long id) {
        CategoryDO category = categoryMapper.selectById(id);
        if (category == null || Boolean.TRUE.equals(category.getDeleted())) {
            throw new ServiceException(404, "分类不存在");
        }
        return CategoryConvert.INSTANCE.convert(category);
    }

    @Override
    public List<CategoryTreeRespVO> getCategoryTreeByType(String categoryTypeCode, Integer status) {
        String cacheKey = status == null ? "type:" + categoryTypeCode : "type:" + categoryTypeCode + ":status:" + status;
        List<CategoryTreeRespVO> cached = null;
        try {
            cached = CategoryCacheHelper.getCachedTree(stringRedisTemplate, cacheKey);
        } catch (Exception e) {
            log.warn("[getCategoryTreeByType] 读取分类树缓存失败，忽略缓存并回退到 DB 查询，cacheKey={}", cacheKey, e);
        }
        if (cached != null) {
            return cached;
        }
        requireCategoryType(categoryTypeCode);
        List<CategoryDO> list = categoryMapper.selectByCategoryTypeCode(categoryTypeCode);
        if (status != null) {
            list = list.stream().filter(item -> status.equals(item.getStatus())).toList();
        }
        List<CategoryTreeRespVO> tree = buildTree(list);
        CategoryCacheHelper.cacheTree(stringRedisTemplate, cacheKey, tree);
        return tree;
    }

    @Override
    public CategoryTreeRespVO getCategorySubtreeWithRoot(Long id, String categoryTypeCode, Integer status) {
        CategoryDO root = requireCategory(id, categoryTypeCode);
        List<CategoryDO> subtree = categoryMapper.selectSubtreeByPath(root.getTreePath(), categoryTypeCode, status);
        return buildTree(subtree).stream().filter(node -> Objects.equals(node.getId(), root.getId())).findFirst().orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveCategory(Long id, Long targetParentId, String categoryTypeCode) {
        if (targetParentId != null) {
            validateParent(targetParentId, categoryTypeCode);
        }
        core.moveCategory(id, targetParentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sortCategories(Long parentId, List<Long> orderedIds, String categoryTypeCode) {
        if (orderedIds == null || orderedIds.isEmpty()) {
            return;
        }
        for (Long id : orderedIds) {
            CategoryDO category = requireCategory(id, categoryTypeCode);
            if (!Objects.equals(category.getParentId(), parentId)) {
                throw new ServiceException(400, "分类父节点不匹配，无法排序");
            }
        }
        core.sortCategories(orderedIds, categoryTypeCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dragCategory(CategoryDragReqVO reqVO) {
        CategoryDragReqVO.Position position = reqVO.getPosition();
        if (position == null) {
            throw new ServiceException(400, "拖拽位置不能为空");
        }
        String categoryTypeCode = reqVO.getCategoryTypeCode();
        CategoryDO drag = requireCategory(reqVO.getId(), categoryTypeCode);
        CategoryDO target = reqVO.getTargetId() != null ? requireCategory(reqVO.getTargetId(), categoryTypeCode) : null;
        Long resolvedParentId;
        switch (position) {
            case BEFORE, AFTER -> {
                if (target == null) {
                    throw new ServiceException(400, "BEFORE/AFTER 需要提供 targetId");
                }
                resolvedParentId = target.getParentId();
            }
            case INNER -> resolvedParentId = reqVO.getTargetParentId() != null ? reqVO.getTargetParentId() : (target != null ? target.getId() : null);
            default -> throw new ServiceException(400, "不支持的拖拽位置");
        }
        List<CategoryDO> all = categoryMapper.selectByCategoryTypeCode(categoryTypeCode);
        if (resolvedParentId != null) {
            if (Objects.equals(resolvedParentId, drag.getId())) {
                throw new ServiceException(400, "不能拖拽到自身节点下");
            }
            if (isDescendantOf(drag.getId(), resolvedParentId, all)) {
                throw new ServiceException(400, "不能拖拽到自己的子节点下");
            }
        }
        if (!Objects.equals(drag.getParentId(), resolvedParentId)) {
            moveCategory(drag.getId(), resolvedParentId, categoryTypeCode);
        }
        reorderSiblings(resolvedParentId, categoryTypeCode, drag.getId(), target == null ? null : target.getId(), position);
        refreshSubtree(drag.getId(), categoryTypeCode);
    }

    @Override
    public List<CategoryRespVO> searchCategoryList(String keyword, String categoryTypeCode) {
        return CategoryConvert.INSTANCE.convertList(categoryMapper.searchLike(keyword, categoryTypeCode));
    }

    @Override
    public List<CategoryTreeRespVO> searchCategoryTree(String keyword, String categoryTypeCode) {
        List<CategoryDO> all = categoryMapper.selectByCategoryTypeCode(categoryTypeCode);
        if (all.isEmpty()) {
            return List.of();
        }
        String k = keyword == null ? null : keyword.trim();
        if (k == null || k.isEmpty()) {
            return buildTree(all);
        }
        List<CategoryDO> matched = all.stream()
                .filter(item -> item.getName() != null && item.getName().contains(k))
                .toList();
        if (matched.isEmpty()) {
            return List.of();
        }
        Set<Long> finalIds = new LinkedHashSet<>();
        for (CategoryDO match : matched) {
            if (match.getTreePath() == null || match.getTreePath().isEmpty()) {
                finalIds.add(match.getId());
                continue;
            }
            String[] pathParts = match.getTreePath().split("/");
            for (CategoryDO item : all) {
                if (item.getTreePath() == null) {
                    continue;
                }
                for (String part : pathParts) {
                    if (part.isEmpty()) {
                        continue;
                    }
                    if (item.getTreePath().endsWith(part) && match.getTreePath().startsWith(item.getTreePath())) {
                        finalIds.add(item.getId());
                        break;
                    }
                }
                if (item.getTreePath().startsWith(match.getTreePath())) {
                    finalIds.add(item.getId());
                }
            }
        }
        List<CategoryDO> result = all.stream().filter(item -> finalIds.contains(item.getId())).toList();
        return buildTree(result);
    }

    @Override
    public void updateStatus(Long id, Integer status, String categoryTypeCode) {
        requireCategory(id, categoryTypeCode);
        CategoryDO update = new CategoryDO();
        update.setId(id);
        update.setStatus(status);
        categoryMapper.updateById(update);
    }

    @Override
    public List<CategoryRespVO> listByParent(String categoryTypeCode, Long parentId, Integer status) {
        List<CategoryDO> list = categoryMapper.selectByParentIdAndCategoryTypeCode(parentId, categoryTypeCode);
        if (status != null) {
            list = list.stream().filter(item -> status.equals(item.getStatus())).toList();
        }
        return CategoryConvert.INSTANCE.convertList(list);
    }

    @Override
    public List<CategoryRespVO> listByParentRecursive(String categoryTypeCode, Long parentId, Integer status) {
        List<CategoryDO> all = categoryMapper.selectByCategoryTypeCode(categoryTypeCode);
        Map<Long, List<CategoryDO>> childrenMap = all.stream().collect(Collectors.groupingBy(item -> item.getParentId() == null ? 0L : item.getParentId()));
        List<CategoryDO> result = new ArrayList<>();
        ArrayDeque<Long> queue = new ArrayDeque<>();
        queue.add(parentId);
        while (!queue.isEmpty()) {
            Long current = queue.poll();
            for (CategoryDO child : childrenMap.getOrDefault(current == null ? 0L : current, Collections.emptyList())) {
                if (status == null || status.equals(child.getStatus())) {
                    result.add(child);
                }
                queue.add(child.getId());
            }
        }
        return CategoryConvert.INSTANCE.convertList(result);
    }

    @Override
    public List<CategoryRespVO> getPath(Long id, String categoryTypeCode) {
        CategoryDO target = requireCategory(id, categoryTypeCode);
        List<CategoryDO> list = categoryMapper.selectByCategoryTypeCode(categoryTypeCode != null ? categoryTypeCode : target.getCategoryTypeCode());
        String[] parts = target.getTreePath() == null ? new String[0] : target.getTreePath().split("/");
        return list.stream()
                .filter(item -> item.getTreePath() != null)
                .filter(item -> {
                    for (String part : parts) {
                        if (part.isEmpty()) {
                            continue;
                        }
                        if (item.getTreePath().endsWith(part)) {
                            return target.getTreePath().startsWith(item.getTreePath());
                        }
                    }
                    return false;
                })
                .sorted(Comparator.comparing(CategoryDO::getLevel, Comparator.nullsLast(Integer::compareTo)))
                .map(CategoryConvert.INSTANCE::convert)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CategoryRespVO> batchCreateCategory(List<CategoryCreateReqVO> categories) {
        List<CategoryRespVO> result = new ArrayList<>();
        for (CategoryCreateReqVO reqVO : categories) {
            result.add(getCategoryVO(createCategory(reqVO)));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CategoryRespVO> batchUpdateCategory(List<CategoryUpdateReqVO> categories) {
        List<CategoryRespVO> result = new ArrayList<>();
        for (CategoryUpdateReqVO reqVO : categories) {
            updateCategory(reqVO);
            result.add(getCategoryVO(reqVO.getId()));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryBatchDeleteRespVO batchDeleteCategory(List<Long> ids, boolean cascade, String categoryTypeCode) {
        CategoryBatchDeleteRespVO respVO = new CategoryBatchDeleteRespVO();
        List<CategoryBatchDeleteRespVO.CategoryBatchDeleteFailItem> failItems = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        for (Long id : ids) {
            try {
                CategoryDeleteReqVO reqVO = new CategoryDeleteReqVO();
                reqVO.setId(id);
                reqVO.setCascade(cascade);
                reqVO.setCategoryTypeCode(categoryTypeCode);
                deleteCategory(reqVO);
                successCount++;
            } catch (Exception ex) {
                failCount++;
                CategoryBatchDeleteRespVO.CategoryBatchDeleteFailItem item = new CategoryBatchDeleteRespVO.CategoryBatchDeleteFailItem();
                item.setId(id);
                item.setReason(ex.getMessage());
                failItems.add(item);
            }
        }
        respVO.setSuccessCount(successCount);
        respVO.setFailCount(failCount);
        respVO.setFailItems(failItems);
        return respVO;
    }

    @Override
    public List<Long> getAllCategoryIdsIncludingChildren(Long categoryId, String categoryTypeCode) {
        CategoryDO root = requireCategory(categoryId, categoryTypeCode);
        List<CategoryDO> list = categoryMapper.selectSubtreeByPath(root.getTreePath(), categoryTypeCode, null);
        if (list.isEmpty()) {
            return List.of(categoryId);
        }
        return list.stream().map(CategoryDO::getId).filter(Objects::nonNull).toList();
    }

    @Override
    public Map<Long, List<Long>> getAllCategoryIdsIncludingChildrenBatch(List<Long> categoryIds, String categoryTypeCode) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, List<Long>> result = new LinkedHashMap<>();
        for (Long categoryId : categoryIds) {
            if (categoryId != null) {
                result.put(categoryId, getAllCategoryIdsIncludingChildren(categoryId, categoryTypeCode));
            }
        }
        return result;
    }

    private String generateCode() {
        return "CAT-" + java.util.UUID.randomUUID().toString().replace("-", "");
    }

    private CategoryTypeDO requireCategoryType(String categoryTypeCode) {
        CategoryTypeDO categoryType = categoryTypeMapper.selectByCategoryTypeCode(categoryTypeCode);
        if (categoryType == null) {
            throw new ServiceException(404, CATEGORY_TYPE_NOT_EXISTS.getMsg());
        }
        return categoryType;
    }

    private CategoryDO requireCategory(Long id, String categoryTypeCode) {
        CategoryDO category = categoryMapper.selectByIdAndCategoryTypeCode(id, categoryTypeCode);
        if (category == null) {
            throw new ServiceException(404, "分类不存在");
        }
        return category;
    }

    private void validateParent(Long parentId, String categoryTypeCode) {
        if (parentId != null) {
            requireCategory(parentId, categoryTypeCode);
        }
    }

    private Integer resolveNextSort(Long parentId, String categoryTypeCode) {
        List<CategoryDO> siblings = categoryMapper.selectByParentIdAndCategoryTypeCode(parentId, categoryTypeCode);
        return siblings.stream().map(CategoryDO::getSort).filter(Objects::nonNull).max(Integer::compareTo).orElse(0) + 1;
    }

    private void updateNodePathAndLevel(Long categoryId, String categoryTypeCode) {
        CategoryDO current = requireCategory(categoryId, categoryTypeCode);
        int level = 1;
        String treePath = current.getName();
        if (current.getParentId() != null) {
            CategoryDO parent = requireCategory(current.getParentId(), categoryTypeCode);
            level = (parent.getLevel() == null ? 1 : parent.getLevel() + 1);
            treePath = (parent.getTreePath() == null || parent.getTreePath().isEmpty())
                    ? current.getName()
                    : parent.getTreePath() + "/" + current.getName();
        }
        CategoryDO update = new CategoryDO();
        update.setId(current.getId());
        update.setLevel(level);
        update.setTreePath(treePath);
        categoryMapper.updateById(update);
    }

    private void refreshSubtree(Long rootId, String categoryTypeCode) {
        List<CategoryDO> all = categoryMapper.selectByCategoryTypeCode(categoryTypeCode);
        Map<Long, List<CategoryDO>> childrenMap = all.stream().collect(Collectors.groupingBy(item -> item.getParentId() == null ? 0L : item.getParentId()));
        updateNodePathAndLevel(rootId, categoryTypeCode);
        ArrayDeque<Long> queue = new ArrayDeque<>();
        queue.add(rootId);
        while (!queue.isEmpty()) {
            Long current = queue.poll();
            for (CategoryDO child : childrenMap.getOrDefault(current, Collections.emptyList())) {
                updateNodePathAndLevel(child.getId(), categoryTypeCode);
                queue.add(child.getId());
            }
        }
    }

    private boolean isDescendantOf(Long rootId, Long targetId, List<CategoryDO> all) {
        return listDescendantIds(rootId, all).contains(targetId);
    }

    private List<Long> listDescendantIds(Long rootId, List<CategoryDO> all) {
        List<Long> ids = new ArrayList<>();
        for (CategoryDO item : all) {
            if (Objects.equals(rootId, item.getParentId())) {
                ids.add(item.getId());
                ids.addAll(listDescendantIds(item.getId(), all));
            }
        }
        return ids;
    }

    private void reorderSiblings(Long parentId, String categoryTypeCode, Long dragId, Long targetId, CategoryDragReqVO.Position position) {
        List<CategoryDO> siblings = categoryMapper.selectByParentIdAndCategoryTypeCode(parentId, categoryTypeCode);
        siblings.sort(Comparator.comparing(CategoryDO::getSort, Comparator.nullsLast(Integer::compareTo)).thenComparing(CategoryDO::getId));
        List<Long> ordered = new ArrayList<>();
        if (position == CategoryDragReqVO.Position.INNER) {
            for (CategoryDO item : siblings) {
                if (!Objects.equals(item.getId(), dragId)) {
                    ordered.add(item.getId());
                }
            }
            ordered.add(dragId);
        } else {
            for (CategoryDO item : siblings) {
                if (Objects.equals(item.getId(), dragId)) {
                    continue;
                }
                if (Objects.equals(item.getId(), targetId) && position == CategoryDragReqVO.Position.BEFORE) {
                    ordered.add(dragId);
                }
                ordered.add(item.getId());
                if (Objects.equals(item.getId(), targetId) && position == CategoryDragReqVO.Position.AFTER) {
                    ordered.add(dragId);
                }
            }
            if (!ordered.contains(dragId)) {
                ordered.add(dragId);
            }
        }
        int sort = 1;
        for (Long id : ordered) {
            CategoryDO update = new CategoryDO();
            update.setId(id);
            update.setSort(sort++);
            categoryMapper.updateById(update);
        }
    }

    private void collectDescendantIds(Long id, Map<Long, List<CategoryDO>> childrenMap, Set<Long> result) {
        List<CategoryDO> children = childrenMap.get(id == null ? 0L : id);
        if (children == null || children.isEmpty()) {
            return;
        }
        for (CategoryDO child : children) {
            if (child.getId() != null && result.add(child.getId())) {
                collectDescendantIds(child.getId(), childrenMap, result);
            }
        }
    }

    private List<CategoryTreeRespVO> buildTree(List<CategoryDO> categories) {
        List<CategoryDO> sorted = categories.stream()
                .sorted(Comparator.comparing(CategoryDO::getSort, Comparator.nullsLast(Integer::compareTo)).thenComparing(CategoryDO::getId))
                .toList();
        Map<Long, CategoryTreeRespVO> map = new LinkedHashMap<>();
        for (CategoryDO item : sorted) {
            CategoryTreeRespVO tree = CategoryConvert.INSTANCE.convertTree(item);
            tree.setChildren(new ArrayList<>());
            map.put(item.getId(), tree);
        }
        List<CategoryTreeRespVO> roots = new ArrayList<>();
        for (CategoryDO item : sorted) {
            CategoryTreeRespVO current = map.get(item.getId());
            if (item.getParentId() == null || !map.containsKey(item.getParentId())) {
                roots.add(current);
            } else {
                map.get(item.getParentId()).getChildren().add(current);
            }
        }
        return roots;
    }

    private void evictCache(String categoryTypeCode) {
        if (categoryTypeCode == null || categoryTypeCode.isEmpty()) {
            return;
        }
        CategoryCacheHelper.evict(stringRedisTemplate, "type:" + categoryTypeCode);
        CategoryCacheHelper.evict(stringRedisTemplate, "type:" + categoryTypeCode + ":status:0");
        CategoryCacheHelper.evict(stringRedisTemplate, "type:" + categoryTypeCode + ":status:1");
    }

    private class CategoryCoreService extends AbstractCategoryService<CategoryMapper, CategoryDO> {

        @Override
        protected CategoryMapper getMapper() {
            return categoryMapper;
        }

        @Override
        protected int getMaxLevel() {
            return DEFAULT_MAX_LEVEL;
        }

        @Override
        protected void evictCache(String categoryTypeCode) {
            CategoryServiceImpl.this.evictCache(categoryTypeCode);
        }
    }
}
