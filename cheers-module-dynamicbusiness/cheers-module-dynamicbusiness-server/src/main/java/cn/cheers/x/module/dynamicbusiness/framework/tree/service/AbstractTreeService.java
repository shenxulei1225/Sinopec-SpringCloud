package cn.cheers.x.module.dynamicbusiness.framework.tree.service;

import cn.cheers.x.module.dynamicbusiness.framework.tree.core.TreeContract;
import cn.cheers.x.module.dynamicbusiness.framework.tree.mapper.FrameworkTreeMapper;
import cn.cheers.x.module.dynamicbusiness.framework.tree.utils.TreeUtils;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractTreeService<
        M extends FrameworkTreeMapper<DO>,
        DO extends TreeContract<Long>> {

    protected abstract M getMapper();

    protected int getMaxLevel() {
        return 5;
    }

    protected void evictCache() {
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createNode(DO node) {
        fillLevelAndPath(node);
        checkLevelLimit(node.getLevel());
        validateUnique(node, true);
        getMapper().insert(node);
        evictCache();
        return node.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateNode(Long id, DO updateObj) {
        DO db = findNode(id);
        updateObj.setId(id);
        fillLevelAndPathForUpdate(db, updateObj);
        validateUnique(updateObj, false);
        getMapper().updateById(updateObj);
        evictCache();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteNode(Long id, boolean cascade) {
        DO db = findNode(id);
        if (!cascade) {
            List<DO> children = getMapper().selectByParentId(db.getId());
            if (!children.isEmpty()) {
                throw new ServiceException(400, "存在子节点，禁止删除（开启级联可强制删除）");
            }
        }
        List<Long> toDelete = new ArrayList<>();
        if (cascade) {
            List<DO> all = getMapper().selectList(new QueryWrapper<>());
            toDelete.addAll(listDescendantIds(db.getId(), all));
        }
        toDelete.add(db.getId());
        getMapper().delete(new QueryWrapper<DO>().in("id", toDelete));
        evictCache();
    }

    public DO getNode(Long id) {
        return findNode(id);
    }

    public List<DO> getTree() {
        return TreeUtils.buildTree(getMapper().selectList(new QueryWrapper<>()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void moveNode(Long id, Long targetParentId) {
        DO node = findNode(id);
        if (Objects.equals(node.getParentId(), targetParentId)) {
            return;
        }
        List<DO> all = getMapper().selectList(new QueryWrapper<>());
        if (targetParentId != null && isDescendant(node.getId(), targetParentId, all)) {
            throw new ServiceException(400, "无法将节点移动到自己的子节点下");
        }
        DO parent = targetParentId == null ? null : all.stream()
                .filter(item -> Objects.equals(item.getId(), targetParentId))
                .findFirst()
                .orElse(null);
        if (targetParentId != null && parent == null) {
            throw new ServiceException(404, "父节点不存在");
        }
        int newLevel = parent == null ? 1 : Objects.requireNonNullElse(parent.getLevel(), 0) + 1;
        checkLevelLimit(newLevel);
        String newPath = TreeUtils.generateTreePath(parent == null ? null : parent.getTreePath(), node.getName());
        node.setParentId(targetParentId);
        node.setLevel(newLevel);
        node.setTreePath(newPath);
        getMapper().updateById(node);
        updateChildrenPath(node.getId(), newPath, newLevel);
        evictCache();
    }

    @Transactional(rollbackFor = Exception.class)
    public void sortNodes(List<Long> orderedIds) {
        if (orderedIds == null || orderedIds.isEmpty()) {
            return;
        }
        int sort = 1;
        for (Long id : orderedIds) {
            DO db = findNode(id);
            db.setSort(sort++);
            getMapper().updateById(db);
        }
        evictCache();
    }

    private DO findNode(Long id) {
        DO db = getMapper().selectById(id);
        if (db == null) {
            throw ServiceExceptionUtil.exception0(404, "节点不存在");
        }
        return db;
    }

    private void fillLevelAndPath(DO node) {
        Long parentId = node.getParentId();
        if (parentId == null) {
            node.setLevel(1);
            node.setTreePath(node.getName());
            return;
        }
        DO parent = getMapper().selectById(parentId);
        if (parent == null) {
            throw new ServiceException(404, "父节点不存在");
        }
        node.setLevel(Objects.requireNonNullElse(parent.getLevel(), 0) + 1);
        node.setTreePath(TreeUtils.generateTreePath(parent.getTreePath(), node.getName()));
    }

    private void fillLevelAndPathForUpdate(DO db, DO updateObj) {
        Long newParentId = updateObj.getParentId();
        if (Objects.equals(db.getParentId(), newParentId)) {
            if (!Objects.equals(db.getName(), updateObj.getName())) {
                updateObj.setTreePath(TreeUtils.generateTreePath(extractParentPath(db.getTreePath()), updateObj.getName()));
            } else {
                updateObj.setTreePath(db.getTreePath());
            }
            updateObj.setLevel(db.getLevel());
            return;
        }
        DO parent = newParentId == null ? null : getMapper().selectById(newParentId);
        if (newParentId != null && parent == null) {
            throw new ServiceException(404, "父节点不存在");
        }
        int level = parent == null ? 1 : Objects.requireNonNullElse(parent.getLevel(), 0) + 1;
        checkLevelLimit(level);
        updateObj.setLevel(level);
        updateObj.setTreePath(TreeUtils.generateTreePath(parent == null ? null : parent.getTreePath(), updateObj.getName()));
    }

    private String extractParentPath(String treePath) {
        if (treePath == null) {
            return null;
        }
        int idx = treePath.lastIndexOf('/');
        return idx < 0 ? null : treePath.substring(0, idx);
    }

    private void validateUnique(DO node, boolean isCreate) {
        Long parentId = node.getParentId();
        DO nameDup = getMapper().selectByNameAndParent(node.getName(), parentId);
        if (nameDup != null && (isCreate || !Objects.equals(nameDup.getId(), node.getId()))) {
            throw new ServiceException(400, "同一父节点下名称已存在");
        }
        if (node.getSort() != null) {
            DO sortDup = getMapper().selectBySortAndParent(node.getSort(), parentId);
            if (sortDup != null && (isCreate || !Objects.equals(sortDup.getId(), node.getId()))) {
                throw new ServiceException(400, "同一父节点下排序已存在");
            }
        }
    }

    private void checkLevelLimit(int level) {
        if (level > getMaxLevel()) {
            throw new ServiceException(400, "树层级超过上限 " + getMaxLevel());
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
                .map(TreeContract::getId)
                .collect(Collectors.toList());
        List<Long> children = new ArrayList<>();
        for (Long id : results) {
            children.addAll(listDescendantIds(id, all));
        }
        results.addAll(children);
        return results;
    }

    private void updateChildrenPath(Long parentId, String parentPath, int parentLevel) {
        List<DO> children = getMapper().selectByParentId(parentId);
        for (DO child : children) {
            int newLevel = parentLevel + 1;
            checkLevelLimit(newLevel);
            String newPath = TreeUtils.generateTreePath(parentPath, child.getName());
            child.setLevel(newLevel);
            child.setTreePath(newPath);
            getMapper().updateById(child);
            updateChildrenPath(child.getId(), newPath, newLevel);
        }
    }
}
