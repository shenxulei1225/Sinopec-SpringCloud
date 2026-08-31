package cn.cheers.x.device.protocolgateway.api.opcode;

/**
 * 外层操作码（transport opcode）：平台 ↔ 地面站报文顶层 opcode（多为 500xxx）。
 * <p>机器人与无人机共用；与通讯层无关，由协议对接层信封使用。
 */
public enum TransportOpcode {

    REAL_TIME_STATUS(500101, "获取设备当前实时状态"),
    SWITCH_TIMED_REPORT(500102, "启动/关闭定时上报"),
    TIMED_DEVICE_STATUS(500103, "定时上报设备状态"),
    COMMAND_SEND(500104, "巡检任务指令包下发"),
    HEARTBEAT(500105, "心跳"),
    ACK(500106, "应答"),
    STARTUP_EXECUTE(500201, "启动执行已下发任务"),
    COMMAND_RESULT(500202, "单条指令操作结果"),
    TASK_STATUS(500203, "任务状态"),
    FAULT_REPORT(500204, "设备故障"),
    TASK_STOP(500205, "任务终止"),
    GET_COMMAND_PACKAGE(500301, "获取地面站指令包"),
    DELETE_COMMAND_PACKAGE(500302, "删除地面站指令包"),
    LIST_TEMPLATE_IDS(500303, "列出任务模板 id"),
    LINK_STATE_EVENT(500403, "在线/掉线等事件通知");

    private final int code;
    private final String description;

    TransportOpcode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() {
        return code;
    }

    public String description() {
        return description;
    }

    public static TransportOpcode fromCode(int code) {
        for (TransportOpcode value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知外层操作码: " + code);
    }
}
