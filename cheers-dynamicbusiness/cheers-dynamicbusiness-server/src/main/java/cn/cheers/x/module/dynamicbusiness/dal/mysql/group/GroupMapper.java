package cn.cheers.x.module.dynamicbusiness.dal.mysql.group;

import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.group.GroupDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMapper extends BaseMapperX<GroupDO> {

    default GroupDO selectByTypeAndName(String groupType, String name) {
        return selectOne(new LambdaQueryWrapperX<GroupDO>()
                .eq(GroupDO::getGroupType, groupType)
                .eq(GroupDO::getName, name)
                .eq(GroupDO::getDeleted, false));
    }

    default List<GroupDO> selectByTypeAndParentId(String groupType, Long parentId) {
        return selectList(new LambdaQueryWrapperX<GroupDO>()
                .eq(GroupDO::getGroupType, groupType)
                .eq(GroupDO::getParentId, parentId)
                .eq(GroupDO::getDeleted, false)
                .orderByAsc(GroupDO::getSort, GroupDO::getId));
    }

    default List<GroupDO> searchLike(String groupType, String keyword) {
        return selectList(new LambdaQueryWrapperX<GroupDO>()
                .eq(GroupDO::getGroupType, groupType)
                .and(w -> w.like(GroupDO::getName, keyword)
                        .or().like(GroupDO::getDescription, keyword))
                .eq(GroupDO::getDeleted, false)
                .orderByAsc(GroupDO::getSort, GroupDO::getId));
    }

    default PageResult<GroupDO> selectPageByType(PageParam pageParam, String groupType, String name, Integer status) {
        return selectPage(pageParam, new LambdaQueryWrapperX<GroupDO>()
                .eq(GroupDO::getGroupType, groupType)
                .likeIfPresent(GroupDO::getName, name)
                .eqIfPresent(GroupDO::getStatus, status)
                .eq(GroupDO::getDeleted, false)
                .orderByAsc(GroupDO::getSort, GroupDO::getId));
    }
}
