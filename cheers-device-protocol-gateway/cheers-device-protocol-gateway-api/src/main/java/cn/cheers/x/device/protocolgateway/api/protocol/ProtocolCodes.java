package cn.cheers.x.device.protocolgateway.api.protocol;

/**
 * 对接协议版本。与协议指令、对照表上的版本码一致。
 * <p>不是厂商名，也没有「知仁组包 / 东方组包」。
 */
public final class ProtocolCodes {

    public static final String ROBOT_WS = "robot-ws";
    public static final String UAV_WS = "uav-ws";

    /**
     * 大疆机场上云，发布号跟上云 API v1.16.1。
     * 机场和 Pilot 仍是两本指令；发布号用来区分以后的另一版，不把两本收成一个码。
     */
    public static final String DJI_DOCK = "dji-dock-1.16.1";

    /** 大疆 Pilot 上云，发布号与机场相同，指令清单不同。 */
    public static final String DJI_PILOT = "dji-pilot-1.16.1";

    private ProtocolCodes() {
    }

    /** 机场与 Pilot 都走上云通道，不走地面站操作码。 */
    public static boolean isDjiCloud(String protocolVersion) {
        return DJI_DOCK.equals(protocolVersion) || DJI_PILOT.equals(protocolVersion);
    }

    public static boolean isKnown(String protocolVersion) {
        return ROBOT_WS.equals(protocolVersion) || UAV_WS.equals(protocolVersion) || isDjiCloud(protocolVersion);
    }

    public static String label(String protocolVersion) {
        if (ROBOT_WS.equals(protocolVersion)) {
            return "机器人对接协议";
        }
        if (UAV_WS.equals(protocolVersion)) {
            return "无人机对接协议";
        }
        if (DJI_DOCK.equals(protocolVersion)) {
            return "大疆机场";
        }
        if (DJI_PILOT.equals(protocolVersion)) {
            return "大疆 Pilot";
        }
        return protocolVersion == null ? "" : protocolVersion;
    }
}
