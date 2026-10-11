package cn.cheers.x.device.protocolgateway.api.dto;

import java.util.List;

/**
 * 协议实时监控事件：正式对接口收发时复制出的一条记录。
 * <p>窗口里先留原文和识别。推到页面时再带上按字段说明拆开的行。不写历史表，不进上报总线。
 *
 * @param eventId            本条副本编号，前端用来去重
 * @param deviceId           逻辑设备标识
 * @param direction          inbound=设备发来 / outbound=平台写出 / session=连上或断开
 * @param opcode             外层操作码；解析失败或会话事件为空
 * @param msgId              消息 id；缺省空串
 * @param identity           给人看的识别：心跳、回执、指令名称、设备已连接
 * @param exchangeMode       交互方式编码；会话事件为空
 * @param payloadJson        原文；会话事件可空
 * @param occurredAtEpochMs  复制时间
 * @param parsedFields       按字段说明拆开的业务字段；窗口内副本为空，页面快照才填
 * @param parsedNote         对不上字段说明时的原因；对上了则为空
 */
public record ProtocolMonitorEventDTO(
        String eventId,
        String deviceId,
        String direction,
        Integer opcode,
        String msgId,
        String identity,
        String exchangeMode,
        String payloadJson,
        long occurredAtEpochMs,
        List<ProtocolMonitorFieldDTO> parsedFields,
        String parsedNote
) {

    public ProtocolMonitorEventDTO {
        parsedFields = parsedFields == null ? List.of() : List.copyOf(parsedFields);
    }

    /**
     * 换上页面要看的字段行。原文和识别不动。
     */
    public ProtocolMonitorEventDTO withParsed(List<ProtocolMonitorFieldDTO> fields, String note) {
        return new ProtocolMonitorEventDTO(
                eventId,
                deviceId,
                direction,
                opcode,
                msgId,
                identity,
                exchangeMode,
                payloadJson,
                occurredAtEpochMs,
                fields,
                note
        );
    }
}
