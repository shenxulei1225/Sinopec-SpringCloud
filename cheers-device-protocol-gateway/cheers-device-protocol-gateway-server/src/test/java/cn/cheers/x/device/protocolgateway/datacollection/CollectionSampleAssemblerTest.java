package cn.cheers.x.device.protocolgateway.datacollection;

import cn.cheers.x.device.protocolgateway.api.channel.AccessChannelCodes;
import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.datacollection.ProtocolQualifyStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 结构化上报转发样本：带身份和 taskId；未核说明书不假装合格。
 */
class CollectionSampleAssemblerTest {

    private final CollectionSampleAssembler assembler = new CollectionSampleAssembler(new ObjectMapper());

    @Test
    void structuredJson_forwardsFields_andLeavesQualifyUnchecked() {
        CollectionSample sample = assembler.fromStructuredUplink(
                AccessChannelCodes.INSPECTION,
                "d1",
                500202,
                "u1",
                "{\"opcode\":500202,\"msgId\":\"u1\",\"taskId\":\"9001\",\"status\":300101}",
                10L);

        assertEquals(AccessChannelCodes.INSPECTION, sample.channelCode());
        assertEquals("d1", sample.deviceId());
        assertEquals("500202", sample.messageKind());
        assertEquals(ProtocolQualifyStatus.UNCHECKED, sample.protocolQualify());
        assertEquals(9001L, sample.executionRecordId());
        assertEquals(300101, ((Number) sample.fields().get("status")).intValue());
    }

    @Test
    void missingTaskId_leavesExecutionRecordIdEmpty() {
        CollectionSample sample = assembler.fromStructuredUplink(
                AccessChannelCodes.INSPECTION, "d1", 500202, "u1",
                "{\"opcode\":500202}", 1L);
        assertNull(sample.executionRecordId());
    }

    @Test
    void nonObject_isUnqualified() {
        CollectionSample sample = assembler.fromStructuredUplink(
                AccessChannelCodes.INSPECTION, "d1", 500202, "u1", "[]", 1L);
        assertEquals(ProtocolQualifyStatus.UNQUALIFIED, sample.protocolQualify());
        assertEquals(1, sample.qualifyErrors().size());
    }
}
