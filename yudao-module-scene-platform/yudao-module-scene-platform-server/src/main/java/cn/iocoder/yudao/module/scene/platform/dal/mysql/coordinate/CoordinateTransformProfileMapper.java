package cn.iocoder.yudao.module.scene.platform.dal.mysql.coordinate;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.coordinate.CoordinateTransformProfileDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CoordinateTransformProfileMapper extends BaseMapperX<CoordinateTransformProfileDO> {

    default List<CoordinateTransformProfileDO> selectEnabledList() {
        return selectList(new LambdaQueryWrapperX<CoordinateTransformProfileDO>()
                .eq(CoordinateTransformProfileDO::getStatus, 1)
                .orderByAsc(CoordinateTransformProfileDO::getProfileCode));
    }

    default CoordinateTransformProfileDO selectByProfileCode(String profileCode) {
        return selectOne(new LambdaQueryWrapperX<CoordinateTransformProfileDO>()
                .eq(CoordinateTransformProfileDO::getProfileCode, profileCode));
    }
}
