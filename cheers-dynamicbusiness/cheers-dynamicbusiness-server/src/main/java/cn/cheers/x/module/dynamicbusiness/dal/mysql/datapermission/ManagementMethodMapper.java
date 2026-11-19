package cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.ManagementMethodDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/** 可选的数据管理方式。只读写本表。 */
@Mapper
public interface ManagementMethodMapper extends BaseMapperX<ManagementMethodDO> {

    /** 按列顺序排好的全部方式的目录注册编码。 */
    default List<String> selectRegistryCodesOrdered() {
        return selectList(new LambdaQueryWrapperX<ManagementMethodDO>()
                .orderByAsc(ManagementMethodDO::getSort)
                .orderByAsc(ManagementMethodDO::getId))
                .stream().map(ManagementMethodDO::getRegistryCode).toList();
    }

    default void deleteByRegistryCode(String registryCode) {
        delete(new LambdaQueryWrapperX<ManagementMethodDO>()
                .eq(ManagementMethodDO::getRegistryCode, registryCode));
    }

    default void deleteAllRows() {
        delete(new LambdaQueryWrapperX<ManagementMethodDO>()
                .isNotNull(ManagementMethodDO::getId));
    }
}
