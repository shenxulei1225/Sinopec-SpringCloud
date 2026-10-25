package cn.cheers.x.module.dynamicbusiness.dal.mysql.cataloggrant;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.cataloggrant.CatalogRoleStationScopeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 数据目录的场站范围。
 * 只读写本表。不读角色上的系统部门数据范围。
 */
@Mapper
public interface CatalogRoleStationScopeMapper extends BaseMapperX<CatalogRoleStationScopeDO> {

    default List<CatalogRoleStationScopeDO> selectListByRoleId(Long roleId) {
        return selectList(new LambdaQueryWrapperX<CatalogRoleStationScopeDO>()
                .eq(CatalogRoleStationScopeDO::getRoleId, roleId));
    }

    default List<CatalogRoleStationScopeDO> selectListByRoleIdsAndRegistry(Collection<Long> roleIds, String registryCode) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<CatalogRoleStationScopeDO>()
                .in(CatalogRoleStationScopeDO::getRoleId, roleIds)
                .eq(CatalogRoleStationScopeDO::getRegistryCode, registryCode));
    }

    default void deleteByRoleId(Long roleId) {
        delete(new LambdaQueryWrapperX<CatalogRoleStationScopeDO>()
                .eq(CatalogRoleStationScopeDO::getRoleId, roleId));
    }
}
