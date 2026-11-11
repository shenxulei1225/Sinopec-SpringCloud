package cn.cheers.x.module.dynamicbusiness.service.pagegrant;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 角色与权限页「数据目录以外」可勾页面的唯一登记。
 * 负责：列出分组、页面编码、可勾动作与中文名；供保存校验与角色页展示共用。
 * 不负责：谁勾了什么（那是 page_role_grant）；不放行业务接口；不登记数据目录。
 *
 * 新增业务页时只改这里。禁止再在前端另写一份同名白名单当权威。
 */
public final class PagePermissionRegistry {

    public record ActionDef(String code, String label) {
    }

    public record PageDef(String code, String name, List<ActionDef> actions) {
    }

    public record GroupDef(String title, List<PageDef> pages) {
    }

    private static final List<GroupDef> GROUPS = List.of(
            group("场站实时监控",
                    page("live-monitor", "场站实时监控",
                            action("view", "查看画面"),
                            action("command", "下发操作"))),
            group("巡检管理",
                    page("inspection-task", "巡检任务",
                            action("view", "查看"),
                            action("maintain", "维护"),
                            action("enable", "启用停用")),
                    page("inspection-schedule", "巡检排期",
                            action("view", "查看"))),
            group("应急管理",
                    page("emergency-event", "应急事件列表",
                            action("view", "查看"),
                            action("create", "新建"),
                            action("confirm", "确认"),
                            action("assess", "研判"),
                            action("start", "启动响应"),
                            action("supplement", "补充"),
                            action("command", "指挥")),
                    page("emergency-plan", "应急预案",
                            action("view", "查看"),
                            action("maintain", "维护")),
                    page("emergency-custom-form", "自定义表单",
                            action("view", "查看"),
                            action("maintain", "维护"))),
            group("智能分析",
                    page("analysis-capability", "分析能力",
                            action("view", "查看"),
                            action("maintain", "维护"),
                            action("enable", "启用")),
                    page("analysis-job", "分析任务",
                            action("view", "查看")),
                    page("analysis-provider", "服务接入",
                            action("view", "查看"),
                            action("maintain", "维护"),
                            action("enable", "启用"))),
            group("安防管理",
                    page("security-visitor", "访客登记",
                            action("view", "查看"),
                            action("maintain", "登记")),
                    page("security-my-visitors", "我的访客",
                            action("view", "查看"),
                            action("maintain", "同意")),
                    page("security-my-vehicles", "我的外来车",
                            action("view", "查看"),
                            action("maintain", "同意")),
                    // 放行已经在访客登记的访客表里，顶栏不再单开这一页。旧授权仍认这个编码。
                    page("security-visitor-release", "访客放行",
                            action("view", "查看"),
                            action("maintain", "放行和离开")),
                    page("security-person-location", "人员定位",
                            action("view", "查看")),
                    page("security-personnel", "人员管理",
                            action("view", "查看")),
                    page("security-patrol", "巡检",
                            action("view", "查看")),
                    page("security-phone", "有线电话",
                            action("view", "查看")),
                    page("security-radio", "无线通讯",
                            action("view", "查看")),
                    page("security-video", "视频监控",
                            action("view", "查看"),
                            action("maintain", "维护")),
                    page("security-perimeter", "周界报警",
                            action("view", "查看")),
                    page("security-intrusion", "入侵检测",
                            action("view", "查看")),
                    page("security-access", "门禁",
                            action("view", "查看")),
                    page("security-broadcast", "广播",
                            action("view", "查看")),
                    page("security-resource", "安全资源位置",
                            action("view", "查看")),
                    page("security-person", "人员识别",
                            action("view", "查看")),
                    page("security-vehicle-gate", "门岗车辆",
                            action("view", "查看"),
                            action("maintain", "处理过车")),
                    page("security-vehicle-pass", "车辆进出",
                            action("view", "查看"),
                            action("maintain", "查看进出")),
                    page("security-vehicle", "车辆定位",
                            action("view", "查看")),
                    page("security-behavior", "行为识别",
                            action("view", "查看")),
                    page("security-alarm", "告警记录",
                            action("view", "查看"),
                            action("maintain", "维护")),
                    page("security-alarm-rule", "告警规则",
                            action("view", "查看"),
                            action("maintain", "维护"))),
            group("路网管理",
                    page("road-network", "路网管理",
                            action("view", "查看"))),
            group("管廊",
                    page("gallery-facility", "管廊设施",
                            action("view", "查看")),
                    page("gallery-region", "运营区域",
                            action("view", "查看")),
                    page("gallery-zone", "分区",
                            action("view", "查看")),
                    page("gallery-structure", "构筑物",
                            action("view", "查看")),
                    page("gallery-point", "点位",
                            action("view", "查看")),
                    page("gallery-pipeline", "管线",
                            action("view", "查看")),
                    page("gallery-point-binding", "数据点位对照",
                            action("view", "查看"))),
            group("设备管理",
                    page("equipment-ledger", "设备台账",
                            action("view", "查看")),
                    page("spare-parts", "备品备件",
                            action("view", "查看")),
                    page("station-vehicle-register", "车辆登记",
                            action("view", "查看"),
                            action("maintain", "登记"))),
            group("报修管理",
                    page("repair-request", "我的报修",
                            action("view", "查看"),
                            action("maintain", "新建与维护")),
                    page("repair-accept", "报修受理",
                            action("view", "查看"),
                            action("maintain", "受理与派单")),
                    page("fault-ledger", "故障台账",
                            action("view", "查看"))),
            group("工单",
                    page("work-order-center", "工单中心",
                            action("view", "查看"),
                            action("maintain", "派单")),
                    page("work-order-execute", "我的工单",
                            action("view", "查看"),
                            action("maintain", "认领、开工与完工"))),
            group("站场地理信息核对",
                    page("facility-gis", "站场地理信息核对",
                            action("view", "查看"))),
            group("统计",
                    page("stats", "统计",
                            action("view", "查看"))),
            group("平台配置",
                    page("catalog-admin", "数据管理",
                            action("view", "查看目录"),
                            action("create", "新建目录"),
                            action("update", "修改目录"),
                            action("delete", "删除目录")),
                    page("organization", "组织架构",
                            action("view", "查看"),
                            action("maintain-dept", "维护部门"),
                            action("maintain-user", "维护人员"),
                            action("reset-password", "重置密码")),
                    page("portal", "门户",
                            action("view", "查看"),
                            action("maintain", "维护名称和 Logo")),
                    page("role-permission", "角色与权限",
                            action("view", "查看"),
                            action("maintain-role", "维护角色"),
                            action("save-grant", "保存权限")),
                    page("system-account", "系统账号",
                            action("view", "查看"),
                            action("maintain", "维护")),
                    page("resource-library", "资源库",
                            action("view", "查看"),
                            action("maintain", "维护")),
                    page("plugin", "插件管理",
                            action("view", "查看"),
                            action("maintain", "维护")),
                    page("capability-config", "能力配置",
                            action("view", "查看"),
                            action("maintain", "维护")),
                    page("station-config", "站场配置",
                            action("view", "查看"),
                            action("maintain", "维护")),
                    page("gis-admin", "地理信息管理",
                            action("view", "查看"),
                            action("maintain", "维护")),
                    page("business-list", "业务列表",
                            action("view", "查看"),
                            action("maintain", "维护")),
                    page("mind-map", "思维导图",
                            action("view", "查看"),
                            action("maintain", "维护")),
                    page("workflow", "工作流配置",
                            action("view", "查看"),
                            action("maintain", "维护")))
    );

