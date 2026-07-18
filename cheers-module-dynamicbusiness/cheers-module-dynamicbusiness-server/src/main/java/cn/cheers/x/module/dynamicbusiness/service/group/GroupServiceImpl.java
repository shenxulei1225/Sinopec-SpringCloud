package cn.cheers.x.module.dynamicbusiness.service.group;

import cn.hutool.core.util.IdUtil;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.group.GroupDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.group.GroupRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.group.GroupMapper;
import cn.cheers.x.module.dynamicbusiness.enums.group.GroupTypeEnum;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.group.GroupRelationMapper;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import cn.cheers.x.module.dynamicbusiness.util.SparseSortUtils;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Validated
public class GroupServiceImpl implements GroupService {

    @Resource
    private GroupMapper groupMapper;
    @Resource
    private GroupRelationMapper groupRelationMapper;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Long createGroup(String groupType, String name, String description, Long parentId, Integer sort, Integer status, String codePrefix) {
        GroupDO existGroup = groupMapper.selectByTypeAndName(groupType, name);
        if (existGroup != null) {
            throw new ServiceException(400, "分组名称已存在：" + name);
        }
        if (parentId != null && parentId > 0) {
            assertGroup(groupType, parentId);
        }
        Integer finalStatus = status == null ? 1 : status;
        Integer finalSort = sort;
        if (finalSort == null) {
            int maxSort = groupMapper.selectByTypeAndParentId(groupType, parentId).stream()
                    .map(GroupDO::getSort)
                    .filter(java.util.Objects::nonNull)
                    .max(Integer::compareTo)
                    .orElse(0);
            finalSort = SparseSortUtils.next(maxSort);
        }

        GroupDO group = GroupDO.builder()
                .groupType(groupType)
                .code(generateCode(codePrefix))
                .name(name)
                .description(description)
                .parentId(parentId)
                .sort(finalSort)
                .status(finalStatus)
                .build();
        syncGroupParentCode(group);
        group.setTenantId(getTenantId());
        groupMapper.insert(group);
        GroupCacheHelper.cacheGroup(stringRedisTemplate, groupType, group);
        GroupCacheHelper.evictTree(stringRedisTemplate, groupType, getTenantId());
        return group.getId();
    }

    @Override
    public void updateGroup(String groupType, Long id, String name, String description, Long parentId, Integer sort, Integer status) {
        GroupDO existGroup = assertGroup(groupType, id);
        String oldName = existGroup.getName();
        GroupDO nameGroup = groupMapper.selectByTypeAndName(groupType, name);
        if (nameGroup != null && !nameGroup.getId().equals(id)) {
            throw new ServiceException(400, "分组名称已存在：" + name);
        }
        if (parentId != null && !parentId.equals(existGroup.getParentId()) && parentId > 0) {
            GroupDO parentGroup = assertGroup(groupType, parentId);
            if (isDescendant(groupType, id, parentGroup.getId())) {
                throw new ServiceException(400, "不能将分组移动到其后代分组下");
            }
        }
        GroupDO update = GroupDO.builder()
                .id(id)
                .name(name)
                .description(description)
                .parentId(parentId)
                .sort(sort)
                .status(status)
                .build();
        syncGroupParentCode(update);
        groupMapper.updateById(update);
        if (isEntityTypeGroup(groupType) && name != null && !name.equals(oldName)) {
            renameEntityTypeGroupName(oldName, name);
        }
        GroupCacheHelper.evictGroup(stringRedisTemplate, groupType, id);
        GroupCacheHelper.evictTree(stringRedisTemplate, groupType, getTenantId());
    }

    @Override
    public void deleteGroup(String groupType, Long id) {
        GroupDO group = assertGroup(groupType, id);
        List<GroupDO> childGroups = groupMapper.selectByTypeAndParentId(groupType, id);
        if (!childGroups.isEmpty()) {
            throw new ServiceException(400, "分组下有子分组，请先删除子分组");
        }
        if (isEntityTypeGroup(groupType)) {
            clearEntityTypeGroupName(group.getName());
        }
        groupMapper.deleteById(id);
        groupRelationMapper.deleteByTypeAndGroupId(groupType, id);
        GroupCacheHelper.evictGroup(stringRedisTemplate, groupType, id);
        GroupCacheHelper.evictTree(stringRedisTemplate, groupType, getTenantId());
    }

    @Override
    public GroupDO getGroup(String groupType, Long id) {
        GroupDO cached = GroupCacheHelper.getCachedGroup(stringRedisTemplate, groupType, id);
        if (cached != null) return cached;
        GroupDO group = assertGroup(groupType, id);
        GroupCacheHelper.cacheGroup(stringRedisTemplate, groupType, group);
        return group;
    }

