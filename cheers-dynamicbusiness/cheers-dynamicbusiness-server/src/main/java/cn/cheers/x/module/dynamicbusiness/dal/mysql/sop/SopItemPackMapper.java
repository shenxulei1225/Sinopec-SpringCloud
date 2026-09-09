package cn.cheers.x.module.dynamicbusiness.dal.mysql.sop;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.SopItemPackDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SopItemPackMapper extends BaseMapperX<SopItemPackDO> {

    default List<SopItemPackDO> selectBySopId(Long sopId) {
        return selectList(new LambdaQueryWrapperX<SopItemPackDO>()
                .eq(SopItemPackDO::getSopId, sopId)
                .orderByAsc(SopItemPackDO::getSortNo)
                .orderByAsc(SopItemPackDO::getId));
    }

    default void deleteBySopId(Long sopId) {
        delete(new LambdaQueryWrapperX<SopItemPackDO>()
                .eq(SopItemPackDO::getSopId, sopId));
    }
}
