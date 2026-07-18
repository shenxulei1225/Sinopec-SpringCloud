package cn.iocoder.yudao.module.scene.platform.dal.mysql.coordinate;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.coordinate.CoordinateCrsCatalogDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CoordinateCrsCatalogMapper extends BaseMapperX<CoordinateCrsCatalogDO> {

    default List<CoordinateCrsCatalogDO> selectListEnabled() {
        return selectList(new LambdaQueryWrapperX<CoordinateCrsCatalogDO>()
                .eq(CoordinateCrsCatalogDO::getEnabledFlag, true)
                .orderByAsc(CoordinateCrsCatalogDO::getSortNo, CoordinateCrsCatalogDO::getId));
    }

    default CoordinateCrsCatalogDO selectByCrsCode(String crsCode) {
        return selectOne(new LambdaQueryWrapperX<CoordinateCrsCatalogDO>()
                .eq(CoordinateCrsCatalogDO::getCrsCode, crsCode));
    }
}
