package cn.cheers.x.sinopec.device.dal.mapper;

import cn.cheers.x.sinopec.device.dal.dataobject.EquipmentBusinessDynamicsDO;

import java.util.List;

/**
 * 设备业务动态 Mapper。
 */
public interface EquipmentBusinessDynamicsMapper {

    List<EquipmentBusinessDynamicsDO> selectListByFacilityId(Integer facilityId);

}
