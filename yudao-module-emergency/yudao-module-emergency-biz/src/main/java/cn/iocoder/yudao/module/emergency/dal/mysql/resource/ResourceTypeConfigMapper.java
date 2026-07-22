package cn.iocoder.yudao.module.emergency.dal.mysql.resource;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.emergency.dal.dataobject.resource.ResourceTypeConfigDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 资源类型配置 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface ResourceTypeConfigMapper extends BaseMapperX<ResourceTypeConfigDO> {

    /**
     * 根据资源类型查询配置
     *
     * @param resourceType 资源类型
     * @return 资源类型配置
     */
    ResourceTypeConfigDO selectByResourceType(@Param("resourceType") String resourceType);
}
