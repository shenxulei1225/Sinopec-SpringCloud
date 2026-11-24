package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BusinessCapabilityPackRegistryTest {

    @Test
    void alarmPagesEachOpenOneCatalog() {
        BusinessCapabilityPackDefinition alarm = BusinessCapabilityPackRegistry.find("alarm-management").orElseThrow();
        assertEquals(
                List.of("security-alarm", "security-alarm-rule", "alarm-strategy", "alarm-linkage"),
                alarm.pageCodes());
        assertEquals(
                List.of("alarm", "alarm_rule"),
                BusinessCapabilityPackRegistry.catalogsForOpenedPages(
                        alarm, List.of("security-alarm", "security-alarm-rule")));
        assertEquals(
                List.of("alarm", "alarm_rule", "strategy_alarm", "linkage_alarm"),
                BusinessCapabilityPackRegistry.catalogsForOpenedPages(alarm, null));
        assertTrue(BusinessCapabilityPackRegistry.catalogsForOpenedPages(alarm, List.of()).isEmpty());
    }

    @Test
    void strategyAndLinkageStayOutOfStationAndAnalysis() {
        BusinessCapabilityPackDefinition station = BusinessCapabilityPackRegistry.find("station").orElseThrow();
        BusinessCapabilityPackDefinition analysis = BusinessCapabilityPackRegistry.find("intelligent-analysis").orElseThrow();
        assertFalse(station.catalogCodes().contains("alarm_strategy"));
        assertFalse(analysis.catalogCodes().contains("linkage"));
        assertTrue(analysis.catalogCodes().contains("anomaly"));
        assertEquals(
                station.catalogCodes(),
                BusinessCapabilityPackRegistry.catalogsForOpenedPages(station, List.of()));
    }
}
