package cn.cheers.x.device.protocolgateway.datacollection;

import cn.cheers.x.device.protocolgateway.api.channel.AccessChannelCodes;
import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.datacollection.ProtocolQualifyStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 解析后再核说明书；进总线不再停在未核。
 */
class CollectionQualifyServiceTest {

    private final CollectionQualifyService service = new CollectionQualifyService(
            new CollectionSampleAssembler(new ObjectMapper()),
            (opcode, version) -> CatalogLookup.found(List.of(
                    new FieldDescriptionRow("opcode", "integer", "指令编码", true),
                    new FieldDescriptionRow("msgId", "string", "消息编号", true)
            )),
            deviceId -> java.util.Optional.empty());

    @Test
    void completePacket_isQualified() {
        CollectionSample sample = service.fromStructuredUplink(
                AccessChannelCodes.INSPECTION, "d1", 500202, "u1",
                "{\"opcode\":500202,\"msgId\":\"u1\",\"taskId\":\"9\"}", 10L);
        assertEquals(ProtocolQualifyStatus.QUALIFIED, sample.protocolQualify());
        assertEquals(9L, sample.executionRecordId());
    }

    @Test
    void missingRequired_isUnqualified() {
        CollectionSample sample = service.fromStructuredUplink(
                AccessChannelCodes.INSPECTION, "d1", 500202, "",
                "{\"opcode\":500202}", 10L);
        assertEquals(ProtocolQualifyStatus.UNQUALIFIED, sample.protocolQualify());
        assertTrue(sample.qualifyErrors().get(0).contains("消息编号"));
    }
}
