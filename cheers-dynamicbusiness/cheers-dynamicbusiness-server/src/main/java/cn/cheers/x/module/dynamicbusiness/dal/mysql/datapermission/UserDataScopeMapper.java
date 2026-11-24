package cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.UserDataScopeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/** 人员数据范围。只读写本表。 */
@Mapper
public interface UserDataScopeMapper extends BaseMapperX<UserDataScopeDO> {

    default List<UserDataScopeDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<UserDataScopeDO>()
                .eq(UserDataScopeDO::getUserId, userId)
                .orderByAsc(UserDataScopeDO::getRegistryCode)
                .orderByAsc(UserDataScopeDO::getUnitId));
    }

    default void deleteByUserId(Long userId) {
        delete(new LambdaQueryWrapperX<UserDataScopeDO>()
                .eq(UserDataScopeDO::getUserId, userId));
    }
}
