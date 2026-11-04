package cn.cheers.x.module.dynamicbusiness.service.push;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PushCatalogQueryServiceTest {

    @Test
    void fillsExistingNamePattern() {
        assertEquals("device/88/status",
                PushCatalogQueryService.fillPattern("device/{deviceId}/status", "{deviceId}", 88));
        assertEquals("alarm/facility/12",
                PushCatalogQueryService.fillPattern("alarm/facility/{facilityId}", "{facilityId}", 12));
        assertNull(PushCatalogQueryService.fillPattern(null, "{deviceId}", 88));
        assertNull(PushCatalogQueryService.fillPattern("device/{deviceId}/status", "{deviceId}", 0));
        assertNull(PushCatalogQueryService.fillPattern("device/{deviceId}/status", "{facilityId}", 88));
    }
}
