package cn.cheers.x.device.protocolgateway.api.protocol;

/**
 * 第一期已注册的对接协议编码（protocolCode）。
 * <p>这是「用哪套说明书组包」的注册键，不是用户台账里的设备业务分类。
 * 以后加厂商 / 新协议 = 新增常量并实现适配器，禁止用 ROBOT/UAV 枚举代替。
 */
public final class ProtocolCodes {

    /** 知仁机器人 WebSocket 对接协议 */
    public static final String ZHIREN_ROBOT_WS = "zhiren-robot-ws";

    /** 东方无人机 WebSocket 对接协议 */
    public static final String DONGFANG_UAV_WS = "dongfang-uav-ws";

    private ProtocolCodes() {
    }
}
