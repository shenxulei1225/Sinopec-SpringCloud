package cn.cheers.x.device.protocolsimulator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulatorRepliesTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SimulatorState state = new SimulatorState();
    private final SimulatorReplies replies = new SimulatorReplies(objectMapper, state, "dev-1", "uav");

    @Test
    void locate_repliesAckThenSameOpcode() throws Exception {
        List<String> sent = replies.immediate(
                "{\"opcode\":500101,\"msgId\":\"m1\",\"deviceId\":\"dev-1\"}", 1000L);

        assertEquals(500106, objectMapper.readTree(sent.get(0)).path("opcode").asInt());
        JsonNode body = objectMapper.readTree(sent.get(1));
        assertEquals(500101, body.path("opcode").asInt());
        assertEquals(200, body.path("code").asInt());
        assertEquals("dev-1", body.path("data").path("deviceId").asText());
        assertEquals("17.874", body.path("data").path("height").asText());
        assertEquals("no error", body.path("data").path("faultReasion").asText());
        assertEquals(1, body.path("data").path("dataLink").asInt());
    }

    @Test
    void stepResult_staysOnResultLikeOldSimulator() throws Exception {
        replies.immediate(
                "{\"opcode\":500104,\"msgId\":\"m2\",\"templateId\":\"t1\",\"packages\":\"[{\\\"opcode\\\":200102,\\\"sequence\\\":0,\\\"request\\\":{\\\"pointId\\\":\\\"p1\\\"}}]\"}",
                2000L);

        List<String> steps = replies.runTask("task-1", state.packet("t1").orElseThrow().packagesJson(), 3000L);
        JsonNode step = objectMapper.readTree(steps.get(1));
        assertEquals(500202, step.path("opcode").asInt());
        assertEquals(200102, step.path("result").path("opcode").asInt());
        assertEquals(0, step.path("result").path("sequence").asInt());
        assertTrue(step.path("packages").isMissingNode());
    }

    @Test
    void robot_stepResult_usesPackagesAndEchoesHeartbeat() throws Exception {
        SimulatorState robotState = new SimulatorState();
        SimulatorReplies robot = new SimulatorReplies(objectMapper, robotState, "robot-sim-001", "robot");
        robot.immediate(
                "{\"opcode\":500104,\"msgId\":\"m2\",\"templateId\":\"t1\",\"packages\":\"[{\\\"opcode\\\":200301,\\\"sequence\\\":1,\\\"request\\\":{\\\"pointId\\\":\\\"p1\\\"}}]\"}",
                2000L);

        List<String> steps = robot.runTask("task-1", robotState.packet("t1").orElseThrow().packagesJson(), 3000L);
        JsonNode started = objectMapper.readTree(steps.get(0));
        assertEquals(500203, started.path("opcode").asInt());
        assertEquals("task-1", started.path("taskId").asText());
        assertEquals("3000", started.path("msgId").asText());
        assertEquals("39.1277217676", started.path("reportPoint").path("lat").asText());
        assertEquals("222222", started.path("reportPoint").path("pointId").asText());
        assertEquals(3, started.path("reportPoint").path("optime").asInt());
        JsonNode step = objectMapper.readTree(steps.get(1));
        assertEquals(500202, step.path("opcode").asInt());
        assertEquals("task-1", step.path("taskId").asText());
        assertEquals(200301, step.path("packages").path("opcode").asInt());
        assertEquals(1, step.path("packages").path("sequence").asInt());
        assertEquals(222222, step.path("packages").path("reportPoint").path("pointId").asInt());
        assertTrue(step.path("result").isMissingNode());

        List<String> heartbeat = robot.immediate("{\"opcode\":500105,\"msgId\":\"hb\"}", 4000L);
        assertEquals(1, heartbeat.size());
        assertEquals(500105, objectMapper.readTree(heartbeat.get(0)).path("opcode").asInt());
    }

    @Test
    void stepWithoutSequence_isNotReported() throws Exception {
        replies.immediate(
                "{\"opcode\":500104,\"msgId\":\"m7\",\"templateId\":\"t2\",\"packages\":\"[{\\\"opcode\\\":200102,\\\"request\\\":{}}]\"}",
                6000L);

        List<String> steps = replies.runTask("task-2", state.packet("t2").orElseThrow().packagesJson(), 6001L);

        assertEquals(2, steps.size());
        assertEquals(500203, objectMapper.readTree(steps.get(0)).path("opcode").asInt());
        assertEquals("task-2", objectMapper.readTree(steps.get(0)).path("taskId").asText());
        assertEquals(500203, objectMapper.readTree(steps.get(1)).path("opcode").asInt());
    }

    @Test
    void listTemplates_returnsSavedRowsAndUnknownOpcodeIsNotSuccess() throws Exception {
        replies.immediate(
                "{\"opcode\":500104,\"msgId\":\"m3\",\"templateId\":\"100111\",\"packages\":\"[]\"}",
                5000L);

        JsonNode listed = objectMapper.readTree(replies.immediate(
                "{\"opcode\":500303,\"msgId\":\"m4\"}", 5001L).get(1));
        assertEquals(200, listed.path("code").asInt());
        assertEquals("100111", listed.path("data").path("templates").get(0).path("templateId").asText());
        assertEquals("5000", listed.path("data").path("templates").get(0).path("lineId").asText());

        JsonNode empty = objectMapper.readTree(new SimulatorReplies(
                objectMapper, new SimulatorState(), "dev-1", "robot")
                .immediate("{\"opcode\":500303,\"msgId\":\"m5\"}", 5002L).get(1));
        assertEquals(0, empty.path("data").path("templates").size());

        JsonNode unknown = objectMapper.readTree(replies.immediate(
                "{\"opcode\":509999,\"msgId\":\"m6\"}", 5003L).get(1));
        assertEquals(900103, unknown.path("code").asInt());
    }
}
