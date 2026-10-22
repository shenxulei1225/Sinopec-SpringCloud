package cn.cheers.x.device.protocolgateway.datacollection;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.datacollection.ProtocolQualifyStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 按字段说明核一份已解析的采集样本。
 * <p>负责：必填、类型、路径结构。字段说明里的键名和报文里的键名只差大小写时，仍算这一格。
 * <p>不负责：读说明书、判检查项、补缺字段。
 * <p>禁止：报文多出来的键当不合格；缺说明假装合格；往报文里再写一个大小写不同的键。
 */
public final class FieldDescriptionQualifier {

    private FieldDescriptionQualifier() {
    }

    /**
     * 已是不合格（正文都不是对象）的样本不再改。
     * 说明书对不上 → 不合格并写原因；对上 → 合格。
     */
    public static CollectionSample qualify(CollectionSample sample, CatalogLookup lookup) {
        if (sample.protocolQualify() == ProtocolQualifyStatus.UNQUALIFIED) {
            return sample;
        }
        if (lookup == null || !lookup.ok()) {
            String reason = lookup == null || lookup.error() == null
                    ? "找不到该指令的字段说明"
                    : lookup.error();
            return withQualify(sample, ProtocolQualifyStatus.UNQUALIFIED, List.of(reason));
        }
        List<String> errors = collectErrors(sample.fields(), lookup.rows());
        if (!errors.isEmpty()) {
            return withQualify(sample, ProtocolQualifyStatus.UNQUALIFIED, errors);
        }
        return withQualify(sample, ProtocolQualifyStatus.QUALIFIED, List.of());
    }

    /**
     * 这一行和报文对不上时返回原因。可选字段空着不算错。
     * 监控页用它把错误标在对应字段上，不另写一套核对。
     */
    public static String describeIssue(Map<String, Object> fields, FieldDescriptionRow row) {
        if (arrayPresentButEmpty(fields, row.path())) {
            return null;
        }
        List<Object> values = valuesAtPath(fields, row.path());
        if (values.isEmpty()) {
            return row.required() ? "缺必填" : null;
        }
        for (Object value : values) {
            if (isMissingValue(value, row.type())) {
                return row.required() ? "缺必填" : null;
            }
            if (!typeMatches(value, row.type())) {
                return "类型不对，应为" + row.type();
            }
        }
        return null;
    }

    static List<String> collectErrors(Map<String, Object> fields, List<FieldDescriptionRow> rows) {
        List<String> errors = new ArrayList<>();
        for (FieldDescriptionRow row : rows) {
            if (arrayPresentButEmpty(fields, row.path())) {
                continue;
            }
            List<Object> values = valuesAtPath(fields, row.path());
            if (values.isEmpty()) {
                if (row.required()) {
                    errors.add("缺必填：" + row.label() + "（" + row.path() + "）");
                }
                continue;
            }
            for (Object value : values) {
                if (isMissingValue(value, row.type())) {
                    if (row.required()) {
                        errors.add("缺必填：" + row.label() + "（" + row.path() + "）");
                    }
                    continue;
                }
                if (!typeMatches(value, row.type())) {
                    errors.add("类型不对：" + row.label() + "（" + row.path() + "）应为" + row.type());
                }
            }
        }
        return errors;
    }

    /**
     * 按路径取出所有命中值。{@code photos[]} 表示数组每一项再往下走。
     */
    public static List<Object> valuesAtPath(Object root, String path) {
        List<Object> current = new ArrayList<>();
        current.add(root);
        for (String rawPart : path.split("\\.")) {
            boolean each = rawPart.endsWith("[]");
            String key = each ? rawPart.substring(0, rawPart.length() - 2) : rawPart;
            List<Object> next = new ArrayList<>();
            for (Object node : current) {
                collectChildren(node, key, each, next);
            }
            current = next;
            if (current.isEmpty()) {
                return List.of();
            }
        }
        return current;
    }

    /**
     * 按字段说明里的键名取报文对象上的这一格。
     * 大小写不同仍取到。同时有两种写法、又没有和说明完全一致的那个键时不取，这一格仍按缺了处理。
     */
    public static Object valueAtKey(Map<?, ?> map, String key) {
        String matched = matchKey(map, key);
        return matched == null ? null : map.get(matched);
    }

    /** 这一格在不在。值是空的也算在，只是大小写不同也算在。两种写法同时在、又没有完全同名的键时不算在。 */
    public static boolean hasKey(Map<?, ?> map, String key) {
        return matchKey(map, key) != null;
    }

    private static String matchKey(Map<?, ?> map, String key) {
        if (map == null || key == null) {
            return null;
        }
        if (map.containsKey(key)) {
            return key;
        }
        String found = null;
        for (Object raw : map.keySet()) {
            if (!(raw instanceof String name) || !name.equalsIgnoreCase(key)) {
                continue;
            }
            if (found != null) {
                return null;
            }
            found = name;
        }
        return found;
    }

    private static void collectChildren(Object node, String key, boolean each, List<Object> next) {
        if (!(node instanceof Map<?, ?> map)) {
            return;
        }
        Object child = valueAtKey(map, key);
        if (child == null) {
            return;
        }
        if (each) {
            if (child instanceof Collection<?> collection) {
                next.addAll(collection);
            }
            return;
        }
        next.add(child);
    }

    /**
     * 路径里带 {@code []}，且对应列表键已经在、只是还没有行。
     * 这时行内必填先不标。有行之后，每一行仍按必填核对。
     */
    private static boolean arrayPresentButEmpty(Object root, String path) {
        if (path == null || !path.contains("[]")) {
            return false;
        }
        Object node = root;
        for (String rawPart : path.split("\\.")) {
            if (!(node instanceof Map<?, ?> map)) {
                return false;
            }
            boolean each = rawPart.endsWith("[]");
            String key = each ? rawPart.substring(0, rawPart.length() - 2) : rawPart;
            Object child = valueAtKey(map, key);
            if (each) {
                return child instanceof Collection<?> collection && collection.isEmpty();
            }
            node = child;
        }
        return false;
    }

    private static boolean isMissingValue(Object value, String type) {
        if (value == null) {
            return true;
        }
        if ("string".equals(type) && value instanceof CharSequence text) {
            return text.toString().isBlank();
        }
        return false;
    }

    private static boolean typeMatches(Object value, String type) {
        return switch (type) {
            case "integer", "long" -> isIntegral(value);
            case "number" -> value instanceof Number;
            case "boolean" -> value instanceof Boolean;
            case "array" -> value instanceof Collection<?> || value instanceof Object[];
            case "object" -> value instanceof Map<?, ?>;
            default -> value instanceof CharSequence;
        };
    }

    private static boolean isIntegral(Object value) {
        if (value instanceof Integer || value instanceof Long || value instanceof Short) {
            return true;
        }
        if (value instanceof Number number) {
            return number.doubleValue() == Math.rint(number.doubleValue());
        }
        return false;
    }

    private static CollectionSample withQualify(
            CollectionSample sample,
            ProtocolQualifyStatus status,
            List<String> errors
    ) {
        return new CollectionSample(
                sample.channelCode(),
                sample.deviceId(),
                sample.messageKind(),
                status,
                errors,
                sample.fields(),
                sample.executionRecordId(),
                sample.msgId(),
                sample.receivedAtEpochMs()
        );
    }
}
