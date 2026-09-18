package cn.cheers.x.device.protocolgateway.controller;

import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorDeviceDTO;
import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorEventDTO;
import cn.cheers.x.device.protocolgateway.protocol.monitor.ProtocolMonitorHub;
import cn.cheers.x.framework.common.pojo.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 协议实时监控入口：列出可选设备、回放内存窗口、按设备推 SSE。
 * <p>不负责设备对接口。未选设备不得订阅。
 */
@RestController
@RequestMapping("/device-protocol/monitor")
@RequiredArgsConstructor
public class DeviceProtocolMonitorController {

    private final ProtocolMonitorHub monitorHub;

    /**
     * 当前在线，或窗口里刚出现过的设备。
     */
    @GetMapping("/devices")
    public CommonResult<List<ProtocolMonitorDeviceDTO>> devices() {
        return success(monitorHub.listDevices());
    }

    /**
     * 该设备内存窗口里已有的副本。未选设备返回空。
     */
    @GetMapping("/recent")
    public CommonResult<List<ProtocolMonitorEventDTO>> recent(@RequestParam String deviceId) {
        return success(monitorHub.snapshot(deviceId));
    }

    /**
     * 选定设备后才推。先把窗口里已有的发一遍，再跟新副本。
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam String deviceId) {
        return monitorHub.subscribe(deviceId);
    }
}
