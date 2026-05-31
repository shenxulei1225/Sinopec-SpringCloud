package cn.cheers.x.module.dynamicbusiness.service.unit;

import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.UnitCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.unit.UnitDO;

import java.util.List;

public interface UnitService {

    Long createUnit(UnitCreateReqVO reqVO);

    void updateUnit(Long id, UnitCreateReqVO reqVO);

    void deleteUnit(Long id);

    List<UnitDO> listUnits(String unitType);
}
