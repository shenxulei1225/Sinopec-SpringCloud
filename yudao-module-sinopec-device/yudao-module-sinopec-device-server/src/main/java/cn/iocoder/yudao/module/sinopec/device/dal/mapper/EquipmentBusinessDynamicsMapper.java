package cn.iocoder.yudao.module.sinopec.device.dal.mapper;

import cn.iocoder.yudao.module.sinopec.device.dal.dataobject.EquipmentBusinessDynamicsDO;

import java.util.List;

/**
 * 设备业务动态 Mapper。
 */
public interface EquipmentBusinessDynamicsMapper {

    List<EquipmentBusinessDynamicsDO> selectListBySiteId(Integer siteId);

}
