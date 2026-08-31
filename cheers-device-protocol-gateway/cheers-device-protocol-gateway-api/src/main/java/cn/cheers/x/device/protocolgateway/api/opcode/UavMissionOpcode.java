package cn.cheers.x.device.protocolgateway.api.opcode;

/**
 * 无人机内层动作码（mission opcode）。
 * <p>权威：东方无人机对接协议 WebSocket v0.5.2。起飞为 200101，移动为 200102。
 */
public enum UavMissionOpcode {

    TAKEOFF(200101, "起飞"),
    MOVE(200102, "移动"),
    LAND(200103, "降落"),
    HEIGHT_REDUCTION(200104, "降低飞行高度"),
    HEIGHT_INCREASE(200105, "爬升飞行高度"),
    START_RECORDING(200201, "启动录像"),
    STOP_RECORDING(200202, "关闭录像"),
    PHOTO(200301, "拍照"),
    START_GAS_SENSOR(200401, "启动气体检测"),
    STOP_GAS_SENSOR(200402, "关闭气体检测"),
    TIMED_STATUS_REPORT(200501, "定时上报设备状态");

    private final int code;
    private final String description;

    UavMissionOpcode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() {
        return code;
    }

    public String description() {
        return description;
    }

    public static UavMissionOpcode fromCode(int code) {
        for (UavMissionOpcode value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知无人机内层动作码: " + code);
    }
}
