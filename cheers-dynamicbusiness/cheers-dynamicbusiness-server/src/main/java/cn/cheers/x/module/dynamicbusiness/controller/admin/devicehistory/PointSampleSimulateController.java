package cn.cheers.x.module.dynamicbusiness.controller.admin.devicehistory;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.devicehistory.vo.PointSampleSimulateReqVO;
import cn.cheers.x.module.dynamicbusiness.dataprocess.PointSampleSimulateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 验证入口：把读数交给正式判断。
 * 不管告警目录怎么写，也不下发风机。
 * 禁止在这里补阈值或编联动。
 */
@Tag(name = "管理后台 - 模拟点位读数")
@RestController
@RequestMapping("/dynamicbusiness/point-sample-simulate")
@Validated
public class PointSampleSimulateController {

    private final PointSampleSimulateService simulate;

    public PointSampleSimulateController(PointSampleSimulateService simulate) {
        this.simulate = simulate;
    }

    @PostMapping
    @Operation(summary = "按顺序上报一批采集读数，走正式判断入口")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<Integer> report(@Valid @RequestBody PointSampleSimulateReqVO request) {
        List<PointSampleSimulateService.Reading> readings = new ArrayList<>();
        for (PointSampleSimulateReqVO.Reading row : request.getReadings()) {
            readings.add(new PointSampleSimulateService.Reading(row.getDeviceId(), row.getPointCode(), row.getValue()));
        }
        return success(simulate.report(readings));
    }
}
