package cn.cheers.x.sinopec.device.dal.mapper;

import cn.cheers.x.sinopec.device.dal.dataobject.DeviceDetailsDO;

import java.util.List;

/**
 * 设备详情 Mapper。
 */
public interface DeviceDetailsMapper {

    List<DeviceDetailsDO> selectListByFacilityId(Integer facilityId);

    DeviceDetailsDO selectByEquipmentSerialNo(String equipmentSerialNo);

}
