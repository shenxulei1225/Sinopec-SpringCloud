package cn.cheers.x.device.protocolgateway.djicloud;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DjiCloudExchangePolicyTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void osdAndStickStayInTheTranscriptAndPoseIsSeparate() throws Exception {
        var osd = objectMapper.readTree("""
                {"timestamp":1730000000000,"data":{"latitude":31.2,"longitude":121.5,"height":40}}
                """);
        assertTrue(DjiCloudExchangePolicy.keepTranscript("thing/product/aircraft-1/osd", osd));
        assertNotNull(DjiCloudExchangePolicy.poseOf("thing/product/aircraft-1/osd", osd));

        var stick = objectMapper.readTree("{\"method\":\"drone_control\",\"data\":{}}");
        assertTrue(DjiCloudExchangePolicy.keepTranscript("thing/product/dock-1/drc/up", stick));
        assertFalse(DjiCloudExchangePolicy.keepTranscript("not-a-cloud-topic", stick));
    }
}
