package cn.cheers.x.module.dynamicbusiness.controller.admin.alarmlinkageverify;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.alarmlinkageverify.vo.AlarmLinkageVerifyDeviceRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.alarmlinkageverify.vo.AlarmLinkageVerifySnapshotRespVO;
import cn.cheers.x.module.dynamicbusiness.service.alarmlinkageverify.AlarmLinkageVerifyQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 验证页只读入口。
 * 设备清单按名称列出带采集点的设备。上报读数仍走模拟点位接口。这里把已经发出的事件、告警记录和联动执行给人看。
 * 禁止在这里判断告警或调用联动入口，禁止手填设备编号当主入口。
 */
@Tag(name = "管理后台 - 告警联动验证")
@RestController
@RequestMapping("/dynamicbusiness/alarm-linkage-verify")
@Validated
public class AlarmLinkageVerifyController {

    private final AlarmLinkageVerifyQueryService queryService;

    public AlarmLinkageVerifyController(AlarmLinkageVerifyQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/devices")
    @Operation(summary = "列出带采集点的设备，给人按名称选择")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<List<AlarmLinkageVerifyDeviceRespVO>> devices() {
        return success(queryService.devices());
    }

    @GetMapping("/snapshot")
    @Operation(summary = "看这台设备最近的采集点、告警记录和联动执行")
    @Parameter(name = "deviceId", description = "设备编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<AlarmLinkageVerifySnapshotRespVO> snapshot(@RequestParam("deviceId") Long deviceId) {
        return success(queryService.snapshot(deviceId == null ? 0L : deviceId));
    }
}
