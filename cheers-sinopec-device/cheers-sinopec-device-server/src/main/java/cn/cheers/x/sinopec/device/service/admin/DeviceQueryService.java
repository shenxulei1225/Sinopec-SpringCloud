package cn.cheers.x.sinopec.device.service.admin;

import cn.cheers.x.sinopec.device.api.admin.dto.DeviceListRespDTO;

import java.util.List;

/**
 * 设备查询服务。
 */
public interface DeviceQueryService {

    /**
     * 查询设备列表。
     *
     * @param siteId 站场编号
     * @return 设备列表
     */
    List<DeviceListRespDTO> getDeviceList(Integer siteId);

}
