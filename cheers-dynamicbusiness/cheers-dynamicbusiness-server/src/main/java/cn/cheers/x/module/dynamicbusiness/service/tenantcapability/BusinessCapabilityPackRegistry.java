package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 租户开通时看到的业务模块。
 * 负责：按编码取出模块，模块自带页面和数据目录。
 * 某一页写明自己打开哪本目录时，勾上这一页才带上这本目录。
 * 不负责：租户开通持久化。管廊是单独模块，不并进场站。
 */
public final class BusinessCapabilityPackRegistry {

    private static final Map<String, BusinessCapabilityPackDefinition> BY_CODE = new LinkedHashMap<>();

    /**
     * 这一页打开哪本数据目录。勾上这一页，开通时才挂上这本目录。
     * 告警策略打开告警业务域「告警策略」，告警联动打开告警业务域「告警联动」。
     * 不把基础数据「策略」「联动」整本算给告警。入廊客户管理和收费管理各打开已有的那一本目录。没写在这里的页面，不单独占一本目录。
     */
    private static final Map<String, String> PAGE_CATALOG = Map.of(
            "security-alarm", "alarm",
            "security-alarm-rule", "alarm_rule",
            "alarm-strategy", "strategy_alarm",
            "alarm-linkage", "linkage_alarm",
            "gallery-customer", "customer",
            "gallery-billing", "billing",
            "work-person-certificate", "person_certificate",
            "work-external-worker", "external_worker",
            "work-permit", "work_permit",
            "work-field-record", "repair_field_record");

