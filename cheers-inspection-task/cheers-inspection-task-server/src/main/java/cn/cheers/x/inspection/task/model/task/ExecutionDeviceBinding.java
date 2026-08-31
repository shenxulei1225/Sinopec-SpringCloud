package cn.cheers.x.inspection.task.model.task;

import lombok.Data;

/**
 * 任务会话上的执行设备绑定。
 * <p>用户绑设备时写入，详情展示「具体执行设备」；开跑读此结构调网关。
 * <p>不管：瞬时 WebSocket 连接、设备台账整行、排期资源池策略。
 * <p>禁止：开跑时再挖台账补 protocolCode / logicalDeviceId。
 */
@Data
public class ExecutionDeviceBinding {

    /**
     * 设备业务 id（台账主键；展示用，名称由查询侧增强）。
     */
    private Long equipmentId;

    /**
     * 对接协议编码，如 zhiren-robot-ws。
     */
    private String protocolCode;

    /**
     * 逻辑设备标识（报文与网关注册表键；不是瞬时连接句柄）。
     */
    private String logicalDeviceId;
}
