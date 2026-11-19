package cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.CatalogOwnershipDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/** 归属声明。只读写本表。 */
@Mapper
public interface CatalogOwnershipMapper extends BaseMapperX<CatalogOwnershipDO> {

    default List<CatalogOwnershipDO> selectListByRegistryCode(String registryCode) {
        return selectList(new LambdaQueryWrapperX<CatalogOwnershipDO>()
                .eq(CatalogOwnershipDO::getRegistryCode, registryCode)
                .orderByAsc(CatalogOwnershipDO::getId));
    }

    /** 哪些目录的归属声明指向这本目录。 */
    default List<CatalogOwnershipDO> selectListByTargetRegistryCode(String targetRegistryCode) {
        return selectList(new LambdaQueryWrapperX<CatalogOwnershipDO>()
                .eq(CatalogOwnershipDO::getTargetRegistryCode, targetRegistryCode)
                .orderByAsc(CatalogOwnershipDO::getId));
    }

    default void deleteByRegistryCode(String registryCode) {
        delete(new LambdaQueryWrapperX<CatalogOwnershipDO>()
                .eq(CatalogOwnershipDO::getRegistryCode, registryCode));
    }
}
