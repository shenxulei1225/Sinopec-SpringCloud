package cn.cheers.x.device.protocolgateway.controller.admin;

import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorDeviceDTO;
import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorDownlinkRequest;
import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorDownlinkResultDTO;
import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorEventDTO;
import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorInstructionListDTO;
import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorSimulatorDTO;
import cn.cheers.x.device.protocolgateway.protocol.monitor.ProtocolMonitorDeviceListing;
import cn.cheers.x.device.protocolgateway.protocol.monitor.ProtocolMonitorDownlinkService;
import cn.cheers.x.device.protocolgateway.protocol.monitor.ProtocolMonitorHub;
import cn.cheers.x.device.protocolgateway.protocol.monitor.ProtocolMonitorSimulatorService;
import cn.cheers.x.inspection.task.api.dto.PatrolGeneratedTaskOptionDTO;
import cn.cheers.x.framework.common.pojo.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.error;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 协议实时监控页的接口。放在 admin 包里，页面地址才是 /admin-api/device-protocol/monitor。
 * <p>负责：列出设备、回放这一台的内存窗口、按设备推送、清掉这一台的副本、按已登记协议模拟下发、启停无人机和机器人模拟器进程。
 * <p>不负责：设备对接口。设备连上来走路径末段的设备编号，不经过这里。模拟器怎么应答报文。
 * <p>禁止：没选设备就订阅；为模拟下发再开一条连接；按页面传入的命令启动别的程序。
 */
@RestController
@RequestMapping("/device-protocol/monitor")
@RequiredArgsConstructor
public class DeviceProtocolMonitorController {

    private final ProtocolMonitorHub monitorHub;
    private final ProtocolMonitorDeviceListing deviceListing;
    private final ProtocolMonitorDownlinkService downlinkService;
    private final ProtocolMonitorSimulatorService simulatorService;

    /**
     * 台账已登记默认协议的设备，以及当前在线或窗口里刚出现过的设备。
     */
    @GetMapping("/devices")
    public CommonResult<List<ProtocolMonitorDeviceDTO>> devices() {
        return success(deviceListing.list());
    }

    /**
     * 该设备内存窗口里已有的副本。未选设备返回空。
     */
    @GetMapping("/recent")
    public CommonResult<List<ProtocolMonitorEventDTO>> recent(@RequestParam String deviceId) {
        return success(monitorHub.snapshotForView(deviceId));
    }

    /**
     * 选定设备后才推。先把窗口里已有的发一遍，再跟新副本。
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam String deviceId) {
        return monitorHub.subscribe(deviceId);
    }

    /**
     * 这台设备默认协议下的下发指令。没登记协议时列表为空，原因写在 note。
     */
    @GetMapping("/downlink-instructions")
    public CommonResult<ProtocolMonitorInstructionListDTO> downlinkInstructions(@RequestParam String deviceId) {
        return success(downlinkService.list(deviceId));
    }

    /**
     * 这台设备待执行和正在执行的已生成任务。监控页点一条，任务指令和推流地址用这一条上已经生成的内容。
     */
    @GetMapping("/tasks")
    public CommonResult<List<PatrolGeneratedTaskOptionDTO>> tasks(@RequestParam String deviceId) {
        return success(downlinkService.tasks(deviceId));
    }

    /**
     * 清掉当前这一台已复制的报文。不关设备连接，也不动其它设备的窗口。
     */
    @PostMapping("/clear")
    public CommonResult<Long> clear(@RequestParam String deviceId) {
        try {
            return success(monitorHub.clear(deviceId));
        } catch (IllegalArgumentException ex) {
            return error(400, ex.getMessage());
        }
    }

    /**
     * 按空包写出一条下发。交出去就返回，不等设备答卷。设备不在线则失败。
     */
    @PostMapping("/downlink")
    public CommonResult<ProtocolMonitorDownlinkResultDTO> downlink(@RequestBody ProtocolMonitorDownlinkRequest request) {
        try {
            return success(downlinkService.send(
                    request.deviceId(),
                    request.opcode(),
                    request.enable(),
                    request.frequency(),
                    request.total(),
                    request.streamUrl(),
                    request.executionRecordId(),
                    request.templateId(),
                    request.taskId()));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return error(400, ex.getMessage());
        }
    }

    /**
     * 无人机、机器人两个模拟器进程现在开没开、连上没有。
     */
    @GetMapping("/simulators")
    public CommonResult<List<ProtocolMonitorSimulatorDTO>> simulators() {
        return success(simulatorService.list());
    }

    /**
     * 启动其中一个模拟器。真实设备已经占着同一条连接时拒绝。
     */
    @PostMapping("/simulators/start")
    public CommonResult<ProtocolMonitorSimulatorDTO> startSimulator(@RequestParam String role) {
        try {
            return success(simulatorService.start(role));
        } catch (IllegalArgumentException ex) {
            return error(400, ex.getMessage());
        }
    }

    /**
     * 停掉其中一个模拟器进程。没在运行就直接说明。
     */
    @PostMapping("/simulators/stop")
    public CommonResult<ProtocolMonitorSimulatorDTO> stopSimulator(@RequestParam String role) {
        try {
            return success(simulatorService.stop(role));
        } catch (IllegalArgumentException ex) {
            return error(400, ex.getMessage());
        }
    }
}
