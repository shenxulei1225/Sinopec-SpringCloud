package cn.cheers.x.module.dynamicbusiness.dal.mysql.cataloggrant;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.cataloggrant.CatalogRoleGrantDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 数据目录角色授权。
 * 只读写本表。不查菜单，不按存数编码匹配。
 */
@Mapper
public interface CatalogRoleGrantMapper extends BaseMapperX<CatalogRoleGrantDO> {

    default List<CatalogRoleGrantDO> selectListByRoleId(Long roleId) {
        return selectList(new LambdaQueryWrapperX<CatalogRoleGrantDO>()
                .eq(CatalogRoleGrantDO::getRoleId, roleId));
    }

    default void deleteByRoleId(Long roleId) {
        delete(new LambdaQueryWrapperX<CatalogRoleGrantDO>()
                .eq(CatalogRoleGrantDO::getRoleId, roleId));
    }

    /**
     * 当前用户任一角色是否拥有该目录的这一项操作。
     * 角色编号为空时不放行。
     */
    default boolean existsGrant(Collection<Long> roleIds, String registryCode, String operation) {
        if (roleIds == null || roleIds.isEmpty()) {
            return false;
        }
        return selectCount(new LambdaQueryWrapperX<CatalogRoleGrantDO>()
                .in(CatalogRoleGrantDO::getRoleId, roleIds)
                .eq(CatalogRoleGrantDO::getRegistryCode, registryCode)
                .eq(CatalogRoleGrantDO::getOperation, operation)) > 0;
    }
}
