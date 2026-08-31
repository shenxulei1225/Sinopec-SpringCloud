package cn.cheers.x.inspection.task.service.execution;

import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;

/**
 * 设备上行回写任务会话上的设备运行态。
 * <p>不管：协议编解码、WebSocket 会话。
 */
public interface InspectionDeviceUplinkService {

    /**
     * 按逻辑设备标识匹配任务执行设备绑定，回写 deviceRunStatus。
     */
    void applyUplink(DeviceUplinkEventDTO event);
}
