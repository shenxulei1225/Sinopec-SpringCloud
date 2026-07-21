package cn.iocoder.yudao.module.emergency.service.process;

/**
 * 应急指挥流程定义与业务键约定（B2-A）。
 */
public final class EmergencyProcessKeys {

    public static final String DEFINITION_KEY = "emergency_command_v1";

    public static final String TASK_CONFIRM = "event_confirm";

    public static final String TASK_ASSESS = "event_assess";

    private EmergencyProcessKeys() {
    }

    public static String businessKey(Long eventId) {
        return "emergency-event:" + eventId;
    }

}
