package cn.cheers.x.sinopec.device.convert.admin;

import cn.cheers.x.sinopec.device.api.admin.dto.DeviceListRespDTO;
import cn.cheers.x.sinopec.device.dal.dataobject.DeviceDetailsDO;
import cn.cheers.x.sinopec.device.dal.dataobject.EquipmentBusinessDynamicsDO;

/**
 * 设备管理数据转换器。
 */
public class DeviceConvert {

    private DeviceConvert() {
    }

    public static DeviceListRespDTO convert(EquipmentBusinessDynamicsDO dynamics, DeviceDetailsDO details) {
        DeviceListRespDTO dto = new DeviceListRespDTO();
        if (dynamics != null) {
            dto.setDeviceId(dynamics.getEquipmentSerialNo());
            dto.setDeviceName(dynamics.getEquipmentName());
            dto.setDeviceType(dynamics.getEquipmentType());
            dto.setDeviceModel(dynamics.getModel());
        }
        if (details != null) {
            dto.setCurrentStateTypeCode(details.getStateCode());
            dto.setCurrentStateTypeDesc(details.getStateDesc());
        }
        return dto;
    }

}
