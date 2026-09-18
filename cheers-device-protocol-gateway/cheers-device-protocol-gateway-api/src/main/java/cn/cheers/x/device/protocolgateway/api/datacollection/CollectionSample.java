package cn.cheers.x.device.protocolgateway.api.datacollection;

import cn.cheers.x.device.protocolgateway.api.identity.UplinkIdentity;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 采集样本：网关解析一遍后交给上层的唯一主契约。
 * <p>负责：身份、协议合格、核过（或结构化转发）的字段、收到时间、报文里已有的执行记录 id。
 * <p>不负责：检查项完成、业务合格、持有任务执行记录台账。
 * <p>禁止：执行记录 id 空了用设备编号猜；把原始报文再丢给业务拆第二遍。
 *
 * @param channelCode        接入通道
 * @param deviceId           设备编号
 * @param messageKind        报文种类
 * @param protocolQualify    协议合格状态
 * @param qualifyErrors      不合格原因；未核或合格时为空表
 * @param fields             结构化转发的字段（巡检接近原文对象）
 * @param executionRecordId  报文里已有的执行记录 id；没有则为空
 * @param msgId              消息 id
 * @param receivedAtEpochMs  收到时间
 */
public record CollectionSample(
        String channelCode,
        String deviceId,
        String messageKind,
        ProtocolQualifyStatus protocolQualify,
        List<String> qualifyErrors,
        Map<String, Object> fields,
        Long executionRecordId,
        String msgId,
        long receivedAtEpochMs
) {

    public CollectionSample {
        if (channelCode == null || channelCode.isBlank()) {
            throw new IllegalArgumentException("采集样本缺少接入通道，禁止默认成巡检");
        }
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("采集样本缺少设备编号");
        }
        if (messageKind == null || messageKind.isBlank()) {
            throw new IllegalArgumentException("采集样本缺少报文种类");
        }
        if (protocolQualify == null) {
            throw new IllegalArgumentException("采集样本缺少协议合格状态");
        }
        qualifyErrors = qualifyErrors == null ? List.of() : List.copyOf(qualifyErrors);
        fields = fields == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(fields));
        if (msgId == null) {
            msgId = "";
        }
    }

    public UplinkIdentity identity() {
        return new UplinkIdentity(channelCode, deviceId, messageKind);
    }

    /** 不合格不得按好数据交给业务方法。未核过也不算合格。 */
    public boolean isProtocolQualified() {
        return protocolQualify == ProtocolQualifyStatus.QUALIFIED;
    }
}
