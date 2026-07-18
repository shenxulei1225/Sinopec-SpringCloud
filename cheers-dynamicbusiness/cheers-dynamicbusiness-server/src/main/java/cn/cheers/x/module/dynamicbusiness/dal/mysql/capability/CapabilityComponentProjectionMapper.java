package cn.cheers.x.module.dynamicbusiness.dal.mysql.capability;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.capability.CapabilityComponentProjectionDO;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 组件能力投影 Mapper。
 *
 * <p>说明：</p>
 * <ul>
 *   <li>面向 capability_component_projection 表；</li>
 *   <li>按 (entityTypeCode, componentCode) 读取单条投影；</li>
 *   <li>按 entityTypeCode 读取一组组件投影；</li>
 *   <li>不接受 dataSourceKey 作为查询键。</li>
 * </ul>
 */
@Mapper
public interface CapabilityComponentProjectionMapper extends BaseMapperX<CapabilityComponentProjectionDO> {

    /**
     * 按业务类型编码 + 组件编码查询 entity 投影（兼容旧调用）。
     */
    default CapabilityComponentProjectionDO selectByEntityTypeAndComponent(String entityTypeCode,
                                                                             String componentCode) {
        return selectByEntityTypeComponentAndDataKind(
                entityTypeCode, componentCode, "entity");
    }

    /**
     * 按业务类型编码 + 组件编码 + 数据种类查询唯一投影。
     */
    default CapabilityComponentProjectionDO selectByEntityTypeComponentAndDataKind(
            String entityTypeCode,
            String componentCode,
            String dataKind) {
        return selectOne(new LambdaQueryWrapperX<CapabilityComponentProjectionDO>()
                .eq(CapabilityComponentProjectionDO::getEntityTypeCode, entityTypeCode)
                .eq(CapabilityComponentProjectionDO::getComponentCode, componentCode)
                .eq(CapabilityComponentProjectionDO::getDataKind, dataKind));
    }

    /**
     * 查询某个业务类型下全部组件投影（按组件编码升序）。
     */
    default List<CapabilityComponentProjectionDO> selectByEntityTypeCode(String entityTypeCode) {
        return selectList(new LambdaQueryWrapperX<CapabilityComponentProjectionDO>()
                .eq(CapabilityComponentProjectionDO::getEntityTypeCode, entityTypeCode)
                .orderByAsc(CapabilityComponentProjectionDO::getComponentCode));
    }
}
