package cn.cheers.x.device.protocolgateway.djicloud;

import java.util.List;

/**
 * 上云入站处理结果。三种结果都要让调用方看见，不能吞掉。
 */
public sealed interface DjiCloudDecision {

    /**
     * 要发回设备的一包。topology 为真时，网关序列号视为已上线。
     */
    record Reply(String topic, String json, String gatewaySn, List<String> subDeviceSns, boolean topology)
            implements DjiCloudDecision {
    }

    /**
     * 设备在要许可，但 App ID / Key / License 没配齐。不回假许可。
     */
    record MissingLicense(String gatewaySn) implements DjiCloudDecision {
    }

    /**
     * 设备在绑定组织，但组织编号或名称没配。不回一个编出来的组织。
     */
    record MissingOrganization(String gatewaySn) implements DjiCloudDecision {
    }

    /**
     * 本阶段不处理。不回成功。
     */
    record Ignore(String reason) implements DjiCloudDecision {
    }
}
