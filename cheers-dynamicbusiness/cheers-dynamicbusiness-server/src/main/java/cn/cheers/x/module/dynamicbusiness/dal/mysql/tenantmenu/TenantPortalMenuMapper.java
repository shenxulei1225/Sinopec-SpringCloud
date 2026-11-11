package cn.cheers.x.module.dynamicbusiness.dal.mysql.tenantmenu;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.tenantmenu.TenantPortalMenuDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TenantPortalMenuMapper extends BaseMapperX<TenantPortalMenuDO> {

    default List<TenantPortalMenuDO> selectByTenant(Long tenantId) {
        return selectList(new LambdaQueryWrapperX<TenantPortalMenuDO>()
                .eq(TenantPortalMenuDO::getTenantId, tenantId)
                .eq(TenantPortalMenuDO::getDeleted, false)
                .orderByAsc(TenantPortalMenuDO::getSort)
                .orderByAsc(TenantPortalMenuDO::getId));
    }

    default void deleteByTenant(Long tenantId) {
        delete(new LambdaQueryWrapperX<TenantPortalMenuDO>()
                .eq(TenantPortalMenuDO::getTenantId, tenantId));
    }
}
