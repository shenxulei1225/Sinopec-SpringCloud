package cn.cheers.x.module.dynamicbusiness.service.pagegrant;

import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogRoleGrantItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 业务页用到哪些数据目录。
 * 角色页只勾业务的查看和要写入的动作。保存时按这份登记写成目录授权。
 * 查看写成目录的查看。要写入的动作写成目录的新增、修改、删除，同时保留查看。
 * 不负责：放行接口。接口仍读已经写成的目录授权。不配完整的底座目录来代替一个业务域。
 * 不负责：没有业务页的目录（标准检查库、规范标准、收费、文档）。这些目录没有勾选可带出授权。
 */
public final class PageCatalogBinding {

    private static final List<String> SUBJECTS = List.of("category", "model", "entity", "detail");
    private static final String QUERY = "query";
    private static final List<String> WRITES = List.of("create", "update", "delete");
    private static final String VIEW = "view";

    /** 写动作为 null：这个页面上除查看以外的勾选，都写成这几本目录的维护。空集合：这本目录只跟查看走。 */
    private record CatalogRule(String code, Set<String> writeActions) {
    }

    private static final Map<String, List<CatalogRule>> BY_PAGE = build();

    private PageCatalogBinding() {
    }

    /**
     * 按已勾选的业务页算出目录授权。同一本目录被几页用到时，操作取并集。
     * 没有登记目录的页面不产生目录授权。
     */
    public static List<CatalogRoleGrantItem> derive(List<PageRoleGrantItem> pages) {
        Map<String, Set<String>> operations = new LinkedHashMap<>();
        if (pages != null) {
            for (PageRoleGrantItem page : pages) {
                if (page == null || page.pageCode() == null) {
                    continue;
                }
                List<CatalogRule> rules = BY_PAGE.get(page.pageCode().trim());
                if (rules == null) {
                    continue;
                }
                Set<String> actions = actionsOf(page);
                for (CatalogRule rule : rules) {
                    if (!covers(actions, rule)) {
                        continue;
                    }
                    Set<String> ops = operations.computeIfAbsent(rule.code, key -> new LinkedHashSet<>());
                    ops.add(QUERY);
                    if (writes(actions, rule)) {
                        ops.addAll(WRITES);
                    }
                }
            }
        }
        List<CatalogRoleGrantItem> items = new ArrayList<>();
        operations.forEach((code, ops) -> {
            for (String subject : SUBJECTS) {
                items.add(new CatalogRoleGrantItem(code, subject, List.copyOf(ops)));
            }
        });
        return items;
    }

    private static Set<String> actionsOf(PageRoleGrantItem page) {
        Set<String> actions = new LinkedHashSet<>();
        if (page.actions() != null) {
            for (String action : page.actions()) {
                if (action != null && !action.isBlank()) {
                    actions.add(action.trim());
                }
            }
        }
        return actions;
    }

    private static boolean covers(Set<String> actions, CatalogRule rule) {
        return actions.contains(VIEW) || writes(actions, rule);
    }

    private static boolean writes(Set<String> actions, CatalogRule rule) {
        if (rule.writeActions != null && rule.writeActions.isEmpty()) {
            return false;
        }
        for (String action : actions) {
            if (VIEW.equals(action)) {
                continue;
            }
            if (rule.writeActions == null || rule.writeActions.contains(action)) {
                return true;
            }
        }
        return false;
    }

    private static Map<String, List<CatalogRule>> build() {
        Map<String, List<CatalogRule>> pages = new LinkedHashMap<>();
        // 巡检任务是任务的业务域。不配完整的任务、排期管理。排期写在巡检任务上。
        share(pages, List.of(
                "task_patrol", "task_record_patrol", "task_execution_step", "patrol_target", "patrol_equipment"),
                "inspection-task", "inspection-schedule");
        share(pages, List.of(
                "emergency", "emergency_resource", "emergency_team", "external_rescue_resource", "emergentcy_plan"),
                "emergency-event", "emergency-plan", "emergency-custom-form", "emergency-resource-map");
        // 四页各管一本。告警策略、告警联动是业务域，不配基础数据「策略」「联动」整本。
        one(pages, "security-alarm", "alarm");
        one(pages, "security-alarm-rule", "alarm_rule");
        one(pages, "alarm-strategy", "strategy_alarm");
        one(pages, "alarm-linkage", "linkage_alarm");
        share(pages, List.of("anomaly"),
                "analysis-capability", "analysis-job", "analysis-provider");
        share(pages, List.of(
                "fault", "maintanance", "maintenance", "task_maintenance",
                "maintanence_schedule", "task_record_maintenance", "work_order"),
                "fault-ledger", "repair-request", "repair-accept", "work-order-center", "work-order-execute");
        share(pages, List.of("equipment", "spare_parts", "equipment_inspection_config", "inspection-content"),
                "equipment-ledger", "spare-parts", "security-remote-control");
        one(pages, "security-visitor", "visitor");
        one(pages, "security-my-visitors", "visitor");
        one(pages, "security-my-vehicles", "vehicle_pass");
        one(pages, "security-person-location", "security_personnel");
        one(pages, "security-personnel", "security_personnel");
        one(pages, "security-patrol", "task_patrol");
        one(pages, "security-phone", "security_phone");
        one(pages, "security-radio", "security_radio");
        one(pages, "security-video", "security_video");
        one(pages, "security-perimeter", "security_perimeter");
        one(pages, "security-intrusion", "security_intrusion");
        one(pages, "security-access", "security_access");
        share(pages, List.of("security_access", "access_grant"), "security-access-grant");
        one(pages, "security-broadcast", "security_broadcast");
        one(pages, "security-resource", "security_resource");
        one(pages, "security-person", "security_person");
        share(pages, List.of("vehicle_pass", "vehicle_lane"), "security-vehicle-gate", "security-vehicle-pass");
        one(pages, "security-vehicle", "security_vehicle");
        one(pages, "security-behavior", "security_behavior");
        one(pages, "gallery-facility", "facility");
        one(pages, "gallery-region", "region");
        one(pages, "gallery-zone", "zone");
        one(pages, "gallery-structure", "Constructure");
        one(pages, "gallery-point", "point");
        one(pages, "gallery-pipeline", "pipeline");
        one(pages, "gallery-point-binding", "point_binding");
        one(pages, "gallery-customer", "customer");
        one(pages, "gallery-billing", "billing");
        one(pages, "station-vehicle-register", "vehicle_list");
        one(pages, "facility-gis", "facility");
        // 组织架构：查看能看组织、部门、人员。维护部门只写部门。维护人员只写人员。
        pages.put("organization", List.of(
                rule("organization", Set.of()),
                rule("department", Set.of("maintain-dept")),
                rule("user_account", Set.of("maintain-user"))));
        one(pages, "duty-management", "duty_post");
        return Map.copyOf(pages);
    }

    private static void share(Map<String, List<CatalogRule>> pages, List<String> catalogs, String... pageCodes) {
        List<CatalogRule> rules = new ArrayList<>();
        for (String catalog : catalogs) {
            rules.add(rule(catalog, null));
        }
        List<CatalogRule> fixed = List.copyOf(rules);
        for (String pageCode : pageCodes) {
            pages.put(pageCode, fixed);
        }
    }

    private static void one(Map<String, List<CatalogRule>> pages, String pageCode, String catalog) {
        pages.put(pageCode, List.of(rule(catalog, null)));
    }

    private static CatalogRule rule(String code, Set<String> writeActions) {
        return new CatalogRule(code, writeActions);
    }
}