    @Override
    public List<GroupDO> listGroups(String groupType) {
        return groupMapper.selectList(new LambdaQueryWrapperX<GroupDO>()
                .eq(GroupDO::getGroupType, groupType)
                .eq(GroupDO::getDeleted, false)
                .orderByAsc(GroupDO::getSort, GroupDO::getId));
    }

    @Override
    public PageResult<GroupDO> pageGroups(String groupType, PageParam pageParam, String name, Integer status) {
        return groupMapper.selectPageByType(pageParam, groupType, name, status);
    }

    @Override
    public List<GroupDO> searchGroups(String groupType, String keyword) {
        return groupMapper.searchLike(groupType, keyword);
    }

    @Override
    public List<GroupTreeNode> treeGroups(String groupType) {
        Long tenantId = getTenantId();
        List<GroupTreeNode> cached = GroupCacheHelper.getCachedTree(stringRedisTemplate, groupType, tenantId, GroupTreeNode.class);
        if (cached != null) return cached;

        List<GroupDO> allGroups = listGroups(groupType);
        List<GroupTreeNode> tree = buildTree(allGroups, null);
        GroupCacheHelper.cacheTree(stringRedisTemplate, groupType, tenantId, tree);
        return tree;
    }

    @Override
    public List<GroupTreeNodeWithTargets> treeGroupsWithTargets(String groupType, String targetType) {
        List<GroupDO> allGroups = listGroups(groupType);
        if (allGroups.isEmpty()) {
            return List.of();
        }

        Map<Long, List<Object>> targetMap = loadTargetsByGroup(groupType, targetType, allGroups);
        return buildTreeWithTargets(allGroups, null, targetMap);
    }

    @Override
    public void bindTarget(String groupType, Long targetId, Long groupId) {
        assertGroup(groupType, groupId);
        GroupRelationDO exist = groupRelationMapper.selectByTypeAndTargetAndGroup(groupType, targetId, groupId);
        if (exist != null) return;
        GroupRelationDO relation = GroupRelationDO.builder()
                .groupType(groupType)
                .targetId(targetId)
                .groupId(groupId)
                .build();
        syncGroupRelationIdentity(groupType, groupId, targetId, relation);
        relation.setTenantId(getTenantId());
        groupRelationMapper.insert(relation);
    }

    @Override
    public void unbindTarget(String groupType, Long targetId, Long groupId) {
        groupRelationMapper.deleteByTypeAndTargetAndGroup(groupType, targetId, groupId);
    }

    @Override
    public List<Long> getTargetIdsByGroup(String groupType, Long groupId) {
        return groupRelationMapper.selectByTypeAndGroupId(groupType, groupId)
                .stream().map(GroupRelationDO::getTargetId).collect(Collectors.toList());
    }

