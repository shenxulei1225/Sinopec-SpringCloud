package cn.cheers.x.device.protocolgateway.protocol.processor;

import cn.cheers.x.device.protocolgateway.api.channel.AccessChannelCodes;
import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.device.protocolgateway.api.identity.UplinkIdentity;
import cn.cheers.x.device.protocolgateway.datacollection.DeviceProtocolVersionCatalog;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.api.InspectionDeviceUplinkApi;
import lombok.extern.slf4j.Slf4j;

/**
 * 某一本协议的上报处理入口。
 * <p>负责：只接收协议版本对得上的巡检上报，并交给巡检写入执行记录。
 * <p>不负责：判断操作码在不在协议书里。那一步在分发前已经做完。
 * <p>禁止：机器人入口接收无人机协议的报文。
 */
@Slf4j
public final class DeviceTypeUplinkProcessor implements UplinkProcessor {

    private final String protocolVersion;
    private final DeviceProtocolVersionCatalog deviceProtocolVersions;
    private final InspectionDeviceUplinkApi inspectionDeviceUplinkApi;

    public DeviceTypeUplinkProcessor(
            String protocolVersion,
            DeviceProtocolVersionCatalog deviceProtocolVersions,
            InspectionDeviceUplinkApi inspectionDeviceUplinkApi
    ) {
        this.protocolVersion = protocolVersion;
        this.deviceProtocolVersions = deviceProtocolVersions;
        this.inspectionDeviceUplinkApi = inspectionDeviceUplinkApi;
    }

    @Override
    public boolean supports(UplinkIdentity identity) {
        if (!AccessChannelCodes.INSPECTION.equals(identity.channelCode())) {
            return false;
        }
        return deviceProtocolVersions.firstVersion(identity.deviceId())
                .filter(protocolVersion::equals)
                .isPresent();
    }

    @Override
    public void process(UplinkIdentity identity, DeviceUplinkEventDTO event) {
        CommonResult<Boolean> result = inspectionDeviceUplinkApi.notify(event.sample());
        if (result == null || !result.isSuccess()) {
            throw new IllegalStateException("巡检上报写入执行记录失败 channel="
                    + identity.channelCode()
                    + " deviceId=" + identity.deviceId()
                    + " protocolVersion=" + protocolVersion
                    + " messageKind=" + identity.messageKind()
                    + " msg=" + (result == null ? "无响应" : result.getMsg()));
        }
        log.info("[device-protocol] 上报已交给 {} 的处理 protocolVersion={} deviceId={} messageKind={}",
                protocolVersion, protocolVersion, identity.deviceId(), identity.messageKind());
    }
}
