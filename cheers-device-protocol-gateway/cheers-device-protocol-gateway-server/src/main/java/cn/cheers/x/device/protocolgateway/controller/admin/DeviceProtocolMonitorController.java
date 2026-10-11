package cn.cheers.x.device.protocolgateway.controller.admin;

import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorDeviceDTO;
import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorDownlinkRequest;
import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorDownlinkResultDTO;
import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorEventDTO;
import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorInstructionListDTO;
import cn.cheers.x.device.protocolgateway.protocol.monitor.ProtocolMonitorDeviceListing;
import cn.cheers.x.device.protocolgateway.protocol.monitor.ProtocolMonitorDownlinkService;
import cn.cheers.x.device.protocolgateway.protocol.monitor.ProtocolMonitorHub;
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
 * <p>负责：列出设备、回放这一台的内存窗口、按设备推送、清掉这一台的副本、按已登记协议模拟下发。
 * <p>不负责：设备对接口。设备连上来走路径末段的设备编号，不经过这里。
 * <p>禁止：没选设备就订阅；为模拟下发再开一条连接。
 */
@RestController
@RequestMapping("/device-protocol/monitor")
@RequiredArgsConstructor
public class DeviceProtocolMonitorController {

    private final ProtocolMonitorHub monitorHub;
    private final ProtocolMonitorDeviceListing deviceListing;
    private final ProtocolMonitorDownlinkService downlinkService;

    /**
     * 当前在线，或窗口里刚出现过的设备。
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
     * 按空包写出一条下发。同步指令等到同号答卷。设备不在线则失败。
     */
    @PostMapping("/downlink")
    public CommonResult<ProtocolMonitorDownlinkResultDTO> downlink(@RequestBody ProtocolMonitorDownlinkRequest request) {
        try {
            return success(downlinkService.send(request.deviceId(), request.opcode()));
        } catch (IllegalArgumentException ex) {
            return error(400, ex.getMessage());
        }
    }
}
