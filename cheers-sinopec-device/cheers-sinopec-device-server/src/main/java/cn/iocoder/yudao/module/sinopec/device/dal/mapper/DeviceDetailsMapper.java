package cn.iocoder.yudao.module.sinopec.device.dal.mapper;

import cn.iocoder.yudao.module.sinopec.device.dal.dataobject.DeviceDetailsDO;

import java.util.List;

/**
 * 设备详情 Mapper。
 */
public interface DeviceDetailsMapper {

    List<DeviceDetailsDO> selectListBySiteId(Integer siteId);

    DeviceDetailsDO selectByEquipmentSerialNo(String equipmentSerialNo);

}
