package cn.cheers.x.device.protocolgateway.instructiondispatch;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 按对照把任务冻结时已经解析好的来源值填进指令空包。
 * <p>负责：沿说明书已有路径写入。
 * <p>不负责：回读界面操作、执行设备或资源台账；发明空包字段、猜默认角度。
 * <p>禁止：空包没有的路径硬造一层；参数缺了还当填成功。
 */
public final class InstructionPacketFiller {

    private InstructionPacketFiller() {
    }

    /**
     * 返回填好的空包副本。对照路径必须已在空包里。
     */
    public static Map<String, Object> fill(
            Map<String, Object> outbound,
            List<SlotMapping> slots,
            Map<String, Object> params
    ) {
        Map<String, Object> packet = deepCopy(outbound);
        if (slots == null || slots.isEmpty()) {
            return packet;
        }
        Map<String, Object> bag = params == null ? Map.of() : params;
        for (SlotMapping slot : slots) {
            Object raw = bag.get(slot.slot());
            if (raw == null) {
                throw new IllegalArgumentException(
                        "这一步缺少" + sourceLabel(slot.sourceKind()) + "来源字段「" + slot.slot() + "」");
            }
            Object value = readSource(raw, slot.sourcePath(), slot.slot());
            writeExistingPath(packet, slot.path(), value);
        }
        return packet;
    }

    private static String sourceLabel(String sourceKind) {
        return switch (sourceKind) {
            case "INTERACTION" -> "界面操作";
            case "EXECUTOR_MOUNT" -> "执行设备";
            case "SYSTEM" -> "系统生成";
            case "REFERENCE_RESOURCE" -> "资源文件";
            case "ACTION_PARAM" -> "动作参数";
            default -> "动作参数";
        };
    }

    /**
     * sourcePath 按点号逐层取。对照里写的是「选择位置.坐标.经度」这种路径，不是把整段当成一个键。
     */
    static Object readSource(Object raw, String sourcePath, String slot) {
        if (sourcePath == null || sourcePath.isBlank()) {
            return raw;
        }
        Object cursor = raw;
        for (String part : sourcePath.split("\\.")) {
            if (!(cursor instanceof Map<?, ?> map)) {
                throw new IllegalArgumentException("参数「" + slot + "」不是对象，取不到「" + sourcePath + "」");
            }
            if (!map.containsKey(part) || map.get(part) == null) {
                throw new IllegalArgumentException("参数「" + slot + "」里没有「" + sourcePath + "」");
            }
            cursor = map.get(part);
        }
        return cursor;
    }

    /**
     * 只写空包里已经有的路径。飞向目标点的经纬高在目标点列表的第一项，
     * 路径写成 data.points.0.latitude。列表或这一项不存在就失败，不另造列表。
     */
    static void writeExistingPath(Map<String, Object> root, String path, Object value) {
        String[] parts = path.split("\\.");
        Object cursor = root;
        for (int i = 0; i < parts.length - 1; i++) {
            cursor = existingChild(cursor, parts[i], path);
        }
        String leaf = parts[parts.length - 1];
        if (cursor instanceof Map<?, ?> map) {
            if (!map.containsKey(leaf)) {
                throw new IllegalArgumentException("空包没有路径「" + path + "」");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> parent = (Map<String, Object>) map;
            parent.put(leaf, asInstructionText(value));
            return;
        }
        throw new IllegalArgumentException("空包没有路径「" + path + "」");
    }

    private static Object existingChild(Object cursor, String part, String path) {
        if (cursor instanceof Map<?, ?> map) {
            if (!map.containsKey(part)) {
                throw new IllegalArgumentException("空包没有路径「" + path + "」");
            }
            return map.get(part);
        }
        if (cursor instanceof List<?> list && isListIndex(part)) {
            int index = Integer.parseInt(part);
            if (index < 0 || index >= list.size()) {
                throw new IllegalArgumentException("空包没有路径「" + path + "」");
            }
            return list.get(index);
        }
        throw new IllegalArgumentException("空包没有路径「" + path + "」");
    }

    private static boolean isListIndex(String part) {
        if (part.isEmpty()) {
            return false;
        }
        for (int i = 0; i < part.length(); i++) {
            if (!Character.isDigit(part.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 动作上的数、是否，写进指令时都存成字符串。
     * 协议请求字段不再另标整数或布尔，填包时不用再做一套类型转换。
     * 操作码、序号不走这里，它们留在空包里，不是动作参数。
     */
    static Object asInstructionText(Object value) {
        if (value instanceof String || value == null) {
            return value;
        }
        if (value instanceof Boolean || value instanceof Number) {
            return String.valueOf(value);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> deepCopy(Map<String, Object> source) {
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