    private static final Map<String, Set<String>> PAGE_ACTIONS = buildPageActions();

    private PagePermissionRegistry() {
    }

    public static List<GroupDef> groups() {
        return GROUPS;
    }

    /** 页面编码 → 允许的动作码；保存与回显校验只认这份。 */
    public static Map<String, Set<String>> pageActions() {
        return PAGE_ACTIONS;
    }

    /** 页面在开通勾选和角色页上的中文名。未登记时退回编码。 */
    public static String pageName(String pageCode) {
        if (pageCode == null || pageCode.isBlank()) {
            return "";
        }
        String code = pageCode.trim();
        for (GroupDef group : GROUPS) {
            for (PageDef page : group.pages()) {
                if (code.equals(page.code())) {
                    return page.name();
                }
            }
        }
        return code;
    }

    public static Set<String> actionsOf(String pageCode) {
        if (pageCode == null || pageCode.isBlank()) {
            return Set.of();
        }
        Set<String> actions = PAGE_ACTIONS.get(pageCode.trim());
        return actions == null ? Set.of() : actions;
    }

    private static Map<String, Set<String>> buildPageActions() {
        Map<String, Set<String>> map = new LinkedHashMap<>();
        for (GroupDef group : GROUPS) {
            for (PageDef page : group.pages()) {
                Set<String> actions = new LinkedHashSet<>();
                for (ActionDef action : page.actions()) {
                    actions.add(action.code());
                }
                map.put(page.code(), Set.copyOf(actions));
            }
        }
        return Map.copyOf(map);
    }

    private static GroupDef group(String title, PageDef... pages) {
        return new GroupDef(title, List.of(pages));
    }

    private static PageDef page(String code, String name, ActionDef... actions) {
        return new PageDef(code, name, List.of(actions));
    }

    private static ActionDef action(String code, String label) {
        return new ActionDef(code, label);
    }

    /** 供测试或诊断：全部页面编码。 */
    public static List<String> allPageCodes() {
        List<String> codes = new ArrayList<>(PAGE_ACTIONS.size());
        codes.addAll(PAGE_ACTIONS.keySet());
        return List.copyOf(codes);
    }
}
