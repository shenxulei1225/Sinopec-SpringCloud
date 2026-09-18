package cn.cheers.x.module.dynamicbusiness.service.strategy;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 第一期已登记、可被策略勾选的七件事。
 */
public final class RegisteredActions {

    public static final String CREATE_EXECUTION = "CREATE_EXECUTION";
    public static final String DISPATCH_TO_DEVICE = "DISPATCH_TO_DEVICE";
    public static final String UPDATE_EXECUTION_STATUS = "UPDATE_EXECUTION_STATUS";
    public static final String UPDATE_STEP_STATUS = "UPDATE_STEP_STATUS";
    public static final String APPEND_PROCESS = "APPEND_PROCESS";
    public static final String RAISE_ALARM = "RAISE_ALARM";
    public static final String SEND_NOTIFICATION = "SEND_NOTIFICATION";

    private RegisteredActions() {
    }

    public static String displayName(String actionCode) {
        if (CREATE_EXECUTION.equals(actionCode)) {
            return "新建这次执行的账";
        }
        if (DISPATCH_TO_DEVICE.equals(actionCode)) {
            return "把任务发给设备";
        }
        if (UPDATE_EXECUTION_STATUS.equals(actionCode)) {
            return "更新这次执行的状态";
        }
        if (UPDATE_STEP_STATUS.equals(actionCode)) {
            return "更新某一步的状态";
        }
        if (APPEND_PROCESS.equals(actionCode)) {
            return "往执行账里记一条过程";
        }
        if (RAISE_ALARM.equals(actionCode)) {
            return "新增一条告警";
        }
        if (SEND_NOTIFICATION.equals(actionCode)) {
            return "给相关人员发通知";
        }
        return actionCode;
    }

    public static Map<String, String> catalog() {
        Map<String, String> catalog = new LinkedHashMap<>();
        catalog.put(CREATE_EXECUTION, displayName(CREATE_EXECUTION));
        catalog.put(DISPATCH_TO_DEVICE, displayName(DISPATCH_TO_DEVICE));
        catalog.put(UPDATE_EXECUTION_STATUS, displayName(UPDATE_EXECUTION_STATUS));
        catalog.put(UPDATE_STEP_STATUS, displayName(UPDATE_STEP_STATUS));
        catalog.put(APPEND_PROCESS, displayName(APPEND_PROCESS));
        catalog.put(RAISE_ALARM, displayName(RAISE_ALARM));
        catalog.put(SEND_NOTIFICATION, displayName(SEND_NOTIFICATION));
        return catalog;
    }

    public static boolean isKnown(String actionCode) {
        return catalog().containsKey(actionCode);
    }
}
