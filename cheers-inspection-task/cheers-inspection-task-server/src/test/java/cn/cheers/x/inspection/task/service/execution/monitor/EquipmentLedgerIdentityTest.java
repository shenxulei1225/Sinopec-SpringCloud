package cn.cheers.x.inspection.task.service.execution.monitor;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EquipmentLedgerIdentityTest {

    @Test
    void equipmentCodeIsUsedAsIs() {
        EquipmentLedgerIdentity.Match match = EquipmentLedgerIdentity.resolve(
                "EQ-DJI-PILOT-JQ-01",
                List.of(new EquipmentLedgerIdentity.Device("EQ-DJI-PILOT-JQ-01", "", "TEMP-900158")));

        assertEquals("EQ-DJI-PILOT-JQ-01", match.code());
        assertFalse(match.ambiguous());
    }

    @Test
    void aircraftSerialMapsToTheSingleEquipmentCode() {
        EquipmentLedgerIdentity.Match match = EquipmentLedgerIdentity.resolve(
                "TEMP-900158",
                List.of(
                        new EquipmentLedgerIdentity.Device("EQ-DJI-PILOT-JQ-01", null, "TEMP-900158"),
                        new EquipmentLedgerIdentity.Device("EQ-DJI-DOCK-JQ-01", "", null)));

        assertEquals("EQ-DJI-PILOT-JQ-01", match.code());
    }

    @Test
    void sharedSerialIsNotPicked() {
        EquipmentLedgerIdentity.Match match = EquipmentLedgerIdentity.resolve(
                "SN-1",
                List.of(
                        new EquipmentLedgerIdentity.Device("A", "SN-1", null),
                        new EquipmentLedgerIdentity.Device("B", null, "SN-1")));

        assertNull(match.code());
        assertTrue(match.ambiguous());
    }

    @Test
    void unknownSerialStaysUnmatched() {
        EquipmentLedgerIdentity.Match match = EquipmentLedgerIdentity.resolve(
                "missing",
                List.of(new EquipmentLedgerIdentity.Device("EQ-1", "SN-9", null)));

        assertNull(match.code());
        assertFalse(match.ambiguous());
    }
}
