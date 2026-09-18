package cn.cheers.x.device.protocolgateway.datacollection;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 数据采集：先解析结构化上报，再按说明书核协议合格。
 * <p>负责：解析 + 核对后交出采集样本。
 * <p>不负责：回执、同步等待、巡检台账、猜通道。
 * <p>禁止：说明书缺失仍写成合格；把原文再丢给业务拆第二遍。
 */
@Service
@RequiredArgsConstructor
public class CollectionQualifyService {

    private final CollectionSampleAssembler assembler;
    private final ProtocolInstructionCatalog catalog;
    private final DeviceProtocolVersionCatalog deviceProtocolVersions;

    /**
     * 解析正文并核主包字段说明。进总线的样本只能是合格或不合格。
     * 设备已登记协议版本时按第一种消歧同一操作码。
     */
    public CollectionSample fromStructuredUplink(
            String channelCode,
            String deviceId,
            int opcode,
            String msgId,
            String payloadJson,
            long receivedAtEpochMs
    ) {
        CollectionSample parsed = assembler.fromStructuredUplink(
                channelCode, deviceId, opcode, msgId, payloadJson, receivedAtEpochMs);
        String version = deviceProtocolVersions.firstVersion(deviceId).orElse(null);
        return FieldDescriptionQualifier.qualify(parsed, catalog.findMainPacket(opcode, version));
    }
}
