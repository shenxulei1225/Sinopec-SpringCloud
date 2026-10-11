package cn.cheers.x.device.protocolgateway.api.mission;

import java.util.List;

/**
 * 任务调用协议网关做试译/正式翻译时的入参。
 * <p>任务只提供<strong>动作库参数</strong>（{@link DispatchAction#params()}：用户配置 + 步骤生成时业务写入）。
 * 任务不知道协议指令空包要哪些槽；网关按 {@code protocol_mapping} 从动作参数、执行设备挂载、媒体等来源填包。
 * 见 {@code docs/动态业务/巡检/巡检模型与任务开跑规则.md} §8.2.1。
 * <p>{@code executorEquipmentId}：用户选中的执行设备 id，供对照来源「执行设备挂载」。
 * {@code mediaStreamUrl}：任务侧在<strong>试译与正式翻译</strong>前按「任务 id + 计划点 id」分配（东古/机器人收流），供对照 {@code stream_url}；开跑不再生成。
 * {@code executionMeans} 仅任务内读动作库用，<strong>调用网关时不传</strong>（兼容构造可 null）。
 * <p>禁止：用厂商名冒充协议版本；动作列表空了还组包。
 *
 * @param protocolVersion      对接协议版本（如 {@code robot-ws} / {@code uav-ws}）
 * @param deviceId             逻辑设备标识
 * @param taskId               翻译/航线存储键（试译与物化可不同后缀）
 * @param templateId           与航线模板存储一致，现网常与 taskId 相同
 * @param actions              步骤图组包；params 仅动作库范围内字段
 * @param executorEquipmentId  执行设备台账 id；网关读挂载类对照，非动作参数
 * @param executionMeans       不传网关；保留字段仅为兼容，应 null
 * @param mediaStreamUrl       需要平台收流时必填；大疆机内录等为 null
 */
public record DeviceMissionPlan(
        String protocolVersion,
        String deviceId,
        String taskId,
        String templateId,
        List<DispatchAction> actions,
        Long executorEquipmentId,
        String executionMeans,
        String mediaStreamUrl
) {
    /** 兼容旧调用：无执行设备与媒体上下文时，翻译只消费步骤参数袋。 */
    public DeviceMissionPlan(
            String protocolVersion,
            String deviceId,
            String taskId,
            String templateId,
            List<DispatchAction> actions
    ) {
        this(protocolVersion, deviceId, taskId, templateId, actions, null, null, null);
    }
}
