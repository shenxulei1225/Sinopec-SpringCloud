package cn.cheers.x.module.dynamicbusiness.dataprocess;

import java.util.List;

/**
 * 原先告警配置里的类型。
 * 父类是资产告警、设备告警、任务告警、操作告警、设备故障、安防告警。子类挂在对应父类下。
 * 安防告警是视频、周界、入侵、门禁、通讯、广播和识别结果要记入的分类。
 * 人员管理和巡检不是告警类型，不建在这里。
 * 「正常」不是告警类型，不建。
 * 复核、未处理、已关闭是告警记录上的处理，不建成分类型。
 */
public final class AlarmTypeCatalog {

    public static final String DIRECTORY = "alarm";
    public static final String DIRECTORY_NAME = "告警";
    public static final String GROUP_NAME = "告警";
    public static final String MODEL_NAME = "告警记录";
    /** 质量不合格的读数记到这一类。 */
    public static final String DEVICE_MALFUNCTION = "alarm-device-malfunction";
    public static final String LEVEL_ABNORMAL = "异常告警";
    public static final String HANDLING_OPEN = "未处理";
    public static final String HANDLING_CLOSED = "已关闭";
    /** 新告警还没人看过。弹窗里改成已复核或不适用。 */
    public static final String REVIEW_PENDING = "未复核";

    public static final String FIELD_LEVEL = "告警级别";
    public static final String FIELD_CONTENT = "告警内容";
    public static final String FIELD_DEVICE = "告警设备";
    public static final String FIELD_LOCATION = "告警位置";
    public static final String FIELD_TRIGGER = "触发值";
    public static final String FIELD_THRESHOLD = "告警阈值";
    public static final String FIELD_POINT = "告警点位";
    /** 这次告警已经写下的人和车，以及各自的位置点。没有就不写。 */
    public static final String FIELD_SCENE_OBJECTS = "现场对象";
    /** 写在告警规则上。告警记下来时把当时的原文抄到告警记录，规则上没写就空着。 */
    public static final String FIELD_CAUSE = "原因";
    /** 不处理会怎样。权威在告警规则上。 */
    public static final String FIELD_CONSEQUENCE = "后果";
    /** 这条告警成立时要启动的联动。例如温度超高启动消防。权威在告警规则上。 */
    public static final String FIELD_ACTION = "联动";
    /** 旧名。启动时改成联动，不另建一列。 */
    public static final String FIELD_ACTION_PREVIOUS = "处理动作";
    /** 人需要在多长时间内处理。由人填写，不预填时长。权威在告警规则上。 */
    public static final String FIELD_RESPONSE_TIME = "允许响应时间";
    public static final String FIELD_HANDLING = "处理状态";
    /** 旧联动文本曾用这一列区分产生和消除。运行时事件阶段只认 RAISED / CLEARED。 */
    public static final String EVENT_PHASE = "告警状态";
    public static final String PHASE_RAISED = "RAISED";
    public static final String PHASE_CLEARED = "CLEARED";
    /** 弹窗里的复核。已复核、未复核、不适用。不是分类。 */
    public static final String FIELD_REVIEW = "复核状态";
    /** 产生、再次出现、关闭、人在弹窗里选的处置。一行一件事。 */
    public static final String FIELD_TRACE = "处理脉络";
    /** 操作员把这条告警搁置到这个时间。到点后按读数再决定要不要报。 */
    public static final String FIELD_SHELF_UNTIL = "搁置到";

    private AlarmTypeCatalog() {
    }

    public record Node(String code, String name, String parentCode, int sort) {
    }

    public static List<Node> nodes() {
        return List.of(
                parent("alarm-asset", "资产告警", 1),
                child("alarm-meter", "仪表", "alarm-asset", 101),
                child("alarm-drip", "滴漏", "alarm-asset", 102),
                child("alarm-leak", "漏水", "alarm-asset", 108),
                child("alarm-stranger", "陌生人", "alarm-asset", 103),
                child("alarm-helmet", "安全帽", "alarm-asset", 104),
                child("alarm-gas", "气体", "alarm-asset", 105),
                child("alarm-scale", "明标尺", "alarm-asset", 106),
                child("alarm-clothes", "工服", "alarm-asset", 107),
                parent("alarm-device", "设备告警", 2),
                child("alarm-device-damage", "设备损坏", "alarm-device", 201),
                child("alarm-device-lost", "设备失联", "alarm-device", 202),
                child("alarm-device-online", "设备是否在线", "alarm-device", 206),
                child(DEVICE_MALFUNCTION, "设备功能模块异常", "alarm-device", 203),
                child("alarm-device-trapped", "设备被困", "alarm-device", 204),
                child("alarm-device-battery", "设备电量过低", "alarm-device", 205),
                parent("alarm-task", "任务告警", 3),
                child("alarm-task-stop", "任务异常终止", "alarm-task", 301),
                child("alarm-task-start-failed", "任务启动失败", "alarm-task", 302),
                child("alarm-task-forced", "任务强行终止", "alarm-task", 303),
                parent("alarm-operation", "操作告警", 4),
                parent("alarm-device-fault", "设备故障", 5),
                child("alarm-fault-pump", "水泵故障", "alarm-device-fault", 501),
                child("alarm-fault-fan", "风机故障", "alarm-device-fault", 502),
                child("alarm-fault-hatch", "液压井盖故障", "alarm-device-fault", 503),
                parent("alarm-security", "安防告警", 6),
                child("alarm-security-video", "视频监控", "alarm-security", 601),
                child("alarm-security-perimeter", "周界报警", "alarm-security", 602),
                child("alarm-security-intrusion", "入侵检测", "alarm-security", 603),
                child("alarm-security-access", "门禁", "alarm-security", 604),
                child("alarm-security-phone", "有线电话", "alarm-security", 605),
                child("alarm-security-radio", "无线通讯", "alarm-security", 606),
                child("alarm-security-broadcast", "广播", "alarm-security", 607),
                child("alarm-security-person", "人员识别", "alarm-security", 608),
                child("alarm-security-vehicle", "车辆定位", "alarm-security", 609),
                child("alarm-security-behavior", "行为识别", "alarm-security", 610)
        );
    }

    private static Node parent(String code, String name, int sort) {
        return new Node(code, name, null, sort);
    }

    private static Node child(String code, String name, String parentCode, int sort) {
        return new Node(code, name, parentCode, sort);
    }
}
