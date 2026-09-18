package cn.cheers.x.device.protocolgateway.instructiondispatch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 按对照把动作参数填进指令空包。
 * <p>负责：沿说明书已有路径写入。
 * <p>不负责：发明空包字段、回读路网、猜默认角度。
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
                throw new IllegalArgumentException("对照参数缺失：" + slot.slot());
            }
            Object value = readSource(raw, slot.sourcePath(), slot.slot());
            writeExistingPath(packet, slot.path(), value);
        }
        return packet;
    }

    static Object readSource(Object raw, String sourcePath, String slot) {
        if (sourcePath == null || sourcePath.isBlank()) {
            return raw;
        }
        if (!(raw instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("对照要取引用对象上的字段，参数不是对象：" + slot);
        }
        Object value = map.get(sourcePath);
        if (value == null) {
            throw new IllegalArgumentException("对照要取的字段不在参数里：" + slot + "." + sourcePath);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    static void writeExistingPath(Map<String, Object> root, String path, Object value) {
        String[] parts = path.split("\\.");
        Map<String, Object> cursor = root;
        for (int i = 0; i < parts.length - 1; i++) {
            Object next = cursor.get(parts[i]);
            if (!(next instanceof Map<?, ?> map)) {
                throw new IllegalArgumentException("空包没有路径：" + path);
            }
            cursor = (Map<String, Object>) map;
        }
        String leaf = parts[parts.length - 1];
        if (!cursor.containsKey(leaf)) {
            throw new IllegalArgumentException("空包没有路径：" + path);
        }
        cursor.put(leaf, value);
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
            } else {
                copy.put(entry.getKey(), value);
            }
        }
        return copy;
    }
}
