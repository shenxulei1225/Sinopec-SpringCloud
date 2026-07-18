package cn.cheers.x.module.dynamicbusiness.dal.mysql.unit;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.unit.UnitDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UnitMapper extends BaseMapperX<UnitDO> {

    default UnitDO selectByCode(String code, Long tenantId) {
        return selectOne(new LambdaQueryWrapperX<UnitDO>()
                .eq(UnitDO::getCode, code)
                .eq(UnitDO::getTenantId, tenantId)
                .eq(UnitDO::getDeleted, false));
    }

    default UnitDO selectByIdAndTenant(Long id, Long tenantId) {
        return selectOne(new LambdaQueryWrapperX<UnitDO>()
                .eq(UnitDO::getId, id)
                .eq(UnitDO::getTenantId, tenantId)
                .eq(UnitDO::getDeleted, false));
    }

    default List<UnitDO> selectListByType(String unitType) {
        return selectList(new LambdaQueryWrapperX<UnitDO>()
                .eqIfPresent(UnitDO::getUnitType, unitType)
                .eq(UnitDO::getDeleted, false)
                .orderByAsc(UnitDO::getSort)
                .orderByAsc(UnitDO::getId));
    }
}
