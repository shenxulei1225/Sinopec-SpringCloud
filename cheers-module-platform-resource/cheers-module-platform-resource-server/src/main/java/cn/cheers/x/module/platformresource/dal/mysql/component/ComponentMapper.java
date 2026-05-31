package cn.cheers.x.module.platformresource.dal.mysql.component;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.platformresource.dal.dataobject.component.ComponentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ComponentMapper extends BaseMapperX<ComponentDO> {

    default ComponentDO selectByKey(String key) {
        return selectOne(new LambdaQueryWrapperX<ComponentDO>()
                .eq(ComponentDO::getKey, key));
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

    default boolean existsByKey(String key) {
        return selectCount(new LambdaQueryWrapperX<ComponentDO>()
                .eq(ComponentDO::getKey, key)) > 0;
    }
}