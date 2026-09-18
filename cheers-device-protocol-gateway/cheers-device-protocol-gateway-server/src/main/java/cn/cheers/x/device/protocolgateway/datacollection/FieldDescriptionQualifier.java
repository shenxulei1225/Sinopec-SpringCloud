package cn.cheers.x.device.protocolgateway.datacollection;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.datacollection.ProtocolQualifyStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 按字段说明核一份已解析的采集样本。
 * <p>负责：必填、类型、路径结构。
 * <p>不负责：读说明书、判检查项、补缺字段。
 * <p>禁止：报文多出来的键当不合格；缺说明假装合格。
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

    static List<String> collectErrors(Map<String, Object> fields, List<FieldDescriptionRow> rows) {
        List<String> errors = new ArrayList<>();
        for (FieldDescriptionRow row : rows) {
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
    static List<Object> valuesAtPath(Object root, String path) {
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

    private static void collectChildren(Object node, String key, boolean each, List<Object> next) {
        if (!(node instanceof Map<?, ?> map)) {
            return;
        }
        Object child = map.get(key);
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

    private static boolean isMissingValue(Object value, String type) {
        if (value == null) {
            return true;
        }
        if ("string".equals(type) && value instanceof CharSequence text) {
            return text.toString().isBlank();
        }
        if ("array".equals(type) && value instanceof Collection<?> collection) {
            return collection.isEmpty();
        }
        if ("object".equals(type) && value instanceof Map<?, ?> map) {
            return map.isEmpty();
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
