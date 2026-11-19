package cn.cheers.x.scene.platform.dal.mysql.basemap;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.scene.platform.dal.dataobject.basemap.GisProviderConnectionDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 地图服务连接表。租户条件由框架按当前客户自动加上。
 */
@Mapper
public interface GisProviderConnectionMapper extends BaseMapperX<GisProviderConnectionDO> {

    default GisProviderConnectionDO selectByProviderCode(String providerCode) {
        return selectOne(GisProviderConnectionDO::getProviderCode, providerCode);
    }
}
