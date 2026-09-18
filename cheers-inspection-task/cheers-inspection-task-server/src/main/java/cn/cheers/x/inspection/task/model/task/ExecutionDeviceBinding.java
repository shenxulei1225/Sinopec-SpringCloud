package cn.cheers.x.inspection.task.model.task;

import lombok.Data;

/**
 * 任务会话上的执行设备绑定。
 * <p>选设备后带出设备默认对接协议和逻辑标识；本次任务可临时改协议。
 * <p>开跑只读此结构里的 protocolCode，不回设备台账改默认值。
 * <p>不管瞬时连接、排期资源池。
 * <p>禁止：开跑时再挖台账补 protocolCode / logicalDeviceId。
 */
@Data
public class ExecutionDeviceBinding {

    /**
     * 设备业务 id（台账主键；展示用，名称由查询侧增强）。
     */
    private Long equipmentId;

    /**
     * 本次任务使用的平台对接协议（robot-ws / uav-ws）。
     * 选设备时先写入设备默认协议；用户可临时改。不是软件版本，也不是厂商名。
     */
    private String protocolCode;

    /**
     * 逻辑设备标识（报文与网关注册表键；不是瞬时连接句柄）。
     */
    private String logicalDeviceId;
}
