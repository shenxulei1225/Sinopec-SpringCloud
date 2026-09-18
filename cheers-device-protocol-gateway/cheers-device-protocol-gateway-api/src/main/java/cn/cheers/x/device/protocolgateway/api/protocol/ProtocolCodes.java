package cn.cheers.x.device.protocolgateway.api.protocol;

/**
 * 对接协议版本。与协议指令、对照表上的版本码一致。
 * <p>不是厂商名，也没有「知仁组包 / 东方组包」。
 */
public final class ProtocolCodes {

    public static final String ROBOT_WS = "robot-ws";
    public static final String UAV_WS = "uav-ws";

    private ProtocolCodes() {
    }

    public static boolean isKnown(String protocolVersion) {
        return ROBOT_WS.equals(protocolVersion) || UAV_WS.equals(protocolVersion);
    }
}
