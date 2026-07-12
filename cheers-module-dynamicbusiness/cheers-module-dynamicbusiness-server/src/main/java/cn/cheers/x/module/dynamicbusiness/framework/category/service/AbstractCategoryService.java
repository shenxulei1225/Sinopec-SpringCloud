package cn.cheers.x.module.dynamicbusiness.framework.category.service;

import cn.cheers.x.module.dynamicbusiness.framework.category.core.CategoryContract;
import cn.cheers.x.module.dynamicbusiness.framework.category.mapper.FrameworkCategoryMapper;
import cn.cheers.x.module.dynamicbusiness.framework.category.utils.CategoryUtils;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractCategoryService<
        M extends FrameworkCategoryMapper<DO>,
        DO extends CategoryContract<Long>> {

    protected abstract M getMapper();

    protected int getMaxLevel() {
        return 5;
    }

    protected void evictCache(String categoryTypeCode) {
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(DO category) {
        assertCategoryType(category);
        fillLevelFromParent(category);
        checkLevelLimit(category.getLevel());
        validateUnique(category, true);
        getMapper().insert(category);
        applyTreePath(category);
        getMapper().updateById(category);
        evictCache(category.getCategoryTypeCode());
        return category.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Long id, DO updateObj) {
        DO db = findCategory(id, updateObj.getCategoryTypeCode());
        assertSameCategoryType(db.getCategoryTypeCode(), updateObj.getCategoryTypeCode());
        updateObj.setId(id);
        boolean parentChanged = !Objects.equals(db.getParentId(), updateObj.getParentId());
        fillLevelAndPathForUpdate(db, updateObj);
        validateUnique(updateObj, false);
        getMapper().updateById(updateObj);
        if (parentChanged) {
            updateChildrenPath(updateObj.getId(), updateObj.getTreePath(), updateObj.getLevel(), updateObj.getCategoryTypeCode());
        }
        evictCache(updateObj.getCategoryTypeCode());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id, boolean cascade) {
        DO db = findCategory(id, null);
        if (!cascade) {
            List<DO> children = getMapper().selectByParentIdAndCategoryTypeCode(db.getId(), db.getCategoryTypeCode());
            if (!children.isEmpty()) {
                throw new ServiceException(400, "存在子分类，禁止删除（开启级联可强制删除）");
            }
        }
        List<Long> toDelete = new ArrayList<>();
        if (cascade) {
            List<DO> all = getMapper().selectByCategoryTypeCode(db.getCategoryTypeCode());
            toDelete.addAll(listDescendantIds(db.getId(), all));
        }
        toDelete.add(db.getId());
        getMapper().delete(new QueryWrapper<DO>().in("id", toDelete));
        evictCache(db.getCategoryTypeCode());
    }

    public DO getCategory(Long id, String categoryTypeCode) {
        return findCategory(id, categoryTypeCode);
    }

    public List<DO> getCategoryTree(String categoryTypeCode) {
        List<DO> list = getMapper().selectByCategoryTypeCode(categoryTypeCode);
        return CategoryUtils.buildTree(list);
    }

    /**
     * 按 id 路径格式（{@code /1/677/}）补全或修正节点的 tree_path、level；必要时级联修正子孙节点。
     * 仅当父子关系非法、存在环、父节点缺失或层级超限等无法恢复的情况才抛异常。
     */
    public void ensureTreeMetadata(Long id, String categoryTypeCode) {
        if (id == null) {
            return;
        }
        ensureTreeMetadataInternal(id, categoryTypeCode, new HashSet<>());
    }

    @Transactional(rollbackFor = Exception.class)
    public void moveCategory(Long id, Long targetParentId) {
        DO category = findCategory(id, null);
        String categoryTypeCode = category.getCategoryTypeCode();
        Long parentId = normalizeParentId(targetParentId);
        if (Objects.equals(normalizeParentId(category.getParentId()), parentId)) {
            ensureTreeMetadata(id, categoryTypeCode);
            return;
        }
        List<DO> all = getMapper().selectByCategoryTypeCode(categoryTypeCode);
        if (parentId != null && isDescendant(category.getId(), parentId, all)) {
            throw new ServiceException(400, "无法将分类移动到自己的子节点下");
        }
        if (parentId != null) {
            ensureTreeMetadata(parentId, categoryTypeCode);
        }
        DO parent = parentId == null ? null : getMapper().selectById(parentId);
        if (parentId != null && parent == null) {
            throw new ServiceException(404, "目标父分类(id=" + parentId + ")不存在，无法移动分类");
        }
        int newLevel = parent == null ? 1 : Objects.requireNonNullElse(parent.getLevel(), 0) + 1;
        checkLevelLimit(newLevel);
        String newPath = CategoryUtils.buildIdTreePath(parent == null ? null : parent.getTreePath(), category.getId());
        category.setParentId(parentId);
        category.setLevel(newLevel);
        category.setTreePath(newPath);
        category.setParentCode(parent == null ? null : parent.getCode());
        getMapper().updateById(category);
        updateChildrenPath(category.getId(), newPath, newLevel, categoryTypeCode);
        evictCache(categoryTypeCode);
    }

    @Transactional(rollbackFor = Exception.class)
    public void sortCategories(List<Long> orderedIds, String categoryTypeCode) {
        if (orderedIds == null || orderedIds.isEmpty()) {
            return;
        }
        int sort = 1;
        for (Long id : orderedIds) {
            DO db = findCategory(id, categoryTypeCode);
            db.setSort(sort++);
            getMapper().updateById(db);
        }
        evictCache(categoryTypeCode);
    }

    public List<DO> searchCategories(String keyword, String categoryTypeCode) {
        LambdaQueryWrapper<DO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DO::getCategoryTypeCode, categoryTypeCode);
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(q -> q.like(DO::getName, keyword).or().like(DO::getTreePath, keyword));
        }
        wrapper.orderByAsc(DO::getSort, DO::getId);
        return getMapper().selectList(wrapper);
    }

    private DO findCategory(Long id, String categoryTypeCode) {
        DO db = categoryTypeCode == null ? getMapper().selectById(id) : getMapper().selectByIdAndCategoryTypeCode(id, categoryTypeCode);
        if (db == null) {
            throw ServiceExceptionUtil.exception0(404, "分类不存在");
        }
        return db;
    }

    private void assertCategoryType(DO category) {
        if (StringUtils.isBlank(category.getCategoryTypeCode())) {
            throw new ServiceException(400, "分类类型编码不能为空");
        }
    }

    private void assertSameCategoryType(String expected, String actual) {
        if (!Objects.equals(expected, actual)) {
            throw new ServiceException(400, "分类类型不匹配");
        }
    }

    private void fillLevelFromParent(DO category) {
        Long parentId = normalizeParentId(category.getParentId());
        category.setParentId(parentId);
        if (parentId == null) {
            category.setLevel(1);
            category.setParentCode(null);
            return;
        }
        DO parent = getMapper().selectById(parentId);
        if (parent == null) {
            throw new ServiceException(404, "父分类(id=" + parentId + ")不存在，无法创建子分类");
        }
        assertSameCategoryType(parent.getCategoryTypeCode(), category.getCategoryTypeCode());
        ensureTreeMetadata(parentId, category.getCategoryTypeCode());
        parent = getMapper().selectById(parentId);
        category.setLevel(Objects.requireNonNullElse(parent.getLevel(), 0) + 1);
        category.setParentCode(parent.getCode());
    }

    private void applyTreePath(DO category) {
        if (category.getId() == null) {
            return;
        }
        Long parentId = normalizeParentId(category.getParentId());
        if (parentId == null) {
            category.setTreePath(CategoryUtils.buildIdTreePath(null, category.getId()));
            return;
        }
        DO parent = getMapper().selectById(parentId);
        if (parent == null) {
            throw new ServiceException(404, "父分类(id=" + parentId + ")不存在，无法生成 tree_path");
        }
        ensureTreeMetadata(parentId, category.getCategoryTypeCode());
        parent = getMapper().selectById(parentId);
        category.setTreePath(CategoryUtils.buildIdTreePath(parent.getTreePath(), category.getId()));
    }

    private void fillLevelAndPathForUpdate(DO db, DO updateObj) {
        Long newParentId = normalizeParentId(updateObj.getParentId());
        updateObj.setParentId(newParentId);
        if (Objects.equals(normalizeParentId(db.getParentId()), newParentId)) {
            ensureTreeMetadata(db.getId(), updateObj.getCategoryTypeCode());
            DO repaired = findCategory(db.getId(), updateObj.getCategoryTypeCode());
            updateObj.setTreePath(repaired.getTreePath());
            updateObj.setLevel(repaired.getLevel());
            updateObj.setParentCode(repaired.getParentCode());
            return;
        }
        DO parent = newParentId == null ? null : getMapper().selectById(newParentId);
        if (newParentId != null && parent == null) {
            throw new ServiceException(404, "父分类(id=" + newParentId + ")不存在，无法更新分类");
        }
        if (parent != null) {
            assertSameCategoryType(parent.getCategoryTypeCode(), updateObj.getCategoryTypeCode());
            ensureTreeMetadata(newParentId, updateObj.getCategoryTypeCode());
            parent = getMapper().selectById(newParentId);
        }
        int level = parent == null ? 1 : Objects.requireNonNullElse(parent.getLevel(), 0) + 1;
        checkLevelLimit(level);
        String newPath = CategoryUtils.buildIdTreePath(parent == null ? null : parent.getTreePath(), updateObj.getId());
        updateObj.setLevel(level);
        updateObj.setTreePath(newPath);
        updateObj.setParentCode(parent == null ? null : parent.getCode());
    }

    private void validateUnique(DO category, boolean isCreate) {
        Long parentId = category.getParentId();
        String categoryTypeCode = category.getCategoryTypeCode();
        DO nameDup = getMapper().selectByNameAndParent(category.getName(), parentId, categoryTypeCode);
        if (nameDup != null && (isCreate || !Objects.equals(nameDup.getId(), category.getId()))) {
            throw new ServiceException(400, "同一父分类下名称已存在");
        }
        if (category.getSort() != null) {
            DO sortDup = getMapper().selectBySortAndParent(category.getSort(), parentId, categoryTypeCode);
            if (sortDup != null && (isCreate || !Objects.equals(sortDup.getId(), category.getId()))) {
                throw new ServiceException(400, "同一父分类下排序已存在");
            }
        }
    }

    private void checkLevelLimit(int level) {
        if (level > getMaxLevel()) {
            throw new ServiceException(400,
                    "分类层级为 " + level + "，超过上限 " + getMaxLevel() + "，无法补全或更新 tree_path 与 level");
        }
    }

    private boolean isDescendant(Long rootId, Long targetId, List<DO> all) {
        return listDescendantIds(rootId, all).contains(targetId);
    }

    private List<Long> listDescendantIds(Long rootId, List<DO> all) {
        if (all == null || all.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> results = all.stream()
                .filter(item -> Objects.equals(rootId, item.getParentId()))
                .map(CategoryContract::getId)
                .collect(Collectors.toList());
        List<Long> children = new ArrayList<>();
        for (Long id : results) {
            children.addAll(listDescendantIds(id, all));
        }
        results.addAll(children);
        return results;
    }

    private void updateChildrenPath(Long parentId, String parentPath, int parentLevel, String categoryTypeCode) {
        DO parent = getMapper().selectById(parentId);
        String parentCode = parent != null ? parent.getCode() : null;
        List<DO> children = getMapper().selectByParentIdAndCategoryTypeCode(parentId, categoryTypeCode);
        for (DO child : children) {
            int newLevel = parentLevel + 1;
            checkLevelLimit(newLevel);
            String newPath = CategoryUtils.buildIdTreePath(parentPath, child.getId());
            child.setLevel(newLevel);
            child.setTreePath(newPath);
            child.setParentCode(parentCode);
            getMapper().updateById(child);
            updateChildrenPath(child.getId(), newPath, newLevel, categoryTypeCode);
        }
    }

    private DO ensureTreeMetadataInternal(Long id, String categoryTypeCode, Set<Long> visiting) {
        if (!visiting.add(id)) {
            throw new ServiceException(400,
                    "分类父子关系存在环（分类 id=" + id + "），无法补全 tree_path 与 level");
        }
        DO node = findCategory(id, categoryTypeCode);
        String effectiveTypeCode = node.getCategoryTypeCode();
        Long parentId = normalizeParentId(node.getParentId());
        DO parent = null;
        if (parentId != null) {
            parent = getMapper().selectById(parentId);
            if (parent == null) {
                throw new ServiceException(400,
                        "分类「" + safeName(node) + "」(id=" + id + ") 的父分类 id=" + parentId
                                + " 不存在，无法补全 tree_path 与 level");
            }
            assertSameCategoryType(parent.getCategoryTypeCode(), effectiveTypeCode);
            parent = ensureTreeMetadataInternal(parentId, effectiveTypeCode, visiting);
        }

        int expectedLevel = parent == null ? 1 : Objects.requireNonNullElse(parent.getLevel(), 0) + 1;
        checkLevelLimit(expectedLevel);
        String expectedPath = CategoryUtils.buildIdTreePath(parent == null ? null : parent.getTreePath(), node.getId());

        if (needsTreeMetadataRepair(node, expectedPath, expectedLevel)) {
            node.setLevel(expectedLevel);
            node.setTreePath(expectedPath);
            node.setParentCode(parent == null ? null : parent.getCode());
            getMapper().updateById(node);
            updateChildrenPath(node.getId(), expectedPath, expectedLevel, effectiveTypeCode);
        }
        visiting.remove(id);
        return findCategory(id, effectiveTypeCode);
    }

    private boolean needsTreeMetadataRepair(DO node, String expectedPath, int expectedLevel) {
        if (!Objects.equals(node.getLevel(), expectedLevel)) {
            return true;
        }
        if (!CategoryUtils.treePathMatchesNode(node.getTreePath(), node.getId())) {
            return true;
        }
        return !Objects.equals(expectedPath, node.getTreePath());
    }

    private Long normalizeParentId(Long parentId) {
        if (parentId == null || parentId == 0L) {
            return null;
        }
        return parentId;
    }

    private String safeName(DO node) {
        return node.getName() != null ? node.getName() : "";
    }
}
