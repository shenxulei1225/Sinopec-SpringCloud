package cn.iocoder.yudao.module.emergency.dal.mysql.dispatch;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.emergency.dal.dataobject.dispatch.ResourcePoolDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResourcePoolMapper extends BaseMapperX<ResourcePoolDO> {

    /**
     * 根据类型查询可用资源
     *
     * @param type 资源类型
     * @return 可用资源列表
     */
    List<ResourcePoolDO> selectAvailableByType(@Param("type") String type);

    /**
     * 根据类型和状态统计资源数量
     *
     * @param type   资源类型，为空时统计所有类型
     * @param status 资源状态，为空时统计所有状态
     * @return 资源数量
     */
    Integer countByTypeAndStatus(@Param("type") String type, @Param("status") String status);

    /**
     * 根据组织ID查询资源
     *
     * @param organizationId 组织ID
     * @return 资源列表
     */
    List<ResourcePoolDO> selectByOrganizationId(@Param("organizationId") Long organizationId);
}



