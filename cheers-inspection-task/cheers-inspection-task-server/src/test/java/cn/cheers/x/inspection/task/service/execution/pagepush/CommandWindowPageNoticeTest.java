package cn.cheers.x.inspection.task.service.execution.pagepush;

import cn.cheers.x.inspection.task.api.dto.TaskExchangeTranscriptReqDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class CommandWindowPageNoticeTest {

    private InspectionExecutionPagePublisher publisher;
    private CommandWindowPageNotice notice;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        publisher = mock(InspectionExecutionPagePublisher.class);
        notice = new CommandWindowPageNotice(publisher);
    }

    @Test
    void issuedCommandUsesTheStoredTopic() throws Exception {
        TaskExchangeTranscriptReqDTO req = packet(TaskExchangeTranscriptReqDTO.OUTBOUND,
                "{\"opcode\":500104,\"msgId\":\"e071\"}");
        req.setEventTopicCode("inspection_command_accepted");

        notice.publish(req, 924024L, objectMapper.readTree(req.getPayloadJson()));

        verify(publisher).publishProcessNotice(
                "inspection_command_accepted",
                924024L,
                "dev-1",
                "巡检任务指令包下发",
                "下发中",
                null,
                "e071",
                TaskExchangeTranscriptReqDTO.OUTBOUND,
                1L,
                null);
    }

    @Test
    void ackUpdatesTheSameCommandWithoutANewName() throws Exception {
        TaskExchangeTranscriptReqDTO req = packet(TaskExchangeTranscriptReqDTO.INBOUND,
                "{\"opcode\":500106,\"msgId\":\"e071\",\"data\":{\"opcode\":500104}}");
        req.setEventTopicCode("inspection_command_accepted");

        notice.publish(req, 924024L, objectMapper.readTree(req.getPayloadJson()));

        verify(publisher).publishProcessNotice(
                "inspection_command_accepted",
                924024L,
                "dev-1",
                null,
                "设备已接收",
                null,
                "e071",
                TaskExchangeTranscriptReqDTO.INBOUND,
                1L,
                null);
    }

    @Test
    void replyIsNotSentFromTheLedgerRow() throws Exception {
        TaskExchangeTranscriptReqDTO req = packet(TaskExchangeTranscriptReqDTO.INBOUND,
                "{\"opcode\":500104,\"msgId\":\"e071\",\"code\":200}");
        req.setEventTopicCode("inspection_command_accepted");

        notice.publish(req, 924024L, objectMapper.readTree(req.getPayloadJson()));

        verify(publisher, never()).publishProcessNotice(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }

    @Test
    void missingTopicIsNotInvented() throws Exception {
        TaskExchangeTranscriptReqDTO req = packet(TaskExchangeTranscriptReqDTO.OUTBOUND,
                "{\"opcode\":500104,\"msgId\":\"e071\"}");

        notice.publish(req, 924024L, objectMapper.readTree(req.getPayloadJson()));

        verify(publisher, never()).publishProcessNotice(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }

    @Test
    void statusNames() {
        assertEquals("下发中", CommandWindowPageNotice.eventStatus(TaskExchangeTranscriptReqDTO.OUTBOUND, 500104));
        assertEquals("设备已接收", CommandWindowPageNotice.eventStatus(TaskExchangeTranscriptReqDTO.INBOUND, 500106));
        assertNull(CommandWindowPageNotice.eventStatus(TaskExchangeTranscriptReqDTO.INBOUND, 500104));
        assertNull(CommandWindowPageNotice.eventStatus(TaskExchangeTranscriptReqDTO.INBOUND, 500202));
        assertNull(CommandWindowPageNotice.eventStatus(TaskExchangeTranscriptReqDTO.INBOUND, 500105));
    }

    private static TaskExchangeTranscriptReqDTO packet(String direction, String payloadJson) {
        TaskExchangeTranscriptReqDTO req = new TaskExchangeTranscriptReqDTO();
        req.setDeviceId("dev-1");
        req.setDirection(direction);
        req.setPayloadJson(payloadJson);
        req.setReceivedAtEpochMs(1L);
        return req;
    }
}
