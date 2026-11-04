package cn.cheers.x.module.dynamicbusiness.service.linkage;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinkageModeQueryServiceTest {

    private final LinkageModeQueryService service = new LinkageModeQueryService();

    @Test
    void matchingUsesDeviceWhenAndIgnoresAlarmRuleText() {
        EntityDO linkage = EntityDO.builder()
                .id(101L)
                .dedicatedBaseFieldValues(Map.of(
                        LinkageFieldCodes.MODE,
                        Map.of("rows", List.of(
                                Map.of(
                                        "when", List.of("告警规则 等于 5", "告警设备 等于 77"),
                                        "match", "ALL",
                                        "steps", List.of(Map.of("actionId", 31, "equipmentId", 88))),
                                Map.of(
                                        "when", List.of("告警设备 等于 99"),
                                        "match", "ALL",
                                        "steps", List.of(Map.of("actionId", 32, "equipmentId", 89))),
                                Map.of(
                                        "when", List.of(),
                                        "match", "ALL",
                                        "steps", List.of(Map.of("actionId", 33, "equipmentId", 90)))))))
                .build();

        List<LinkagePair> found = service.findMatchingPairs(linkage, "equipment", 77L);

        assertEquals(List.of(1L, 3L), found.stream().map(LinkagePair::index).toList());
        assertEquals(88L, found.get(0).targetEquipmentId());
        assertEquals(31L, found.get(0).actionId());
        assertEquals(90L, found.get(1).targetEquipmentId());
    }

    @Test
    void matchingAcceptsDeviceCodeWhenClause() {
        EntityDO linkage = EntityDO.builder()
                .id(102L)
                .dedicatedBaseFieldValues(Map.of(
                        LinkageFieldCodes.MODE,
                        Map.of("rows", List.of(
                                Map.of(
                                        "when", List.of("device 等于 77"),
                                        "match", "ALL",
                                        "steps", List.of(Map.of("actionId", 31, "equipmentId", 88)))))))
                .build();

        List<LinkagePair> found = service.findMatchingPairs(linkage, "equipment", 77L);

        assertEquals(List.of(1L), found.stream().map(LinkagePair::index).toList());
        assertEquals(88L, found.get(0).targetEquipmentId());
    }

    @Test
    void matchingRunsEveryStepOnTheSameRow() {
        EntityDO linkage = EntityDO.builder()
                .id(103L)
                .dedicatedBaseFieldValues(Map.of(
                        LinkageFieldCodes.MODE,
                        Map.of("rows", List.of(
                                Map.of(
                                        "when", List.of("告警设备 等于 77"),
                                        "match", "ALL",
                                        "steps", List.of(
                                                Map.of("actionId", 31, "equipmentId", 88),
                                                Map.of("actionId", 31, "equipmentId", 89)))))))
                .build();

        List<LinkagePair> found = service.findMatchingPairs(linkage, "equipment", 77L);

        assertEquals(List.of(1L, 2L), found.stream().map(LinkagePair::index).toList());
        assertEquals(88L, found.get(0).targetEquipmentId());
        assertEquals(89L, found.get(1).targetEquipmentId());
        assertEquals(31L, found.get(0).actionId());
        assertEquals(31L, found.get(1).actionId());
    }
}
