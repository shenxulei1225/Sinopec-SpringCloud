package cn.iocoder.yudao.module.scene.platform.dal.mysql.asset;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.asset.AssetResourceDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface AssetResourceMapper extends BaseMapperX<AssetResourceDO> {

    default List<AssetResourceDO> selectListByIds(Collection<Long> ids) {
        return selectList(new LambdaQueryWrapperX<AssetResourceDO>()
                .in(AssetResourceDO::getId, ids)
                .orderByAsc(AssetResourceDO::getId));
    }

    default AssetResourceDO selectByAssetCode(String assetCode) {
        return selectOne(new LambdaQueryWrapperX<AssetResourceDO>()
                .eq(AssetResourceDO::getAssetCode, assetCode));
    }
}
