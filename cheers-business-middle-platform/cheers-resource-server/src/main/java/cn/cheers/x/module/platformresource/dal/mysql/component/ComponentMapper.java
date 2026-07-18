package cn.cheers.x.module.platformresource.dal.mysql.component;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.platformresource.dal.dataobject.component.ComponentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ComponentMapper extends BaseMapperX<ComponentDO> {

    default ComponentDO selectByComponentCode(String componentCode) {
        return selectOne(new LambdaQueryWrapperX<ComponentDO>()
                .eq(ComponentDO::getComponentCode, componentCode));
    }

    default List<ComponentDO> selectEnabledList() {
        return selectList(new LambdaQueryWrapperX<ComponentDO>()
                .eq(ComponentDO::getStatus, 1)
                .orderByAsc(ComponentDO::getSort));
    }

    default List<ComponentDO> selectAllList() {
        return selectList(new LambdaQueryWrapperX<ComponentDO>()
                .orderByAsc(ComponentDO::getSort));
    }

    default boolean existsByComponentCode(String componentCode) {
        return selectCount(new LambdaQueryWrapperX<ComponentDO>()
                .eq(ComponentDO::getComponentCode, componentCode)) > 0;
    }
}
