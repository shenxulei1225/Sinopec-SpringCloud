package cn.cheers.x.sinopec.device.controller.admin;

import cn.cheers.x.sinopec.device.api.admin.dto.DeviceListRespDTO;
import cn.cheers.x.sinopec.device.service.admin.DeviceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 设备后台管理控制器。
 */
@RestController
@RequestMapping("/sinopec/device/admin")
@RequiredArgsConstructor
public class DeviceAdminController {

    private final DeviceQueryService deviceQueryService;

    @GetMapping("/list")
    public List<DeviceListRespDTO> getDeviceList(@RequestParam("siteId") Integer siteId) {
        return deviceQueryService.getDeviceList(siteId);
    }

}
