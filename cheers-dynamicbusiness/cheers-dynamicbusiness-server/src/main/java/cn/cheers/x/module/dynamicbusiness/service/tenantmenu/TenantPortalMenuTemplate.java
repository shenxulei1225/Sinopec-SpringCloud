package cn.cheers.x.module.dynamicbusiness.service.tenantmenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 还没保存过菜单时，各租户顶栏共用的排法。
 * 负责：按已开通页面排出一级名称和下面的页面。安防下面再分人防、通信、技防。某一组一页都没开通时，这一级不出现。
 * 不负责：写入租户菜单表。租户管理员以后在菜单管理里改的顺序，以他保存的那份为准。
 * 平台扩展配置、系统账号、站场地理信息核对不在这份排法里。要给某个租户看时，再从菜单管理拖进去。
 * 维修任务、维修排期、维修记录不单列。维修过程在工单里，设备上看到的是维修工单。
 */
final class TenantPortalMenuTemplate {

    private TenantPortalMenuTemplate() {
    }

    private record Slot(String title, String pageCode, List<Slot> children) {

        private static Slot page(String title, String pageCode) {
            return new Slot(title, pageCode, List.of());
        }

        private static Slot group(String title, Slot... children) {
            return new Slot(title, null, List.of(children));
        }
    }

    /** 一级顺序。自己就是页面的，下面不再挂页面。 */
    private static final List<Slot> SLOTS = List.of(
            Slot.page("场站实时监控", "live-monitor"),
            Slot.group("管廊",
                    Slot.page("管廊设施", "gallery-facility"),
                    Slot.page("运营区域", "gallery-region"),
                    Slot.page("分区", "gallery-zone"),
                    Slot.page("构筑物", "gallery-structure"),
                    Slot.page("点位", "gallery-point"),
                    Slot.page("管线", "gallery-pipeline"),
                    Slot.page("数据点位对照", "gallery-point-binding"),
                    Slot.page("路网管理", "road-network")),
            Slot.group("设备管理",
                    Slot.page("设备台账", "equipment-ledger"),
                    Slot.page("备品备件", "spare-parts"),
                    Slot.page("车辆登记", "station-vehicle-register")),
            Slot.group("报修管理",
                    Slot.page("我的报修", "repair-request"),
                    Slot.page("报修受理", "repair-accept"),
                    Slot.page("故障台账", "fault-ledger")),
            Slot.group("工单",
                    Slot.page("工单中心", "work-order-center"),
                    Slot.page("我的工单", "work-order-execute")),
            Slot.group("巡检",
                    Slot.page("巡检任务", "inspection-task"),
                    Slot.page("巡检排期", "inspection-schedule")),
            // 物防是围墙、门、锁、舱门、井盖。这些页面还没有，不排空组。
            // 巡检打开巡检任务，安全资源位置只标资源落点，这两项不进三组。
            Slot.group("安防",
                    Slot.group("人防",
                            Slot.page("访客登记", "security-visitor"),
                            Slot.page("我的访客", "security-my-visitors"),
                            Slot.page("我的外来车", "security-my-vehicles"),
                            Slot.page("人员定位", "security-person-location")),
                    Slot.group("通信",
                            Slot.page("有线电话", "security-phone"),
                            Slot.page("无线通讯", "security-radio")),
                    Slot.group("技防",
                            Slot.page("视频监控", "security-video"),
                            Slot.page("周界报警", "security-perimeter"),
                            Slot.page("入侵检测", "security-intrusion"),
                            Slot.page("门禁", "security-access"),
                            Slot.page("门岗车辆", "security-vehicle-gate"),
                            Slot.page("车辆进出", "security-vehicle-pass"),
                            Slot.page("广播", "security-broadcast"),
                            Slot.page("人员识别", "security-person"),
                            Slot.page("车辆定位", "security-vehicle"),
                            Slot.page("行为识别", "security-behavior"),
                            Slot.page("告警记录", "security-alarm"),
                            Slot.page("告警规则", "security-alarm-rule")),
                    Slot.page("安全资源位置", "security-resource"),
                    Slot.page("巡检", "security-patrol")),
            Slot.group("应急",
                    Slot.page("应急事件列表", "emergency-event"),
                    Slot.page("应急预案", "emergency-plan"),
                    Slot.page("自定义表单", "emergency-custom-form")),
            Slot.group("智能分析",
                    Slot.page("分析能力", "analysis-capability"),
                    Slot.page("分析任务", "analysis-job"),
                    Slot.page("服务接入", "analysis-provider")),
            Slot.group("系统配置",
                    Slot.page("组织架构", "organization"),
                    Slot.page("门户", "portal"),
                    Slot.page("角色与权限", "role-permission"),
                    Slot.page("数据管理", "catalog-admin")));

    /**
     * 按这份排法，只留下已经开通的页面。
     * 一级自己就是页面时，用这里的名称。
     * 分组下面的页面用开通清单里的中文名。工单这一级下面是工单中心和我的工单。
     */
    static List<TenantPortalMenuNode> apply(Map<String, String> opened) {
        List<TenantPortalMenuNode> nodes = new ArrayList<>();
        if (opened == null || opened.isEmpty()) {
            return nodes;
        }
        for (Slot slot : SLOTS) {
            TenantPortalMenuNode node = materialize(slot, opened);
            if (node != null) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    private static TenantPortalMenuNode materialize(Slot slot, Map<String, String> opened) {
        if (slot.pageCode() != null) {
            if (!opened.containsKey(slot.pageCode())) {
                return null;
            }
            return new TenantPortalMenuNode(slot.title(), slot.pageCode(), List.of());
        }
        List<TenantPortalMenuNode> children = new ArrayList<>();
        for (Slot child : slot.children()) {
            if (child.pageCode() != null) {
                if (!opened.containsKey(child.pageCode())) {
                    continue;
                }
                String name = opened.get(child.pageCode());
                if (name == null || name.isBlank()) {
                    name = child.title();
                }
                children.add(new TenantPortalMenuNode(name, child.pageCode(), List.of()));
                continue;
            }
            TenantPortalMenuNode group = materialize(child, opened);
            if (group != null) {
                children.add(group);
            }
        }
        if (children.isEmpty()) {
            return null;
        }
        return new TenantPortalMenuNode(slot.title(), null, children);
    }
}
