package cn.cheers.x.device.protocolgateway.protocol.monitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 把下发空包里已经有、还空着的格子填上。
 * <p>负责：复制空包，再填设备编号、消息编号，以及任务指令里空着的模板编号、任务编号、执行时间。
 * <p>不负责：把台账上的其它列抄进报文；给空包补它没有的键。
 * <p>模板编号和任务编号用协议样例里的值，这样巡检指令包、启动、取包、删包、列出模板能对上同一份任务。
 */
public final class ProtocolMonitorDownlinkFiller {

    private ProtocolMonitorDownlinkFiller() {
    }

    public static Map<String, Object> fillIdentity(Map<String, Object> outbound, String deviceId, String msgId) {
        Map<String, Object> packet = deepCopy(outbound);
        fillBlank(packet, "deviceId", deviceId);
        fillBlank(packet, "msgId", msgId);
        return packet;
    }

    /**
     * 任务指令空包里已有的模板编号、任务编号、执行时间还空着时，填上协议样例。
     * 空包没有这些键时不补。已经写了值的格子保持原样。
     */
    public static Map<String, Object> fillBlankTaskSlots(Map<String, Object> outbound) {
        Map<String, Object> packet = deepCopy(outbound);
        fillBlank(packet, "templateId", "100111");
        fillBlank(packet, "taskId", "111122223301");
        fillZero(packet, "executionTime");
        return packet;
    }

    public static boolean isInnerStep(Map<String, Object> outbound) {
        return outbound != null && outbound.get("request") instanceof Map<?, ?>;
    }

    private static void fillZero(Map<String, Object> packet, String key) {
        if (packet == null || !packet.containsKey(key)) {
            return;
        }
        Object current = packet.get(key);
        if (current instanceof Number number && number.longValue() == 0L) {
            packet.put(key, System.currentTimeMillis() / 1000);
        }
    }

    private static void fillBlank(Map<String, Object> packet, String key, String value) {
        if (packet == null || value == null || value.isBlank() || !packet.containsKey(key)) {
            return;
        }
        Object current = packet.get(key);
        if (current == null || (current instanceof String text && text.isBlank())) {
            packet.put(key, value);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> deepCopy(Map<String, Object> source) {
        Map<String, Object> copy = new LinkedHashMap<>();
        if (source == null) {
            return copy;
        }
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map<?, ?> map) {
                copy.put(entry.getKey(), deepCopy((Map<String, Object>) map));
            } else if (value instanceof List<?> list) {
                copy.put(entry.getKey(), deepCopyList(list));
            } else {
                copy.put(entry.getKey(), value);
            }
        }
        return copy;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> deepCopyList(List<?> source) {
        List<Object> copy = new ArrayList<>();
        for (Object item : source) {
            if (item instanceof Map<?, ?> map) {
                copy.add(deepCopy((Map<String, Object>) map));
            } else if (item instanceof List<?> list) {
                copy.add(deepCopyList(list));
            } else {
                copy.add(item);
            }
        }
        return copy;
    }
}
