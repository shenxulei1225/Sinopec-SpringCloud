package cn.cheers.x.device.protocolgateway.api.opcode;

/**
 * 任务状态内层状态码（外层 500203 报文里的 {@code status} 字段）。
 * <p>口径来自旧栈接收侧注释（启动成功 / 失败 / 终止 / 完成），不是外层 transport opcode。
 */
public final class DeviceTaskStatusCode {

    /** 任务启动成功 → 业务侧 RUNNING */
    public static final int START_SUCCESS = 300101;
    /** 任务启动失败 → FAULT */
    public static final int START_FAILURE = 300102;
    /** 任务终止成功 → COMPLETED（主动终止且地面站确认） */
    public static final int STOP_SUCCESS = 300103;
    /** 任务终止失败 → FAULT */
    public static final int STOP_FAILURE = 300104;
    /** 任务已经完成 → COMPLETED */
    public static final int TASK_COMPLETED = 300105;

    private DeviceTaskStatusCode() {
    }
}
