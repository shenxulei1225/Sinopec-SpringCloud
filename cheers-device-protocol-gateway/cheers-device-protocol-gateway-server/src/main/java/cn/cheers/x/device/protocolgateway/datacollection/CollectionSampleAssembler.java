package cn.cheers.x.device.protocolgateway.datacollection;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.datacollection.ProtocolQualifyStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 把结构化上报转成采集样本：打身份、转发字段、有则带执行记录 id。
 * <p>负责：巡检等 JSON 应用报文的核对前转发。定时状态和步骤结果先认 {@code packages} 与 {@code result} 两个外层名。
 * <p>不负责：按字段说明做协议合格核对（交给 {@link CollectionQualifyService}）；PLC 点表换算；改模拟器。
 * <p>禁止：执行记录 id 空了用设备编号猜；在本类写成合格；两个外层字段都有还互相覆盖。往来原文归入正在执行的任务不在本类。
 */
@Component
@RequiredArgsConstructor
public class CollectionSampleAssembler {

    private final ObjectMapper objectMapper;

    /**
     * 结构化上报先解析转发。正文必须是对象；否则不合格。
     * 定时状态、步骤结果若只带了另一个外层名，先补到下游正在读的字段，原文里的那个键还留着。
     * 解析成功时状态仍是未核，由 {@link CollectionQualifyService} 再按说明书判定。
     */
    public CollectionSample fromStructuredUplink(
            String channelCode,
            String deviceId,
            int opcode,
            String msgId,
            String payloadJson,
            long receivedAtEpochMs
    ) {
        String messageKind = String.valueOf(opcode);
        JsonNode root;
        try {
            root = objectMapper.readTree(payloadJson);
        } catch (Exception ex) {
            return unqualified(
                    channelCode, deviceId, messageKind, msgId, receivedAtEpochMs,
                    List.of("上报正文无法解析为 JSON"));
        }
        if (root == null || !root.isObject()) {
            return unqualified(
                    channelCode, deviceId, messageKind, msgId, receivedAtEpochMs,
                    List.of("上报正文不是对象"));
        }
        Map<String, Object> fields = objectMapper.convertValue(root, new TypeReference<>() {
        });
        if (fields == null) {
            fields = new LinkedHashMap<>();
        }
        fields = UplinkBodyKeyAlias.acceptEitherCarrier(opcode, fields);
        return new CollectionSample(
                channelCode,
                deviceId,
                messageKind,
                ProtocolQualifyStatus.UNCHECKED,
                List.of(),
                fields,
                readExecutionRecordId(fields.get("taskId")),
                msgId,
                receivedAtEpochMs
        );
    }

    private CollectionSample unqualified(
            String channelCode,
            String deviceId,
            String messageKind,
            String msgId,
            long receivedAtEpochMs,
            List<String> errors
    ) {
        return new CollectionSample(
                channelCode,
                deviceId,
                messageKind,
                ProtocolQualifyStatus.UNQUALIFIED,
                new ArrayList<>(errors),
                Map.of(),
                null,
                msgId,
                receivedAtEpochMs
        );
    }

    /**
     * 只认报文里的 taskId。解不出数字就空着，不猜。
     */
    static Long readExecutionRecordId(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number number) {
            return number.longValue();
        }
        String text = String.valueOf(raw).trim();
        if (text.isEmpty() || "null".equals(text)) {
            return null;
        }
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
