package cn.cheers.x.device.protocolgateway.instructiondispatch;

import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WaylineKmzBuilderTest {

    @Test
    void packsTemplateAndExecutableWayline() throws Exception {
        DispatchAction point = new DispatchAction(1L, Map.of(
                "latitude", 39.12,
                "longitude", 117.01,
                "height", 30,
                "max_speed", 5,
                "security_takeoff_height", 20,
                "rth_altitude", 80,
                "finish_action", "goHome"
        ), "action-065340437bcb46788b7dc261fab81c63");
        DispatchAction gimbal = new DispatchAction(2L, Map.of("gimbalYawRotateAngle", 30), "gimbalRotate");
        WaylineKmzBuilder.Built built = WaylineKmzBuilder.build(List.of(point, gimbal));
        assertTrue(built.fingerprint().length() == 32);
        String packed = zipText(built.kmz());
        assertTrue(packed.contains("wpmz/template.kml"));
        assertTrue(packed.contains("wpmz/waylines.wpml"));
        assertTrue(packed.contains("gimbalRotate"));
        assertTrue(packed.contains("reachPoint"));
        assertTrue(packed.contains("<wpml:finishAction>goHome</wpml:finishAction>"));
    }

    @Test
    void landAtPointWritesAutoLand() throws Exception {
        DispatchAction head = new DispatchAction(9L, Map.of(
                "max_speed", 5,
                "security_takeoff_height", 20,
                "rth_altitude", 80,
                "finish_action", "autoLand"
        ), "act-uav-wayline-prepare");
        DispatchAction point = new DispatchAction(1L, Map.of(
                "latitude", 39.12,
                "longitude", 117.01,
                "height", 30
        ), "action-065340437bcb46788b7dc261fab81c63");
        String packed = zipText(WaylineKmzBuilder.build(List.of(head, point)).kmz());
        assertTrue(packed.contains("<wpml:finishAction>autoLand</wpml:finishAction>"));
    }

    @Test
    void missingFinishStops() {
        DispatchAction point = new DispatchAction(1L, Map.of(
                "latitude", 39.12,
                "longitude", 117.01,
                "height", 30,
                "max_speed", 5,
                "security_takeoff_height", 20,
                "rth_altitude", 80
        ), "action-065340437bcb46788b7dc261fab81c63");
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> WaylineKmzBuilder.build(List.of(point)));
        assertTrue(ex.getMessage().contains("航线结束还没选"));
    }

    @Test
    void splitCatalogActionsUseOfficialWpmlActuators() throws Exception {
        DispatchAction point = new DispatchAction(1L, Map.of(
                "latitude", 39.12,
                "longitude", 117.01,
                "height", 30,
                "max_speed", 5,
                "security_takeoff_height", 20,
                "rth_altitude", 80,
                "finish_action", "goHome"
        ), "action-065340437bcb46788b7dc261fab81c63");
        DispatchAction pointCloud = new DispatchAction(2L, Map.of(
                "payloadPositionIndex", 0,
                "recordPointCloudOperate", "startRecord"
        ), "recordPointCloudStart");
        DispatchAction searchlight = new DispatchAction(3L, Map.of(
                "payloadPositionIndex", 0,
                "searchlightOperateType", 1,
                "searchlightBrightness", 80
        ), "searchlightOn");

        String packed = zipText(WaylineKmzBuilder.build(List.of(point, pointCloud, searchlight)).kmz());

        assertTrue(packed.contains("<wpml:actionActuatorFunc>recordPointCloud</wpml:actionActuatorFunc>"));
        assertTrue(packed.contains("<wpml:recordPointCloudOperate>startRecord</wpml:recordPointCloudOperate>"));
        assertTrue(packed.contains("<wpml:actionActuatorFunc>searchlight</wpml:actionActuatorFunc>"));
        assertTrue(packed.contains("<wpml:searchlightOperateType>1</wpml:searchlightOperateType>"));
    }

    private static String zipText(byte[] kmz) throws Exception {
        StringBuilder text = new StringBuilder();
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(kmz))) {
            java.util.zip.ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                text.append(entry.getName()).append('\n');
                text.append(new String(zip.readAllBytes(), StandardCharsets.UTF_8));
            }
        }
        return text.toString();
    }
}
