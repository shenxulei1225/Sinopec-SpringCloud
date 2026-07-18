package cn.iocoder.yudao.module.nats.api.subjects;

/**
 * NATS Subject 统一规范
 * 禁止业务代码硬编码 Subject
 */
public class Subjects {

    private Subjects() {}

    /**
     * Device Metric Subject
     */
    public static String metric(String deviceId) {
        return String.format("device.%s.metric", deviceId);
    }

    /**
     * Inspector Location Subject
     */
    public static String location(String inspectId) {
        return String.format("inspect.%s.location", inspectId);
    }
}
