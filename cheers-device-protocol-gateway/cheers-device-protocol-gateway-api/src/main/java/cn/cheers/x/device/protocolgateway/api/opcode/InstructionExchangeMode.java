package cn.cheers.x.device.protocolgateway.api.opcode;

/**
 * 指令交互方式：只给通信层分流，不表示「几秒后返回」。
 * <p>权威应写在协议指令上。本枚举不负责填包、不读巡检库。
 */
public enum InstructionExchangeMode {

    /** 同步要结果：按消息 id 等同一操作码的业务答卷，不进上报总线。 */
    SYNC_WAIT_RESULT,

    /** 异步出结果：不解开这次下发等待，正文进总线。 */
    ASYNC_RESULT,

    /** 主动上报：没有对应下发可等，正文进总线。 */
    ACTIVE_UPLINK,

    /** 心跳：原样加 time 回，不回回执。 */
    HEARTBEAT,

    /** 回执：只记收到，不当业务成功，不结束同步等待。 */
    ACK
}
