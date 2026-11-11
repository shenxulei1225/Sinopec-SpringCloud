package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 首批业务能力包目录（与 docs/平台管理/租户管理/业务能力包清单（权威）.md 对齐）。
 * 负责：按编码取出包定义。不负责：租户开通持久化。
 */
public final class BusinessCapabilityPackRegistry {

    private static final Map<String, BusinessCapabilityPackDefinition> BY_CODE = new LinkedHashMap<>();

    static {
        register(new BusinessCapabilityPackDefinition(
                "tenant-foundation",
                "租户底座",
                "组织、门户、角色与权限、数据管理、系统账号",
                List.of("organization", "portal", "role-permission", "catalog-admin", "system-account"),
                List.of("user_account", "department", "duty_post")));
        register(new BusinessCapabilityPackDefinition(
                "station-live-monitor",
                "场站实时监控",
                "三维监控画面与下发操作",
                List.of("live-monitor"),
                List.of("scene", "scene_placement", "event_topic", "event_subscription", "facility")));
        register(new BusinessCapabilityPackDefinition(
                "inspection",
                "巡检",
                "巡检任务、排期与执行记录",
                List.of("inspection-task", "inspection-schedule"),
                List.of(
                        "task_patrol",
                        "task_record_patrol",
                        "task_execution_step",
                        // 早期目录 patrol_schedule / patrol_route 已废：排期与路线都写在任务上
                        "patrol_target",
                        "patrol_equipment",
                        "schedule",
                        "inspection_item",
                        "inspection-content",
                        "equipment_inspection_config",
                        "action",
                        "sop",
                        "task")));
        register(new BusinessCapabilityPackDefinition(
                "emergency",
                "应急",
                "应急事件办理、预案与自定义表单；资源与响应台账目录随包开通",
                List.of(
                        "emergency-event",
                        "emergency-plan",
                        "emergency-custom-form"),
                List.of("emergency", "emergency_resource", "emergency_response", "emergentcy_plan")));
        register(new BusinessCapabilityPackDefinition(
                "intelligent-analysis",
                "智能分析",
                "分析能力、任务与服务接入",
                List.of("analysis-capability", "analysis-job", "analysis-provider"),
                List.of("alarm", "alarm_rule", "strategy_alarm", "linkage", "linkage_alarm", "anomaly")));
        register(new BusinessCapabilityPackDefinition(
                "facility-ops",
                "场站资产与运维",
                "这座站的空间与资产台账、运维与 GIS；不含客户收费与过程台账",
                List.of(
                        "road-network",
                        "facility-gis",
                        "equipment-ledger",
                        "spare-parts",
                        "fault-ledger",
                        "repair-request",
                        "repair-accept",
                        "station-vehicle-register",
                        "work-order-center",
                        "work-order-execute"),
                List.of(
                        "facility",
                        "region",
                        "zone",
                        "point",
                        "Constructure",
                        "pipeline",
                        "point_binding",
                        "equipment",
                        "spare_parts",
                        "equipment_inspection_config",
                        "route",
                        "route_network_point",
                        "fault",
                        "company_vehicle",
                        "vehicle_dispatch",
                        "maintanance",
                        "maintenance",
                        "task_maintenance",
                        "maintanence_schedule",
                        "work_order",
                        "scene",
                        "scene_placement")));
        register(new BusinessCapabilityPackDefinition(
                "pipe-gallery",
                "管廊",
                "管廊设施、分区、管线与点位对照",
                List.of(
                        "gallery-facility",
                        "gallery-region",
                        "gallery-zone",
                        "gallery-structure",
                        "gallery-point",
                        "gallery-pipeline",
                        "gallery-point-binding"),
                List.of("facility", "region", "zone", "point", "pipeline", "point_binding", "Constructure")));
        register(new BusinessCapabilityPackDefinition(
                "standards",
                "标准规范",
                "标准检查库、规范、动作、SOP、作业标准与协议",
                List.of(),
                List.of(
                        "inspection_item",
                        "standard",
                        "action",
                        "sop",
                        // 配置页 /standards/field-work 打开 NATIVE 作业标准；DOMAIN 入口随主目录一并授目录权限
                        "field_work_standard",
                        "field_work_standard_repair",
                        "field_work_standard_patrol",
                        "field_work_standard_upkeep",
                        "data_protocol",
                        "protocol_mapping")));
        register(new BusinessCapabilityPackDefinition(
                "security-video",
                "安防视频",
                "开通时在数据管理左侧建立分组「安防管理」。顶栏「安防」按人防、通信、技防分组。人防是访客登记、我的访客和人员定位。访客登记给门卫刷二代身份证，打印后面放行。被访人在我的访客里同意。人员定位打开这一座站的场景看人。车辆进出按车道上的车牌识别写下进出。门岗另开一页处理过车。车辆登记在场站管理，不在安防菜单。电话、视频、周界、入侵、门禁、广播、安全资源位置使用设备账。人员识别、车辆定位、行为识别使用告警账。告警记录和告警规则仍是告警目录、告警规则目录。实时告警仍由场站告警图层订阅。",
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
                        "security-broadcast",
                        "security-resource",
                        "security-person",
                        "security-vehicle-gate",
                        "security-vehicle-pass",
                        "security-vehicle",
                        "security-behavior",
                        "security-alarm",
                        "security-alarm-rule"),
                List.of(
                        "visitor",
                        "vehicle_pass",
                        "vehicle_list",
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
                        "security_behavior",
                        "alarm",
                        "alarm_rule")));
        register(new BusinessCapabilityPackDefinition(
                "platform-extensions",
                "平台扩展配置",
                "资源库、插件、能力配置等实施侧页面",
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
}