    static {
        register(new BusinessCapabilityPackDefinition(
                "inspection",
                "巡检",
                "巡检任务和巡检排期",
                List.of("inspection-task", "inspection-schedule"),
                List.of(
                        "task_patrol",
                        "task_record_patrol",
                        "task_execution_step",
                        "patrol_target",
                        "patrol_equipment",
                        "schedule",
                        "task"),
                List.of("organization", "facility")));
        register(new BusinessCapabilityPackDefinition(
                "emergency",
                "应急",
                "事件列表、应急预案、自定义表单、应急资源",
                List.of(
                        "emergency-event",
                        "emergency-plan",
                        "emergency-custom-form",
                        "emergency-resource-map"),
                List.of(
                        "emergency",
                        "emergency_resource",
                        "emergency_team",
                        "external_rescue_resource",
                        "emergentcy_plan"),
                List.of("organization", "facility")));
        register(new BusinessCapabilityPackDefinition(
                "security-video",
                "安防",
                "人防、通信和技防页面",
                List.of(
                        "security-visitor",
                        "security-my-visitors",
                        "security-my-vehicles",
                        "security-person-location",
                        "security-personnel",
                        "security-patrol",
                        "security-phone",
                        "security-radio",
                        "security-video",
                        "security-perimeter",
                        "security-intrusion",
                        "security-access",
                        "security-access-grant",
                        "security-broadcast",
                        "security-resource",
                        "security-person",
                        "security-vehicle-gate",
                        "security-vehicle-pass",
                        "security-vehicle",
                        "security-behavior"),
                List.of(
                        "visitor",
                        "vehicle_pass",
                        "vehicle_lane",
                        "security_personnel",
                        "security_phone",
                        "security_radio",
                        "security_video",
                        "security_perimeter",
                        "security_intrusion",
                        "security_access",
                        "security_broadcast",
                        "security_resource",
                        "security_person",
                        "security_vehicle",
                        "security_behavior"),
                List.of("facility")));
        register(new BusinessCapabilityPackDefinition(
                "maintenance",
                "维修",
                "故障、报修、受理和工单",
                List.of(
                        "fault-ledger",
                        "repair-request",
                        "repair-accept",
                        "work-order-center",
                        "work-order-execute"),
                List.of(
                        "fault",
                        "maintanance",
                        "maintenance",
                        "task_maintenance",
                        "maintanence_schedule",
                        "task_record_maintenance",
                        "work_order"),
                List.of("facility")));
        register(new BusinessCapabilityPackDefinition(
                "rush-repair",
                "抢修",
                "这一次事故的抢修方案。作业许可和现场记录不在本包",
                List.of(),
                List.of("repair_scheme"),
                List.of("organization", "facility")));
        register(new BusinessCapabilityPackDefinition(
                "work-management",
                "作业管理",
                "人员资质、外部施工人员、作业许可和现场作业记录。场站作业、管线作业和抢修都走这里",
                List.of(
                        "work-person-certificate",
                        "work-external-worker",
                        "work-permit",
                        "work-field-record"),
                List.of("person_certificate", "external_worker", "work_permit", "repair_field_record"),
                List.of("organization", "facility")));
        register(new BusinessCapabilityPackDefinition(
                "equipment",
                "设备管理",
                "设备台账、备品备件和设备检查项",
                List.of("equipment-ledger", "spare-parts", "security-remote-control"),
                List.of("equipment", "spare_parts", "equipment_inspection_config", "inspection-content"),
                List.of("facility")));
        register(new BusinessCapabilityPackDefinition(
                "station",
                "智慧巡检",
                "智慧巡检租户的实时监控、站场台账、分区点位、车辆、三维场景和路网。这不是全系统通用的业务名称",
                List.of(
                        "live-monitor",
                        "station-scene",
                        "road-network",
                        "station-vehicle-register",
                        "facility-gis"),
                List.of(
                        "facility",
                        "region",
                        "zone",
                        "point",
                        "Constructure",
                        "route",
                        "route_network_point",
                        "scene",
                        "scene_placement",
                        "pipeline",
                        "pipeline_pipe",
                        "pipeline_structure",
                        "point_binding",
                        "vehicle_list",
                        "event_subscription",
                        "event_topic",
                        "department",
                        "billing",
                        "Document",
                        "task_excution_record"),
                List.of("facility")));
        register(new BusinessCapabilityPackDefinition(
                "tenant-foundation",
                "组织架构管理",
                "组织架构、角色与权限、职务、菜单、数据管理、数据资源和系统账号",
                List.of(
                        "organization",
                        "portal",
                        "menu-admin",
                        "role-permission",
                        "duty-management",
                        "catalog-admin",
                        "data-resources",
                        "management-unit",
                        "system-account"),
                List.of("user_account", "department", "duty_post", "organization")));
        register(new BusinessCapabilityPackDefinition(
                "standards",
                "标准规范",
                "标准检查库、规范标准、动作库和标准作业程序",
                List.of(),
                List.of(
                        "inspection_item",
                        "standard",
                        "action",
                        "sop",
                        "field_work_standard",
                        "field_work_standard_repair",
                        "field_work_standard_patrol",
                        "field_work_standard_upkeep",
                        "data_protocol",
                        "protocol_mapping")));
        register(new BusinessCapabilityPackDefinition(
                "intelligent-analysis",
                "智能分析",
                "分析能力、分析任务和服务接入",
                List.of("analysis-capability", "analysis-job", "analysis-provider"),
                List.of("anomaly"),
                List.of("facility", "pipeline", "pipeline_pipe")));
        register(new BusinessCapabilityPackDefinition(
                "alarm-management",
                "告警管理",
                "告警记录、告警规则、告警策略和告警联动。勾上哪一页，就开通这一页打开的那本目录",
                List.of("security-alarm", "security-alarm-rule", "alarm-strategy", "alarm-linkage"),
                List.of("alarm", "alarm_rule", "strategy_alarm", "linkage_alarm"),
                List.of("organization", "facility", "pipeline", "pipeline_pipe")));
        register(new BusinessCapabilityPackDefinition(
                "pipe-gallery",
                "管廊",
                "给智慧管廊租户用。不并进场站",
                List.of(
                        "gallery-facility",
                        "gallery-region",
                        "gallery-zone",
                        "gallery-corridor",
                        "gallery-structure",
                        "gallery-point",
                        "gallery-pipeline",
                        "gallery-point-binding",
                        "gallery-customer",
                        "gallery-billing"),
                List.of(
                        "facility",
                        "region",
                        "zone",
                        "point",
                        "pipeline",
                        "point_binding",
                        "Constructure",
                        "customer",
                        "billing"),
                List.of("facility")));
        register(new BusinessCapabilityPackDefinition(
                "platform-extensions",
                "平台配置",
                "资源库、插件和能力配置，给实施人员",
                List.of(
                        "resource-library",
                        "plugin",
                        "capability-config",
                        "station-config",
                        "gis-admin",
                        "business-list",
                        "mind-map",
                        "workflow",
                        "stats"),
                List.of()));
    }

