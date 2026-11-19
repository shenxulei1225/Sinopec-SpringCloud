package cn.cheers.x.module.dynamicbusiness.service.emergencycommand;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmergencyNearbyResourceQueryServiceTest {

    @Test
    void samePointIsZero() {
        assertEquals(0.0, EmergencyNearbyResourceQueryService.straightLineKm(117.24, 34.30, 117.24, 34.30));
    }

    @Test
    void oneDegreeOfLatitudeIsAboutOneHundredElevenKm() {
        double km = EmergencyNearbyResourceQueryService.straightLineKm(117.0, 34.0, 117.0, 35.0);
        assertTrue(km > 110.5 && km < 111.8, String.valueOf(km));
    }
}
