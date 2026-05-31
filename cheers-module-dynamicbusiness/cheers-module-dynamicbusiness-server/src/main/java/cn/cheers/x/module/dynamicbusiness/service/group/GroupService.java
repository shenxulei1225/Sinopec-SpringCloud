package cn.cheers.x.module.dynamicbusiness.service.group;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.group.GroupDO;

import java.util.List;

public interface GroupService {

    Long createGroup(String groupType, String name, String description, Long parentId, Integer sort, Integer status, String codePrefix);

    void updateGroup(String groupType, Long id, String name, String description, Long parentId, Integer sort, Integer status);

    void deleteGroup(String groupType, Long id);

    GroupDO getGroup(String groupType, Long id);

    List<GroupDO> listGroups(String groupType);

    PageResult<GroupDO> pageGroups(String groupType, PageParam pageParam, String name, Integer status);

    List<GroupDO> searchGroups(String groupType, String keyword);

    List<GroupTreeNode> treeGroups(String groupType);

    List<GroupTreeNodeWithTargets> treeGroupsWithTargets(String groupType, String targetType);

    void bindTarget(String groupType, Long targetId, Long groupId);

    void unbindTarget(String groupType, Long targetId, Long groupId);

    List<Long> getTargetIdsByGroup(String groupType, Long groupId);

    void reorderTargets(String groupType, Long groupId, List<Long> orderedTargetIds);

    void reorderGroup(String groupType, Long movedGroupId, Long prevGroupId, Long nextGroupId);

    void batchBindTargets(String groupType, Long groupId, List<Long> targetIds);

    void batchUnbindTargets(String groupType, Long groupId, List<Long> targetIds);
}
