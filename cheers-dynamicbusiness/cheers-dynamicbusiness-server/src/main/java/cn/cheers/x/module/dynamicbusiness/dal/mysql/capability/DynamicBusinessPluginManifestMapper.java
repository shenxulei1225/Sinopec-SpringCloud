package cn.cheers.x.module.dynamicbusiness.dal.mysql.capability;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.capability.DynamicBusinessPluginManifestDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 动态业务插件清单 Mapper。
 */
@Mapper
public interface DynamicBusinessPluginManifestMapper extends BaseMapperX<DynamicBusinessPluginManifestDO> {

    default List<DynamicBusinessPluginManifestDO> selectAllOrderByPluginId() {
        return selectList(new LambdaQueryWrapperX<DynamicBusinessPluginManifestDO>()
                .orderByAsc(DynamicBusinessPluginManifestDO::getPluginId));
    }

    default DynamicBusinessPluginManifestDO selectByPluginId(String pluginId) {
        return selectOne(new LambdaQueryWrapperX<DynamicBusinessPluginManifestDO>()
                .eq(DynamicBusinessPluginManifestDO::getPluginId, pluginId));
    }
}
