package cn.cheers.x.sinopec.device.service.admin.impl;

import cn.cheers.x.sinopec.device.api.admin.dto.DeviceListRespDTO;
import cn.cheers.x.sinopec.device.convert.admin.DeviceConvert;
import cn.cheers.x.sinopec.device.dal.dataobject.DeviceDetailsDO;
import cn.cheers.x.sinopec.device.dal.dataobject.EquipmentBusinessDynamicsDO;
import cn.cheers.x.sinopec.device.dal.mapper.DeviceDetailsMapper;
import cn.cheers.x.sinopec.device.dal.mapper.EquipmentBusinessDynamicsMapper;
import cn.cheers.x.sinopec.device.service.admin.DeviceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备查询服务实现。
 */
@Service
@RequiredArgsConstructor
public class DeviceQueryServiceImpl implements DeviceQueryService {

    private final DeviceDetailsMapper deviceDetailsMapper;
    private final EquipmentBusinessDynamicsMapper equipmentBusinessDynamicsMapper;

    @Override
    public List<DeviceListRespDTO> getDeviceList(Integer siteId) {
        List<EquipmentBusinessDynamicsDO> dynamicsList = equipmentBusinessDynamicsMapper.selectListBySiteId(siteId);
        List<DeviceDetailsDO> detailsList = deviceDetailsMapper.selectListBySiteId(siteId);
        Map<String, DeviceDetailsDO> detailsMap = buildDetailsMap(detailsList);

        List<DeviceListRespDTO> result = new ArrayList<>();
        for (EquipmentBusinessDynamicsDO dynamics : dynamicsList) {
            DeviceDetailsDO details = detailsMap.get(dynamics.getEquipmentSerialNo());
            result.add(DeviceConvert.convert(dynamics, details));
        }
        return result;
    }

    private Map<String, DeviceDetailsDO> buildDetailsMap(List<DeviceDetailsDO> detailsList) {
        Map<String, DeviceDetailsDO> detailsMap = new HashMap<>();
        for (DeviceDetailsDO details : detailsList) {
            detailsMap.put(details.getEquipmentSerialNo(), details);
        }
        return detailsMap;
    }

}
