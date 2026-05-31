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
import java.util.List;
import java.util.Objects;
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
        fillLevelAndPath(category);
        checkLevelLimit(category.getLevel());
        validateUnique(category, true);
        getMapper().insert(category);
        evictCache(category.getCategoryTypeCode());
        return category.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Long id, DO updateObj) {
        DO db = findCategory(id, updateObj.getCategoryTypeCode());
        assertSameCategoryType(db.getCategoryTypeCode(), updateObj.getCategoryTypeCode());
        updateObj.setId(id);
        fillLevelAndPathForUpdate(db, updateObj);
        validateUnique(updateObj, false);
        getMapper().updateById(updateObj);
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

    @Transactional(rollbackFor = Exception.class)
    public void moveCategory(Long id, Long targetParentId) {
        DO category = findCategory(id, null);
        String categoryTypeCode = category.getCategoryTypeCode();
        Long parentId = targetParentId;
        if (Objects.equals(category.getParentId(), parentId)) {
            return;
        }
        List<DO> all = getMapper().selectByCategoryTypeCode(categoryTypeCode);
        if (parentId != null && isDescendant(category.getId(), parentId, all)) {
            throw new ServiceException(400, "无法将分类移动到自己的子节点下");
        }
        DO parent = parentId == null ? null : all.stream().filter(item -> Objects.equals(item.getId(), parentId)).findFirst().orElse(null);
        int newLevel = parent == null ? 1 : Objects.requireNonNullElse(parent.getLevel(), 0) + 1;
        checkLevelLimit(newLevel);
        String newPath = CategoryUtils.generateTreePath(parent == null ? null : parent.getTreePath(), category.getName());
        category.setParentId(parentId);
        category.setLevel(newLevel);
        category.setTreePath(newPath);
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

    private void fillLevelAndPath(DO category) {
        Long parentId = category.getParentId();
        if (parentId == null) {
            category.setLevel(1);
            category.setTreePath(category.getName());
            return;
        }
        DO parent = getMapper().selectById(parentId);
        if (parent == null) {
            throw new ServiceException(404, "父分类不存在");
        }
        assertSameCategoryType(parent.getCategoryTypeCode(), category.getCategoryTypeCode());
        category.setLevel(Objects.requireNonNullElse(parent.getLevel(), 0) + 1);
        category.setTreePath(CategoryUtils.generateTreePath(parent.getTreePath(), category.getName()));
    }

    private void fillLevelAndPathForUpdate(DO db, DO updateObj) {
        Long newParentId = updateObj.getParentId();
        if (Objects.equals(db.getParentId(), newParentId)) {
            if (!Objects.equals(db.getName(), updateObj.getName())) {
                updateObj.setTreePath(CategoryUtils.generateTreePath(extractParentPath(db.getTreePath()), updateObj.getName()));
            } else {
                updateObj.setTreePath(db.getTreePath());
            }
            updateObj.setLevel(db.getLevel());
            return;
        }
        DO parent = newParentId == null ? null : getMapper().selectById(newParentId);
        if (newParentId != null && parent == null) {
            throw new ServiceException(404, "父分类不存在");
        }
        if (parent != null) {
            assertSameCategoryType(parent.getCategoryTypeCode(), updateObj.getCategoryTypeCode());
        }
        int level = parent == null ? 1 : Objects.requireNonNullElse(parent.getLevel(), 0) + 1;
        checkLevelLimit(level);
        String newPath = CategoryUtils.generateTreePath(parent == null ? null : parent.getTreePath(), updateObj.getName());
        updateObj.setLevel(level);
        updateObj.setTreePath(newPath);
    }

    private String extractParentPath(String treePath) {
        if (treePath == null) {
            return null;
        }
        int idx = treePath.lastIndexOf('/');
        return idx < 0 ? null : treePath.substring(0, idx);
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
            throw new ServiceException(400, "分类层级超过上限 " + getMaxLevel());
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
        List<DO> children = getMapper().selectByParentIdAndCategoryTypeCode(parentId, categoryTypeCode);
        for (DO child : children) {
            int newLevel = parentLevel + 1;
            checkLevelLimit(newLevel);
            String newPath = CategoryUtils.generateTreePath(parentPath, child.getName());
            child.setLevel(newLevel);
            child.setTreePath(newPath);
            getMapper().updateById(child);
            updateChildrenPath(child.getId(), newPath, newLevel, categoryTypeCode);
        }
    }
}
