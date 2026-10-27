package cn.cheers.x.device.protocolgateway.datacollection;

import cn.cheers.x.device.protocolgateway.api.channel.AccessChannelCodes;
import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.datacollection.ProtocolQualifyStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 转发名把设备路径收成标准事件字段。没有转发到主题时报文种类仍是操作码。
 */
class StandardEventForwarderTest {

    @Test
    void projectsForwardNamesAndTopic() {
        CollectionSample sample = sample(Map.of(
                "result", Map.of("lat", "31.1", "lng", "121.2", "height", "8", "operatePoint", Map.of("lat", "1"))
        ));
        CatalogLookup lookup = CatalogLookup.found(List.of(
                new FieldDescriptionRow("result.lat", "string", "纬度", true, "lat"),
                new FieldDescriptionRow("result.lng", "string", "经度", true, "lng"),
                new FieldDescriptionRow("result.operatePoint.lat", "string", "巡检点纬度", false)
        )).withForwardTopic("inspection_execution_trajectory");

        CollectionSample forwarded = StandardEventForwarder.apply(sample, lookup);

        assertEquals("inspection_execution_trajectory", forwarded.messageKind());
        assertEquals("31.1", forwarded.fields().get("lat"));
        assertEquals("121.2", forwarded.fields().get("lng"));
        assertFalse(forwarded.fields().containsKey("operatePoint"));
    }

    @Test
    void projectsPackageListUnderContainerName() {
        CollectionSample sample = sample(Map.of(
                "packages", List.of(Map.of(
                        "sequence", 1,
                        "opcode", 200301,
                        "result", Map.of("operatePoint", Map.of("lat", "3"))
                ))
        ));
        CatalogLookup lookup = CatalogLookup.found(List.of(
                new FieldDescriptionRow("packages", "array", "步骤", true, "steps"),
                new FieldDescriptionRow("packages[].sequence", "integer", "序号", true, "sequence"),
                new FieldDescriptionRow("packages[].opcode", "integer", "内层操作码", true, "opcode"),
                new FieldDescriptionRow("packages[].result.operatePoint.lat", "string", "纬度", false, "lat")
        )).withForwardTopic("inspection_execution_step");

        CollectionSample forwarded = StandardEventForwarder.apply(sample, lookup);

        assertEquals("inspection_execution_step", forwarded.messageKind());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> steps = (List<Map<String, Object>>) forwarded.fields().get("steps");
        assertEquals(1, steps.get(0).get("sequence"));
        assertEquals(200301, steps.get(0).get("opcode"));
        assertEquals("3", steps.get(0).get("lat"));
    }

    @Test
    void unqualifiedStaysOnOpcode() {
        CollectionSample sample = new CollectionSample(
                AccessChannelCodes.INSPECTION, "d1", "500103",
                ProtocolQualifyStatus.UNQUALIFIED, List.of("缺必填"),
                Map.of("result", Map.of("lat", "1")), null, "m", 1L);
        CatalogLookup lookup = CatalogLookup.found(List.of(
                new FieldDescriptionRow("result.lat", "string", "纬度", true, "lat")
        )).withForwardTopic("inspection_execution_trajectory");

        CollectionSample forwarded = StandardEventForwarder.apply(sample, lookup);
        assertEquals("inspection_execution_trajectory", forwarded.messageKind());
        assertTrue(forwarded.fields().isEmpty());
        assertEquals(List.of("缺必填"), forwarded.qualifyErrors());
    }

    private static CollectionSample sample(Map<String, Object> fields) {
        return new CollectionSample(
                AccessChannelCodes.INSPECTION, "d1", "500103",
                ProtocolQualifyStatus.QUALIFIED, List.of(), fields, 15L, "m", 1L);
    }
}