    private BusinessCapabilityPackRegistry() {
    }

    private static void register(BusinessCapabilityPackDefinition pack) {
        BY_CODE.put(pack.code(), pack);
    }

    public static Collection<BusinessCapabilityPackDefinition> all() {
        return List.copyOf(BY_CODE.values());
    }

    public static Optional<BusinessCapabilityPackDefinition> find(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(BY_CODE.get(code.trim()));
    }

    /** 这个页面属于哪一项业务。一个页面只登记在一项业务里。 */
    public static Optional<BusinessCapabilityPackDefinition> findByPageCode(String pageCode) {
        if (pageCode == null || pageCode.isBlank()) {
            return Optional.empty();
        }
        String code = pageCode.trim();
        for (BusinessCapabilityPackDefinition pack : BY_CODE.values()) {
            if (pack.pageCodes().contains(code)) {
                return Optional.of(pack);
            }
        }
        return Optional.empty();
    }

    /**
     * 这次勾上的页面要带上哪些目录。
     * 某一页写明了目录时，只有勾上这一页才带上。
     * 没有被某一页占住的目录，这一包开着就带上。
     * 页面列表为 null 时，按这一包的全部页面。
     */
    public static List<String> catalogsForOpenedPages(BusinessCapabilityPackDefinition pack, List<String> openedPageCodes) {
        if (pack == null) {
            return List.of();
        }
        Set<String> claimed = claimedCatalogs(pack);
        List<String> catalogs = new ArrayList<>();
        for (String catalogCode : pack.catalogCodes()) {
            if (!claimed.contains(catalogCode)) {
                catalogs.add(catalogCode);
            }
        }
        List<String> pages = openedPageCodes == null ? pack.pageCodes() : openedPageCodes;
        for (String catalogCode : catalogsOfPages(pages)) {
            if (pack.catalogCodes().contains(catalogCode) && !catalogs.contains(catalogCode)) {
                catalogs.add(catalogCode);
            }
        }
        return List.copyOf(catalogs);
    }

    /** 这几页各自打开的目录。没挂目录的页面不产生目录。 */
    public static List<String> catalogsOfPages(List<String> pageCodes) {
        if (pageCodes == null || pageCodes.isEmpty()) {
            return List.of();
        }
        List<String> catalogs = new ArrayList<>();
        for (String raw : pageCodes) {
            if (raw == null || raw.isBlank()) {
                continue;
            }
            String catalogCode = PAGE_CATALOG.get(raw.trim());
            if (catalogCode != null && !catalogs.contains(catalogCode)) {
                catalogs.add(catalogCode);
            }
        }
        return List.copyOf(catalogs);
    }

    private static Set<String> claimedCatalogs(BusinessCapabilityPackDefinition pack) {
        Set<String> claimed = new LinkedHashSet<>();
        for (String pageCode : pack.pageCodes()) {
            String catalogCode = PAGE_CATALOG.get(pageCode);
            if (catalogCode != null && pack.catalogCodes().contains(catalogCode)) {
                claimed.add(catalogCode);
            }
        }
        return claimed;
    }
}
