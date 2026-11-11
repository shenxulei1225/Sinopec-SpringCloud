package cn.cheers.x.module.dynamicbusiness.service.tenantmenu;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TenantPortalMenuServiceImplTest {

    @Test
    void liveMonitorIsItselfALevelOnePage() {
        List<TenantPortalMenuNode> nodes = TenantPortalMenuServiceImpl.generate(List.of(
                group("station-live-monitor", "场站实时监控", page("live-monitor", "场站实时监控"))
        ));
        assertEquals(1, nodes.size());
        assertEquals("场站实时监控", nodes.get(0).title());
        assertEquals("live-monitor", nodes.get(0).pageCode());
        assertTrue(nodes.get(0).children().isEmpty());
    }

    @Test
    void oneEmergencyPageStaysInsideTheEmergencyGroup() {
        List<TenantPortalMenuNode> nodes = TenantPortalMenuServiceImpl.generate(List.of(
                group("emergency", "应急", page("emergency-event", "应急事件列表"))
        ));
        assertEquals(1, nodes.size());
        assertEquals("应急", nodes.get(0).title());
        assertNull(nodes.get(0).pageCode());
        assertEquals("emergency-event", nodes.get(0).children().get(0).pageCode());
    }

    @Test
    void repairWorkOrderAndEquipmentStaySeparate() {
        List<TenantPortalMenuNode> nodes = TenantPortalMenuServiceImpl.generate(List.of(
                group("facility-ops", "场站资产与运维",
                        page("equipment-ledger", "设备台账"),
                        page("spare-parts", "备品备件"),
                        page("fault-ledger", "故障台账"),
                        page("repair-request", "我的报修"),
                        page("repair-accept", "报修受理"),
                        page("work-order-center", "工单中心"),
                        page("work-order-execute", "我的工单"))
        ));
        assertEquals(List.of("设备管理", "报修管理", "工单"),
                nodes.stream().map(TenantPortalMenuNode::title).toList());
        assertEquals(List.of("设备台账", "备品备件"),
                nodes.get(0).children().stream().map(TenantPortalMenuNode::title).toList());
        assertEquals(List.of("我的报修", "报修受理", "故障台账"),
                nodes.get(1).children().stream().map(TenantPortalMenuNode::title).toList());
        assertNull(nodes.get(2).pageCode());
        assertEquals(List.of("工单中心", "我的工单"),
                nodes.get(2).children().stream().map(TenantPortalMenuNode::title).toList());
    }

    @Test
    void galleryAndRoadNetworkShareOneGroup() {
        List<TenantPortalMenuNode> nodes = TenantPortalMenuServiceImpl.generate(List.of(
                group("pipe-gallery", "管廊", page("gallery-facility", "管廊设施")),
                group("facility-ops", "场站资产与运维", page("road-network", "路网管理"))
        ));
        assertEquals(1, nodes.size());
        assertEquals("管廊", nodes.get(0).title());
        assertEquals(List.of("管廊设施", "路网管理"),
                nodes.get(0).children().stream().map(TenantPortalMenuNode::title).toList());
    }

    @Test
    void platformPagesStayOffTheDefaultMenu() {
        List<TenantPortalMenuNode> nodes = TenantPortalMenuServiceImpl.generate(List.of(
                group("platform-extensions", "平台扩展配置", page("resource-library", "资源库")),
                group("tenant-foundation", "租户底座", page("organization", "组织架构"))
        ));
        assertEquals(1, nodes.size());
        assertEquals("系统配置", nodes.get(0).title());
        assertEquals(List.of("组织架构"),
                nodes.get(0).children().stream().map(TenantPortalMenuNode::title).toList());
    }

    @Test
    void securityMenuGroupsPeopleCommAndTechnical() {
        List<TenantPortalMenuNode> nodes = TenantPortalMenuServiceImpl.generate(List.of(
                group("security-video", "安防视频",
                        page("security-visitor", "访客登记"),
                        page("security-my-visitors", "我的访客"),
                        page("security-person-location", "人员定位"),
                        page("security-patrol", "巡检"),
                        page("security-phone", "有线电话"),
                        page("security-video", "视频监控"),
                        page("security-alarm", "告警记录"))
        ));
        assertEquals(1, nodes.size());
        assertEquals("安防", nodes.get(0).title());
        assertEquals(List.of("人防", "通信", "技防", "巡检"),
                nodes.get(0).children().stream().map(TenantPortalMenuNode::title).toList());
        assertEquals(List.of("访客登记", "我的访客", "人员定位"),
                nodes.get(0).children().get(0).children().stream().map(TenantPortalMenuNode::title).toList());
        assertEquals(List.of("有线电话"),
                nodes.get(0).children().get(1).children().stream().map(TenantPortalMenuNode::title).toList());
        assertEquals(List.of("视频监控", "告警记录"),
                nodes.get(0).children().get(2).children().stream().map(TenantPortalMenuNode::title).toList());
        assertEquals("security-patrol", nodes.get(0).children().get(3).pageCode());
    }

    private static TenantPortalMenuPageGroup group(String code, String name, TenantPortalMenuPage... pages) {
        return new TenantPortalMenuPageGroup(code, name, List.of(pages));
    }

    private static TenantPortalMenuPage page(String code, String name) {
        return new TenantPortalMenuPage(code, name);
    }
}
