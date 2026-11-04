package cn.cheers.x.device.protocolgateway.api.protocol;

/**
 * 工业设备的会话协议。
 * 负责：OPC UA 和 MQTT 这两个会话码。动作先对上点位，再由当前会话把点送出。
 * 不负责：数据协议目录里的指令说明书。这两条不占型号，也不填巡检指令空包。
 */
public final class IndustrialSessionProtocols {

    /** 当前会话。送出的是浏览得到的节点标识和开关值。 */
    public static final String OPC_UA = "industrial-opcua";

    /** 以后换会话时用。点位对照不改。主题还没有时不能发。 */
    public static final String MQTT = "industrial-mqtt";

    private IndustrialSessionProtocols() {
    }

    public static boolean isIndustrial(String protocolVersion) {
        return OPC_UA.equals(protocolVersion) || MQTT.equals(protocolVersion);
    }

    public static String label(String protocolVersion) {
        if (OPC_UA.equals(protocolVersion)) {
            return "OPC UA";
        }
        if (MQTT.equals(protocolVersion)) {
            return "MQTT";
        }
        return protocolVersion == null ? "" : protocolVersion;
    }
}
