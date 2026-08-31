package cn.cheers.x.device.protocolgateway.api.opcode;

/**
 * 机器人内层动作码（mission opcode）。
 * <p>权威：知仁机器人协议 v0.5.8 + 联调样例。移动必须为 {@link #MOVE}(200102)，禁止使用 200101。
 */
public enum RobotMissionOpcode {

    /** 移动到指定点位（联调与协议均为 200102） */
    MOVE(200102, "移动"),
    PHOTO(200301, "拍照"),
    TIMED_STATUS_REPORT(200501, "定时上报设备状态"),
    /** 气体检测数据上报（协议语义为上报，不是开/关气体） */
    GAS_DETECT_REPORT(200403, "气体检测数据上报");

    private final int code;
    private final String description;

    RobotMissionOpcode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() {
        return code;
    }

    public String description() {
        return description;
    }

    public static RobotMissionOpcode fromCode(int code) {
        for (RobotMissionOpcode value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知机器人内层动作码: " + code);
    }
}
