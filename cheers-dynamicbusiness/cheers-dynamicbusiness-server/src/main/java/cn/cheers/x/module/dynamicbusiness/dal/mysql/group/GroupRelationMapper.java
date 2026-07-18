package cn.cheers.x.module.dynamicbusiness.dal.mysql.group;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.group.GroupRelationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupRelationMapper extends BaseMapperX<GroupRelationDO> {

    default GroupRelationDO selectByTypeAndTargetAndGroup(String groupType, Long targetId, Long groupId) {
        return selectOne(new LambdaQueryWrapperX<GroupRelationDO>()
                .eq(GroupRelationDO::getGroupType, groupType)
                .eq(GroupRelationDO::getTargetId, targetId)
                .eq(GroupRelationDO::getGroupId, groupId)
                .eq(GroupRelationDO::getDeleted, false));
    }

    default List<GroupRelationDO> selectByTypeAndGroupId(String groupType, Long groupId) {
        return selectList(new LambdaQueryWrapperX<GroupRelationDO>()
                .eq(GroupRelationDO::getGroupType, groupType)
                .eq(GroupRelationDO::getGroupId, groupId)
                .eq(GroupRelationDO::getDeleted, false)
                .orderByAsc(GroupRelationDO::getSort, GroupRelationDO::getId));
    }

    default void deleteByTypeAndTargetAndGroup(String groupType, Long targetId, Long groupId) {
        delete(new LambdaQueryWrapperX<GroupRelationDO>()
                .eq(GroupRelationDO::getGroupType, groupType)
                .eq(GroupRelationDO::getTargetId, targetId)
                .eq(GroupRelationDO::getGroupId, groupId));
    }

    default void deleteByTypeAndGroupId(String groupType, Long groupId) {
        delete(new LambdaQueryWrapperX<GroupRelationDO>()
                .eq(GroupRelationDO::getGroupType, groupType)
                .eq(GroupRelationDO::getGroupId, groupId));
    }
}
