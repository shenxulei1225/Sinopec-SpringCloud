package cn.cheers.x.device.protocolgateway.datacollection;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.opcode.InstructionExchangeCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 数据采集：先解析结构化上报，再按说明书核协议合格，再按转发名收成标准事件。
 * <p>负责：解析 + 核对 + 转发后交出采集样本。
 * <p>不负责：回执、同步等待、巡检台账、猜通道。
 * <p>禁止：说明书缺失仍写成合格；把原文再丢给业务拆第二遍；没有转发名时用设备路径填标准事件。
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
        CatalogLookup lookup = lookupFor(opcode, version, parsed.fields());
        CollectionSample qualified = FieldDescriptionQualifier.qualify(parsed, lookup);
        return StandardEventForwarder.apply(qualified, lookup);
    }

    /**
     * 同步答卷对响应卷。主动上报对下发卷。响应卷读不到时仍对下发卷，缺口留在不合格原因里。
     */
    private CatalogLookup lookupFor(int opcode, String version, Map<String, Object> fields) {
        if (InstructionExchangeCatalog.isSyncWaitResult(opcode)) {
            String volume = responseCode(fields) == 200
                    ? ProtocolInstructionCatalog.VOLUME_INBOUND_SUCCESS
                    : ProtocolInstructionCatalog.VOLUME_INBOUND_FAILURE;
            CatalogLookup response = catalog.findVolume(opcode, version, volume);
            if (response.ok()) {
                return response;
            }
        }
        return catalog.findMainPacket(opcode, version);
    }

    private static int responseCode(Map<String, Object> fields) {
        if (fields == null) {
            return -1;
        }
        Object raw = fields.get("code");
        if (raw instanceof Number number) {
            return number.intValue();
        }
        if (raw instanceof String text && !text.isBlank()) {
            try {
                return Integer.parseInt(text.trim());
            } catch (NumberFormatException ex) {
                return -1;
            }
        }
        return -1;
    }
}
