package cn.cheers.x.module.dynamicbusiness.controller.admin.devicehistory;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.controller.admin.devicehistory.vo.DeviceCollectionPointRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.devicehistory.vo.DevicePointSampleRespVO;
import cn.cheers.x.module.dynamicbusiness.devicehistory.DeviceCollectionPoint;
import cn.cheers.x.module.dynamicbusiness.devicehistory.DevicePointHistoryService;
import cn.cheers.x.module.dynamicbusiness.devicehistory.DevicePointSample;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 设备详情读取点的历史。
 * 不管采集写入，也不下发控制。
 */
@Tag(name = "管理后台 - 设备点历史")
@RestController
@RequestMapping("/dynamicbusiness/device-point-history")
@Validated
public class DevicePointHistoryController {

    private final DevicePointHistoryService history;

    public DevicePointHistoryController(DevicePointHistoryService history) {
        this.history = history;
    }

    @GetMapping("/points")
    @Operation(summary = "这台设备配了哪些点")
    @Parameter(name = "deviceId", description = "设备编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<List<DeviceCollectionPointRespVO>> points(@RequestParam("deviceId") Long deviceId) {
        long tenantId = TenantContextHolder.getRequiredTenantId();
        List<DeviceCollectionPointRespVO> rows = new ArrayList<>();
        for (DeviceCollectionPoint point : history.listPoints(tenantId, deviceId)) {
            rows.add(toPoint(point));
        }
        return success(rows);
    }

    @GetMapping("/latest")
    @Operation(summary = "每个点最新的一条读数")
    @Parameter(name = "deviceId", description = "设备编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<List<DevicePointSampleRespVO>> latest(@RequestParam("deviceId") Long deviceId) {
        long tenantId = TenantContextHolder.getRequiredTenantId();
        return success(toSamples(history.latest(tenantId, deviceId)));
    }

    @GetMapping
    @Operation(summary = "看一段时间内的读数")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<List<DevicePointSampleRespVO>> range(
            @RequestParam("deviceId") Long deviceId,
            @RequestParam(value = "pointCode", required = false) String pointCode,
            @RequestParam("from") Instant from,
            @RequestParam("to") Instant to) {
        long tenantId = TenantContextHolder.getRequiredTenantId();
        return success(toSamples(history.range(tenantId, deviceId, pointCode, from, to)));
    }

    private List<DevicePointSampleRespVO> toSamples(List<DevicePointSample> samples) {
        List<DevicePointSampleRespVO> rows = new ArrayList<>();
        for (DevicePointSample sample : samples) {
            DevicePointSampleRespVO row = new DevicePointSampleRespVO();
            row.setDeviceId(sample.getDeviceId());
            row.setPointCode(sample.getPointCode());
            row.setPointName(sample.getPointName());
            row.setUnit(sample.getUnit());
            row.setSampledAt(sample.getSampledAt());
            row.setEngineeringValue(sample.getEngineeringValue());
            row.setQuality(sample.getQuality());
            rows.add(row);
        }
        return rows;
    }

    private DeviceCollectionPointRespVO toPoint(DeviceCollectionPoint point) {
        DeviceCollectionPointRespVO row = new DeviceCollectionPointRespVO();
        row.setDeviceId(point.getDeviceId());
        row.setPointCode(point.getPointCode());
        row.setPointName(point.getPointName());
        row.setUnit(point.getUnit());
        return row;
    }
}
