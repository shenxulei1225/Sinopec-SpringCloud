package cn.cheers.x.device.protocolgateway.protocol.monitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 把下发空包里已经有、还空着的格子填上。
 * <p>负责：复制空包，再填设备编号、消息编号、占位时间戳，以及任务指令里空着的模板编号、任务编号、执行时间。
 * 定时上报的开关、频率和重复次数由页面选定。空包里的 false 和 0 是没填的占位，按下发时页面上的值改写。
 * 启动推流的地址也由页面选定，写进空包里已有的推流地址格。
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
        fillZero(packet, "time");
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

    /**
     * 按页面选定的开关、频率和重复次数填写定时上报。空包没有开关时不能发。
     * 启动时频率必须大于 0，重复次数必须是正整数或 -1。页面填了的数直接写入，不保留空包里的 0。
     */
    public static void applyReportSwitch(
            Map<String, Object> packet, boolean enable, Integer frequency, Integer total) {
        if (packet == null || !packet.containsKey("enable")) {
            throw new IllegalArgumentException("这条指令空包没有开关，无法下发");
        }
        if (frequency != null && frequency <= 0) {
            throw new IllegalArgumentException("上报频率要填大于 0 的秒数");
        }
        if (total != null && (total == 0 || total < -1)) {
            throw new IllegalArgumentException("重复次数要填正整数，-1 表示不限次数");
        }
        if (enable && (frequency == null || total == null)) {
            throw new IllegalArgumentException("启动定时上报要填写频率和重复次数");
        }
        packet.put("enable", enable);
        writeCount(packet, "frequency", frequency);
        writeCount(packet, "total", total);
    }

    /**
     * 把页面上的推流地址写进空包已有的地址格。空包没有这一格时不能发。地址空着也不能发。
     * 地面站启动推流写 {@code rtmp}，开始录像写 {@code request.rstp}，大疆开始直播写 {@code data.url}。
     */
    public static void applyStreamAddress(Map<String, Object> packet, String streamUrl) {
        if (packet == null || !hasStreamAddressSlot(packet)) {
            throw new IllegalArgumentException("这条指令空包没有推流地址，无法下发");
        }
        if (streamUrl == null || streamUrl.isBlank()) {
            throw new IllegalArgumentException("启动推流要填写推流地址");
        }
        String url = streamUrl.trim();
        if (packet.containsKey("rtmp")) {
            packet.put("rtmp", url);
        }
        writeNested(packet, "request", "rstp", url);
        writeNested(packet, "data", "url", url);
    }

    public static boolean isInnerStep(Map<String, Object> outbound) {
        return outbound != null && outbound.get("request") instanceof Map<?, ?>;
    }

    private static boolean hasStreamAddressSlot(Map<String, Object> packet) {
        if (packet.containsKey("rtmp")) {
            return true;
        }
        return nestedHas(packet, "request", "rstp") || nestedHas(packet, "data", "url");
    }

    private static boolean nestedHas(Map<String, Object> packet, String objectKey, String field) {
        return packet.get(objectKey) instanceof Map<?, ?> nested && nested.containsKey(field);
    }

    @SuppressWarnings("unchecked")
    private static void writeNested(Map<String, Object> packet, String objectKey, String field, String url) {
        if (!(packet.get(objectKey) instanceof Map<?, ?> nested) || !nested.containsKey(field)) {
            return;
        }
        ((Map<String, Object>) nested).put(field, url);
    }

    private static void writeCount(Map<String, Object> packet, String key, Integer value) {
        if (value == null || !packet.containsKey(key)) {
            return;
        }
        packet.put(key, value);
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
