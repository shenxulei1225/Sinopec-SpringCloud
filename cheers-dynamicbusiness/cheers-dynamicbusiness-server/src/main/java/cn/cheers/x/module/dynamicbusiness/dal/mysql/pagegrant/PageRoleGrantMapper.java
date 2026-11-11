package cn.cheers.x.module.dynamicbusiness.dal.mysql.pagegrant;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.pagegrant.PageRoleGrantDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 页面角色授权。
 * 只读写本表。不查菜单，不查数据目录授权。
 */
@Mapper
public interface PageRoleGrantMapper extends BaseMapperX<PageRoleGrantDO> {

    default List<PageRoleGrantDO> selectListByRoleId(Long roleId) {
        return selectList(new LambdaQueryWrapperX<PageRoleGrantDO>()
                .eq(PageRoleGrantDO::getRoleId, roleId));
    }

    /** 当前用户的角色在这些页面上已勾的动作。空角色不查。 */
    default List<PageRoleGrantDO> selectListByRoleIds(Collection<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<PageRoleGrantDO>()
                .in(PageRoleGrantDO::getRoleId, roleIds));
    }

    default void deleteByRoleId(Long roleId) {
        delete(new LambdaQueryWrapperX<PageRoleGrantDO>()
                .eq(PageRoleGrantDO::getRoleId, roleId));
    }
}
