package cn.cheers.x.module.dynamicbusiness.devicehistory;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GatewayBrowseMatcherTest {

    @Test
    void matchesTagUnderItsZoneShortcut() {
        GatewayPointRef point = point("GKYB01", "SB1_I_LOW");
        GatewaySeenNode node = new GatewaySeenNode(
                "ns=2;s=[GKYB01_ACU]SB1_I_LOW",
                "SB1_I_LOW",
                List.of("FactoryTalkLinxGateway", "GKYB01_ACU"));

        Map<String, GatewayPointRef> matched = GatewayBrowseMatcher.match(List.of(point), List.of(node));

        assertEquals(point, matched.get("ns=2;s=[GKYB01_ACU]SB1_I_LOW"));
    }

    @Test
    void matchesBrowseNameThatIsTheFullPointCode() {
        GatewayPointRef point = point("GKYB01", "SB1_I_LOW");
        GatewaySeenNode node = new GatewaySeenNode("ns=3;s=GKYB01/SB1_I_LOW", "GKYB01/SB1_I_LOW", List.of());

        Map<String, GatewayPointRef> matched = GatewayBrowseMatcher.match(List.of(point), List.of(node));

        assertEquals("GKYB01/SB1_I_LOW", matched.get("ns=3;s=GKYB01/SB1_I_LOW").getBindingCode());
    }

    @Test
    void skipsTagWhenTheZoneFolderIsMissing() {
        GatewayPointRef point = point("GKYB01", "SB1_I_LOW");
        GatewaySeenNode node = new GatewaySeenNode("ns=2;s=SB1_I_LOW", "SB1_I_LOW", List.of("Online"));

        assertTrue(GatewayBrowseMatcher.match(List.of(point), List.of(node)).isEmpty());
    }

    @Test
    void skipsWhenTwoNodesMatchOnePoint() {
        GatewayPointRef point = point("GKYB01", "SB1_I_LOW");
        GatewaySeenNode first = new GatewaySeenNode("ns=2;s=a", "SB1_I_LOW", List.of("GKYB01"));
        GatewaySeenNode second = new GatewaySeenNode("ns=2;s=b", "SB1_I_LOW", List.of("GKYB01_ACU"));

        assertTrue(GatewayBrowseMatcher.match(List.of(point), List.of(first, second)).isEmpty());
    }

    @Test
    void keepsOneAndZeroAndDropsText() {
        assertEquals(1.0d, GatewaySampleValue.engineering(true));
        assertEquals(0.0d, GatewaySampleValue.engineering(false));
        assertEquals(12.5d, GatewaySampleValue.engineering(12.5d));
        assertNull(GatewaySampleValue.engineering("开"));
        assertNull(GatewaySampleValue.engineering(null));
    }

    private static GatewayPointRef point(String zone, String tag) {
        return new GatewayPointRef(123L, 20626L, "F-point", zone + "/" + tag, zone, tag, null);
    }
}
