package cn.cheers.x.inspection.task.controller.admin.execution;

import cn.cheers.x.device.protocolgateway.api.dto.AircraftFlightStatusDTO;
import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.controller.admin.execution.vo.LiveCameraCommandReqVO;
import cn.cheers.x.inspection.task.controller.admin.execution.vo.LiveFlightCommandReqVO;
import cn.cheers.x.inspection.task.service.execution.live.PatrolLiveAircraftStatusService;
import cn.cheers.x.inspection.task.service.execution.live.PatrolLiveCameraCommandService;
import cn.cheers.x.inspection.task.service.execution.live.PatrolLiveFlightCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 巡检图层执行中的画面：把这一下负载操作、一键起飞或一键返航发出去，并读取飞机最新状态。
 *
 * <p>不负责改执行记录状态，不负责在这里配协议对照，不负责编电量、目标点或高度。
 */
@Tag(name = "管理后台 - 巡检实时画面")
@RestController
@RequestMapping("/inspection-task/execution-record")
@Validated
@RequiredArgsConstructor
public class PatrolLiveCameraController {

    private final PatrolLiveCameraCommandService liveCameraCommandService;
    private final PatrolLiveFlightCommandService liveFlightCommandService;
    private final PatrolLiveAircraftStatusService aircraftStatusService;

    @PostMapping("/{id}/live-camera")
    @Operation(summary = "把画面上的这一下负载操作按已保存对照发出")
    public CommonResult<MissionStartRespDTO> send(
            @PathVariable("id") Long id,
            @Valid @RequestBody LiveCameraCommandReqVO request) {
        return success(liveCameraCommandService.send(id, request.getActionCode(), request.getParams()));
    }

    @PostMapping("/{id}/live-flight")
    @Operation(summary = "把画面上的一键起飞或一键返航按已保存对照发出")
    public CommonResult<MissionStartRespDTO> sendFlight(
            @PathVariable("id") Long id,
            @Valid @RequestBody LiveFlightCommandReqVO request) {
        return success(liveFlightCommandService.send(id, request.getCommand()));
    }

    @GetMapping("/{id}/live-aircraft-status")
    @Operation(summary = "读取这次巡检所记网关下那一架飞机的最新状态")
    public CommonResult<AircraftFlightStatusDTO> aircraftStatus(@PathVariable("id") Long id) {
        return success(aircraftStatusService.load(id));
    }
}
