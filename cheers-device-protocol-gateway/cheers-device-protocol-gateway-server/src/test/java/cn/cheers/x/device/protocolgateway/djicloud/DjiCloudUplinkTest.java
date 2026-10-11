package cn.cheers.x.device.protocolgateway.djicloud;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DjiCloudUplinkTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DjiCloudUplink uplink = new DjiCloudUplink(objectMapper);
    private final DjiCloudCredentials license = new DjiCloudCredentials(
            "197159", "app-key", "app-license", "ntp.aliyun.com", 123);

    @Test
    void updateTopo_repliesResultZeroAndKeepsAircraftSn() throws Exception {
        String body = """
                {"tid":"t1","bid":"b1","method":"update_topo","data":{"sub_devices":[{"sn":"aircraft-1"}]}}
                """;
        DjiCloudDecision decision = uplink.accept(
                "sys/product/dock-1/status", body.getBytes(StandardCharsets.UTF_8), license);

        DjiCloudDecision.Reply reply = assertInstanceOf(DjiCloudDecision.Reply.class, decision);
        assertEquals("sys/product/dock-1/status_reply", reply.topic());
        assertEquals("dock-1", reply.gatewaySn());
        assertEquals(List.of("aircraft-1"), reply.subDeviceSns());
        assertTrue(reply.topology());
        JsonNode json = objectMapper.readTree(reply.json());
        assertEquals("update_topo", json.get("method").asText());
        assertEquals("t1", json.get("tid").asText());
        assertEquals(0, json.get("data").get("result").asInt());
    }

    @Test
    void config_repliesLicenseWhenComplete() throws Exception {
        String body = """
                {"tid":"t2","bid":"b2","method":"config","data":{}}
                """;
        DjiCloudDecision decision = uplink.accept(
                "thing/product/rc-1/requests", body.getBytes(StandardCharsets.UTF_8), license);

        DjiCloudDecision.Reply reply = assertInstanceOf(DjiCloudDecision.Reply.class, decision);
        assertEquals("thing/product/rc-1/requests_reply", reply.topic());
        JsonNode data = objectMapper.readTree(reply.json()).get("data");
        assertEquals("197159", data.get("app_id").asText());
        assertEquals("app-key", data.get("app_key").asText());
        assertEquals("app-license", data.get("app_license").asText());
        assertEquals(123, data.get("ntp_server_port").asInt());
    }

    @Test
    void config_doesNotInventLicense() {
        String body = """
                {"tid":"t2","bid":"b2","method":"config"}
                """;
        DjiCloudDecision decision = uplink.accept(
                "thing/product/dock-1/requests",
                body.getBytes(StandardCharsets.UTF_8),
                new DjiCloudCredentials("", "", "", "ntp.aliyun.com", 123));
        assertInstanceOf(DjiCloudDecision.MissingLicense.class, decision);
    }

    @Test
    void bindStatus_saysUnboundWhenOrganizationMissing() throws Exception {
        String body = """
                {"tid":"t4","bid":"b4","method":"airport_bind_status","data":{"devices":[{"sn":"dock-1"}]}}
                """;
        DjiCloudDecision decision = uplink.accept(
                "thing/product/dock-1/requests", body.getBytes(StandardCharsets.UTF_8), license);

        DjiCloudDecision.Reply reply = assertInstanceOf(DjiCloudDecision.Reply.class, decision);
        JsonNode item = objectMapper.readTree(reply.json()).get("data").get("output").get("bind_status").get(0);
        assertEquals("dock-1", item.get("sn").asText());
        assertEquals(false, item.get("is_device_bind_organization").asBoolean());
        assertEquals("", item.get("organization_name").asText());
    }

    @Test
    void bindStatus_usesConfiguredOrganization() throws Exception {
        String body = """
                {"tid":"t5","bid":"b5","method":"airport_bind_status","data":{"devices":[{"sn":"dock-1"}]}}
                """;
        DjiCloudDecision decision = uplink.accept(
                "thing/product/dock-1/requests",
                body.getBytes(StandardCharsets.UTF_8),
                license,
                new DjiCloudOrganization("org-1", "金桥巡检"));

        JsonNode item = objectMapper.readTree(((DjiCloudDecision.Reply) decision).json())
                .get("data").get("output").get("bind_status").get(0);
        assertEquals(true, item.get("is_device_bind_organization").asBoolean());
        assertEquals("org-1", item.get("organization_id").asText());
        assertEquals("金桥巡检", item.get("organization_name").asText());
    }

    @Test
    void organizationGet_doesNotInventName() {
        String body = """
                {"tid":"t6","bid":"b6","method":"airport_organization_get"}
                """;
        DjiCloudDecision decision = uplink.accept(
                "thing/product/dock-1/requests", body.getBytes(StandardCharsets.UTF_8), license);
        assertInstanceOf(DjiCloudDecision.MissingOrganization.class, decision);
    }

    @Test
    void organizationGet_returnsConfiguredName() throws Exception {
        String body = """
                {"tid":"t7","bid":"b7","method":"airport_organization_get"}
                """;
        DjiCloudDecision decision = uplink.accept(
                "thing/product/dock-1/requests",
                body.getBytes(StandardCharsets.UTF_8),
                license,
                new DjiCloudOrganization("org-1", "金桥巡检"));
        JsonNode output = objectMapper.readTree(((DjiCloudDecision.Reply) decision).json())
                .get("data").get("output");
        assertEquals("金桥巡检", output.get("organization_name").asText());
    }

    @Test
    void otherMethod_isNotAcked() {
        String body = """
                {"tid":"t3","bid":"b3","method":"flighttask_prepare"}
                """;
        DjiCloudDecision decision = uplink.accept(
                "thing/product/dock-1/requests", body.getBytes(StandardCharsets.UTF_8), license);
        assertInstanceOf(DjiCloudDecision.Ignore.class, decision);
    }
}