    @Override
    public void reorderTargets(String groupType, Long groupId, List<Long> orderedTargetIds) {
        assertGroup(groupType, groupId);
        List<GroupRelationDO> relations = groupRelationMapper.selectByTypeAndGroupId(groupType, groupId);
        if (relations == null || relations.isEmpty()) return;
        Map<Long, GroupRelationDO> relationByTargetId = relations.stream()
                .collect(Collectors.toMap(GroupRelationDO::getTargetId, r -> r, (a, b) -> a));
        LinkedHashSet<Long> normalized = new LinkedHashSet<>();
        if (orderedTargetIds != null) {
            for (Long targetId : orderedTargetIds) {
                if (targetId != null && relationByTargetId.containsKey(targetId)) {
                    normalized.add(targetId);
                }
            }
        }
        for (GroupRelationDO relation : relations) {
            if (relation.getTargetId() != null) normalized.add(relation.getTargetId());
        }
        int sort = 1;
        for (Long targetId : normalized) {
            GroupRelationDO relation = relationByTargetId.get(targetId);
            if (relation == null) continue;
            relation.setSort(sort++);
            groupRelationMapper.updateById(relation);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reorderGroup(String groupType, Long movedGroupId, Long prevGroupId, Long nextGroupId) {
        GroupDO moved = assertGroup(groupType, movedGroupId);
        GroupDO prev = prevGroupId == null ? null : assertGroup(groupType, prevGroupId);
        GroupDO next = nextGroupId == null ? null : assertGroup(groupType, nextGroupId);

        Long movedParentId = moved.getParentId() == null ? 0L : moved.getParentId();
        if (prev != null) {
            Long prevParentId = prev.getParentId() == null ? 0L : prev.getParentId();
            if (!movedParentId.equals(prevParentId)) {
                throw new ServiceException(400, "排序节点不在同一父分组下");
            }
        }
        if (next != null) {
            Long nextParentId = next.getParentId() == null ? 0L : next.getParentId();
            if (!movedParentId.equals(nextParentId)) {
                throw new ServiceException(400, "排序节点不在同一父分组下");
            }
        }

        Integer prevSort = prev == null ? null : prev.getSort();
        Integer nextSort = next == null ? null : next.getSort();
        int newSort;

        Integer between = SparseSortUtils.between(prevSort, nextSort);
        if (between != null) {
            newSort = between;
        } else {
            List<GroupDO> siblings = groupMapper.selectByTypeAndParentId(groupType, movedParentId == 0 ? null : movedParentId);
            for (int i = 0; i < siblings.size(); i++) {
                GroupDO sibling = siblings.get(i);
                sibling.setSort(SparseSortUtils.reindexSortByPosition(i));
                groupMapper.updateById(sibling);
            }
            GroupDO refreshedPrev = prevGroupId == null ? null : assertGroup(groupType, prevGroupId);
            GroupDO refreshedNext = nextGroupId == null ? null : assertGroup(groupType, nextGroupId);
            Integer rebuilt = SparseSortUtils.between(
                    refreshedPrev == null ? null : refreshedPrev.getSort(),
                    refreshedNext == null ? null : refreshedNext.getSort()
            );
            newSort = rebuilt == null ? SparseSortUtils.next(0) : rebuilt;
        }

        GroupDO update = GroupDO.builder()
                .id(movedGroupId)
                .sort(newSort)
                .build();
        groupMapper.updateById(update);
        GroupCacheHelper.evictGroup(stringRedisTemplate, groupType, movedGroupId);
        GroupCacheHelper.evictTree(stringRedisTemplate, groupType, getTenantId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchBindTargets(String groupType, Long groupId, List<Long> targetIds) {
        assertGroup(groupType, groupId);
        if (targetIds == null) return;
        for (Long targetId : targetIds) {
            bindTarget(groupType, targetId, groupId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUnbindTargets(String groupType, Long groupId, List<Long> targetIds) {
        if (targetIds == null) return;
        for (Long targetId : targetIds) {
            unbindTarget(groupType, targetId, groupId);
        }
    }

    private GroupDO assertGroup(String groupType, Long id) {
        GroupDO group = groupMapper.selectById(id);
        if (group == null || !groupType.equals(group.getGroupType())) {
            throw new ServiceException(404, "分组不存在");
        }
        return group;
    }

    private Long getTenantId() {
        return TenantContextHolder.getTenantId();
    }

    private boolean isEntityTypeGroup(String groupType) {
        return GroupTypeEnum.ENTITY_TYPE.getCode().equals(groupType);
    }

    private void renameEntityTypeGroupName(String oldName, String newName) {
        List<EntityTypeDO> entityTypes = entityTypeMapper.selectList(new LambdaQueryWrapperX<EntityTypeDO>()
                .eq(EntityTypeDO::getGroupName, oldName)
                .eq(EntityTypeDO::getDeleted, false));
        for (EntityTypeDO entityType : entityTypes) {
            EntityTypeDO patch = new EntityTypeDO();
            patch.setId(entityType.getId());
            patch.setGroupName(newName);
            entityTypeMapper.updateById(patch);
        }
    }

    private void clearEntityTypeGroupName(String groupName) {
        List<EntityTypeDO> entityTypes = entityTypeMapper.selectList(new LambdaQueryWrapperX<EntityTypeDO>()
                .eq(EntityTypeDO::getGroupName, groupName)
                .eq(EntityTypeDO::getDeleted, false));
        for (EntityTypeDO entityType : entityTypes) {
            EntityTypeDO patch = new EntityTypeDO();
            patch.setId(entityType.getId());
            patch.setGroupName(null);
            entityTypeMapper.updateById(patch);
        }
    }

    private String generateCode(String prefix) {
        String p = (prefix == null || prefix.isEmpty()) ? "GRP" : prefix;
        return p + "-" + IdUtil.getSnowflakeNextIdStr();
    }

    private boolean isDescendant(String groupType, Long groupId, Long targetId) {
        GroupDO target = groupMapper.selectById(targetId);
        if (target == null || !groupType.equals(target.getGroupType()) || target.getParentId() == null || target.getParentId() == 0) {
            return false;
        }
        if (target.getParentId().equals(groupId)) return true;
        return isDescendant(groupType, groupId, target.getParentId());
    }

    private List<GroupTreeNode> buildTree(List<GroupDO> allGroups, Long parentId) {
        return allGroups.stream()
                .filter(group -> parentId == null
                        ? group.getParentId() == null || group.getParentId() == 0
                        : parentId.equals(group.getParentId()))
                .map(group -> {
                    GroupTreeNode node = new GroupTreeNode();
                    node.setId(group.getId());
                    node.setCode(group.getCode());
                    node.setName(group.getName());
                    node.setDescription(group.getDescription());
                    node.setParentId(group.getParentId());
                    node.setPath(group.getPath());
                    node.setLevel(group.getLevel());
                    node.setSort(group.getSort());
                    node.setStatus(group.getStatus());
                    node.setCreateTime(group.getCreateTime());
                    node.setChildren(buildTree(allGroups, group.getId()));
                    return node;
                })
                .collect(Collectors.toList());
    }

    private Map<Long, List<Object>> loadTargetsByGroup(String groupType, String targetType, List<GroupDO> allGroups) {
        if (!"FIELD".equalsIgnoreCase(targetType)) {
            return Map.of();
        }
        List<Long> groupIds = allGroups.stream().map(GroupDO::getId).toList();
        if (groupIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, List<Long>> targetIdsByGroup = groupRelationMapper.selectList(new LambdaQueryWrapperX<GroupRelationDO>()
                        .eq(GroupRelationDO::getGroupType, groupType)
                        .in(GroupRelationDO::getGroupId, groupIds)
                        .eq(GroupRelationDO::getDeleted, false)
                        .orderByAsc(GroupRelationDO::getSort, GroupRelationDO::getId))
                .stream()
                .collect(Collectors.groupingBy(
                        GroupRelationDO::getGroupId,
                        LinkedHashMap::new,
                        Collectors.mapping(GroupRelationDO::getTargetId, Collectors.toList())
                ));

        List<Long> allTargetIds = targetIdsByGroup.values().stream().flatMap(List::stream).distinct().toList();
        if (allTargetIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, FieldDO> fieldMap = fieldMapper.selectByIds(allTargetIds).stream()
                .filter(field -> field != null && !Boolean.TRUE.equals(field.getDeleted()))
                .collect(Collectors.toMap(FieldDO::getId, field -> field, (a, b) -> a));

        return targetIdsByGroup.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().stream()
                        .map(fieldMap::get)
                        .filter(java.util.Objects::nonNull)
                        .map(field -> (Object) field)
                        .collect(Collectors.toList()),
                (a, b) -> a,
                LinkedHashMap::new
        ));
    }

    private List<GroupTreeNodeWithTargets> buildTreeWithTargets(List<GroupDO> allGroups, Long parentId, Map<Long, List<Object>> targetMap) {
        return allGroups.stream()
                .filter(group -> parentId == null
                        ? group.getParentId() == null || group.getParentId() == 0
                        : parentId.equals(group.getParentId()))
                .map(group -> {
                    GroupTreeNodeWithTargets node = new GroupTreeNodeWithTargets();
                    node.setId(group.getId());
                    node.setCode(group.getCode());
                    node.setName(group.getName());
                    node.setDescription(group.getDescription());
                    node.setParentId(group.getParentId());
                    node.setPath(group.getPath());
                    node.setLevel(group.getLevel());
                    node.setSort(group.getSort());
                    node.setStatus(group.getStatus());
                    node.setCreateTime(group.getCreateTime());
                    node.setTargets(targetMap.getOrDefault(group.getId(), List.of()));
                    node.setChildren(buildTreeWithTargets(allGroups, group.getId(), targetMap));
                    return node;
                })
                .collect(Collectors.toList());
    }

    private void syncGroupParentCode(GroupDO group) {
        if (group == null) {
            return;
        }
        Long parentId = group.getParentId();
        if (parentId == null || parentId <= 0) {
            group.setParentCode(null);
            return;
        }
        GroupDO parent = groupMapper.selectById(parentId);
        if (parent != null) {
            group.setParentCode(parent.getCode());
        }
    }

    /** 写入分组关联时同步 group_code + target_code（迁移以 code 为幂等键）。 */
    private void syncGroupRelationIdentity(String groupType, Long groupId, Long targetId, GroupRelationDO relation) {
        if (relation == null) {
            return;
        }
        GroupDO group = groupMapper.selectById(groupId);
        if (group != null) {
            relation.setGroupId(group.getId());
            relation.setGroupCode(group.getCode());
        }
        if ("FIELD".equals(groupType)) {
            FieldDO field = fieldMapper.selectById(targetId);
            if (field != null) {
                relation.setTargetId(field.getId());
                relation.setTargetCode(field.getCode());
            }
        }
    }
}
