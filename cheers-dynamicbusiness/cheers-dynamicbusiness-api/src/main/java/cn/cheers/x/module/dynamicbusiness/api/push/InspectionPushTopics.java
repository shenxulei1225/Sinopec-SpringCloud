package cn.cheers.x.module.dynamicbusiness.api.push;

import java.util.List;

/**
 * 巡检页面主题的名字规则。目录建行和发出时都用这一份。
 * 占位在发出时填。不按每台设备、每一次执行再造一行。
 * 不负责：把消息送出去、按目录里改过的字再查一遍。
 */
public final class InspectionPushTopics {

    public record Topic(String code, String name, String namePattern) {
    }

    /** 实时画面上的飞机状态。跟这台设备走，不记执行过程。 */
    public static final Topic DEVICE_STATUS = new Topic(
            "inspection_device_status_snapshot",
            "设备实时状态",
            "inspection/device/{设备编号}/status");
    /** 巡检图层上的当前位置。 */
    public static final Topic TRAJECTORY = new Topic(
            "inspection_execution_trajectory",
            "巡检轨迹",
            "inspection/execution/{执行记录编号}/trajectory");
    /** 巡检图层上的步骤完成情况。 */
    public static final Topic STEP = new Topic(
            "inspection_execution_step",
            "巡检步骤结果",
            "inspection/execution/{执行记录编号}/step");
    /** 这一次执行的整次状态。 */
    public static final Topic RUN = new Topic(
            "inspection_execution_run",
            "巡检整次状态",
            "inspection/execution/{执行记录编号}/run");
    /** 巡检任务指令包已经发出，或设备已经收下。窗口里仍是同一行。 */
    public static final Topic COMMAND_ACCEPTED = new Topic(
            "inspection_command_accepted",
            "指令包已收下",
            "inspection/execution/{执行记录编号}/command-accepted");
    /** 启动已下发任务。 */
    public static final Topic START_ACCEPTED = new Topic(
            "inspection_start_accepted",
            "启动请求已收下",
            "inspection/execution/{执行记录编号}/start-accepted");
    /** 终止任务。 */
    public static final Topic STOP_ACCEPTED = new Topic(
            "inspection_stop_accepted",
            "终止请求已收下",
            "inspection/execution/{执行记录编号}/stop-accepted");
    /** 打开或关闭定时上报之后，设备收下了这次开关。 */
    public static final Topic REPORT_ACCEPTED = new Topic(
            "inspection_report_accepted",
            "定时上报请求已收下",
            "inspection/device/{设备编号}/report-accepted");
    /** 设备故障。跟这台设备走。 */
    public static final Topic DEVICE_FAULT = new Topic(
            "inspection_device_fault",
            "设备故障",
            "inspection/device/{设备编号}/fault");
    /** 取回已经收下的指令包。 */
    public static final Topic COMMAND_PACKAGE = new Topic(
            "inspection_command_package",
            "指令包内容",
            "inspection/device/{设备编号}/package");
    /** 删除指令包。 */
    public static final Topic COMMAND_PACKAGE_DELETED = new Topic(
            "inspection_command_package_deleted",
            "指令包已删除",
            "inspection/device/{设备编号}/package-deleted");
    /** 列出任务模板。 */
    public static final Topic COMMAND_PACKAGE_CATALOG = new Topic(
            "inspection_command_package_catalog",
            "任务模板列表",
            "inspection/device/{设备编号}/package-catalog");
    /** 启动推流已被设备收下。 */
    public static final Topic LIVE_START_ACCEPTED = new Topic(
            "inspection_live_start_accepted",
            "启动推流已收下",
            "inspection/device/{设备编号}/live-start-accepted");
    /** 停止推流已被设备收下。 */
    public static final Topic LIVE_STOP_ACCEPTED = new Topic(
            "inspection_live_stop_accepted",
            "停止推流已收下",
            "inspection/device/{设备编号}/live-stop-accepted");
    /** 设备在线或掉线。 */
    public static final Topic DEVICE_PRESENCE = new Topic(
            "inspection_device_presence",
            "在线或掉线",
            "inspection/device/{设备编号}/presence");

    public static final List<Topic> ALL = List.of(
            DEVICE_STATUS,
            TRAJECTORY,
            STEP,
            RUN,
            COMMAND_ACCEPTED,
            START_ACCEPTED,
            STOP_ACCEPTED,
            REPORT_ACCEPTED,
            DEVICE_FAULT,
            COMMAND_PACKAGE,
            COMMAND_PACKAGE_DELETED,
            COMMAND_PACKAGE_CATALOG,
            LIVE_START_ACCEPTED,
            LIVE_STOP_ACCEPTED,
            DEVICE_PRESENCE);

    private InspectionPushTopics() {
    }

    /**
     * 这条主题给人看的名称。没有这一条时返回空。
     */
    public static String name(String code) {
        Topic topic = find(code);
        return topic == null ? null : topic.name;
    }

    /**
     * 这条主题已经存好的名字规则。没有这一条时返回空，调用方不另编主题。
     */
    public static String pattern(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        Topic topic = find(code);
        return topic == null ? null : topic.namePattern;
    }

    private static Topic find(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        for (Topic topic : ALL) {
            if (topic.code.equals(code)) {
                return topic;
            }
        }
        return null;
    }
}
