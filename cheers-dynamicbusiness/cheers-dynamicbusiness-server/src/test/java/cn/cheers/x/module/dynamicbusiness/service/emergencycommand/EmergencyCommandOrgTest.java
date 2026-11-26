package cn.cheers.x.module.dynamicbusiness.service.emergencycommand;

import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmergencyCommandOrgTest {

    @Test
    void snapshotUsesBackupWhenPrimarySeatIsEmpty() {
        EmergencyCommandOrg.Group group = new EmergencyCommandOrg.Group(
                "现场应急指挥部",
                "现场指挥",
                List.of(),
                List.of(new EmergencyCommandOrg.Seat("组长", "unit", "站长", "副站长")));
        JSONObject snapshot = EmergencyCommandOrg.snapshot(groupList(group), Map.of("副站长", "李四"), Map.of());
        assertEquals("李四", snapshot.getJSONArray("groups").getJSONObject(0)
                .getJSONArray("seats").getJSONObject(0).getString("personName"));
    }

    @Test
    void companyLevelGroupStaysHiddenOnALowerLevel() {
        EmergencyCommandOrg.Group onSite = new EmergencyCommandOrg.Group(
                "现场应急指挥部", "现场", List.of(), List.of());
        EmergencyCommandOrg.Group company = new EmergencyCommandOrg.Group(
                "现场工作组", "协助", List.of("II"), List.of());
        JSONObject snapshot = EmergencyCommandOrg.snapshot(List.of(onSite, company), Map.of(), Map.of());
        assertEquals(1, EmergencyCommandOrg.visibleGroups(snapshot, "I").size());
        assertEquals(2, EmergencyCommandOrg.visibleGroups(snapshot, "II").size());
        assertTrue(EmergencyCommandOrg.visibleGroups(null, "I") == null);
    }

    @Test
    void showExplainsWhyTheRosterIsEmpty() {
        assertEquals("启动响应后才列出这一起事件的指挥机构",
                EmergencyCommandOrg.show(null, "I").gap());
        EmergencyCommandOrg.Group company = new EmergencyCommandOrg.Group(
                "现场工作组", "协助", List.of("II"), List.of());
        JSONObject snapshot = EmergencyCommandOrg.snapshot(List.of(company), Map.of(), Map.of());
        assertEquals("这一级没有要成立的指挥组", EmergencyCommandOrg.show(snapshot, "I").gap());
        assertEquals("", EmergencyCommandOrg.show(snapshot, "II").gap());
        assertEquals(1, EmergencyCommandOrg.show(snapshot, "II").groups().size());
    }

    private static List<EmergencyCommandOrg.Group> groupList(EmergencyCommandOrg.Group group) {
        return List.of(group);
    }
}
