package cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.DutyPostRoleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/** 职务绑定角色。只读写本表。 */
@Mapper
public interface DutyPostRoleMapper extends BaseMapperX<DutyPostRoleDO> {

    default List<DutyPostRoleDO> selectListByRoleId(Long roleId) {
        return selectList(new LambdaQueryWrapperX<DutyPostRoleDO>()
                .eq(DutyPostRoleDO::getRoleId, roleId));
    }

    default List<DutyPostRoleDO> selectListByDutyPostId(Long dutyPostId) {
        return selectList(new LambdaQueryWrapperX<DutyPostRoleDO>()
                .eq(DutyPostRoleDO::getDutyPostId, dutyPostId));
    }

    default List<DutyPostRoleDO> selectListByDutyPostIds(Collection<Long> dutyPostIds) {
        return selectList(new LambdaQueryWrapperX<DutyPostRoleDO>()
                .in(DutyPostRoleDO::getDutyPostId, dutyPostIds));
    }

    default void deleteByRoleId(Long roleId) {
        delete(new LambdaQueryWrapperX<DutyPostRoleDO>()
                .eq(DutyPostRoleDO::getRoleId, roleId));
    }

    /** 只删这个职务上的角色绑定，不动这个角色在其它职务上的绑定。 */
    default void deleteByDutyPostId(Long dutyPostId) {
        delete(new LambdaQueryWrapperX<DutyPostRoleDO>()
                .eq(DutyPostRoleDO::getDutyPostId, dutyPostId));
    }
}
