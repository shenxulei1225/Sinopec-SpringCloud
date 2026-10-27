package cn.cheers.x.device.protocolgateway.datacollection;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 把核过的设备报文收成标准事件。
 * <p>负责：写了转发到主题时，报文种类改成该主题。合格包按转发名填字段。不合格包字段留空，错误留在样本上。
 * <p>不负责：写巡检账、判断步骤完成、没有转发名时改用设备路径。
 * <p>禁止：没有转发到主题时把操作码当成标准事件；不合格时把设备路径留在样本里。
 */
public final class StandardEventForwarder {

    private StandardEventForwarder() {
    }

    /**
     * 说明书没有写转发到主题时，样本保持原报文种类。
     * 不合格时字段留空。合格时只放入写了转发名的格子。缺了的格子不补。
     */
    public static CollectionSample apply(CollectionSample sample, CatalogLookup lookup) {
        if (sample == null || lookup == null || !lookup.ok()) {
            return sample;
        }
        String topic = lookup.forwardTopic();
        if (topic == null || topic.isBlank()) {
            return sample;
        }
        if (!sample.isProtocolQualified()) {
            return sample.asTopic(topic, Map.of());
        }
        Map<String, Object> forwarded = project(sample.fields(), lookup.rows());
        if (forwarded.isEmpty()) {
            return sample;
        }
        return new CollectionSample(
                sample.channelCode(),
                sample.deviceId(),
                topic,
                sample.protocolQualify(),
                sample.qualifyErrors(),
                forwarded,
                sample.executionRecordId(),
                sample.msgId(),
                sample.receivedAtEpochMs());
    }

    public static Map<String, Object> project(Map<String, Object> fields, List<FieldDescriptionRow> rows) {
        Map<String, Object> forwarded = new LinkedHashMap<>();
        if (fields == null || rows == null) {
            return forwarded;
        }
        List<FieldDescriptionRow> containers = new ArrayList<>();
        for (FieldDescriptionRow row : rows) {
            if (row.forwardName().isBlank()) {
                continue;
            }
            if (isArrayContainer(row, rows)) {
                containers.add(row);
                continue;
            }
            if (row.path().contains("[]") || isChildOfContainer(row, rows)) {
                continue;
            }
            List<Object> values = FieldDescriptionQualifier.valuesAtPath(fields, row.path());
            if (values.isEmpty()) {
                continue;
            }
            forwarded.put(row.forwardName(), values.size() == 1 ? values.get(0) : List.copyOf(values));
        }
        for (FieldDescriptionRow container : containers) {
            List<Map<String, Object>> items = projectItems(fields, container, rows);
            if (!items.isEmpty()) {
                forwarded.put(container.forwardName(), items);
            }
        }
        return forwarded;
    }

    /**
     * 这一行指向整段列表，后面还有行内转发名。
     * 行内路径写成 {@code packages[].sequence} 或 {@code packages.sequence} 都算。
     */
    private static boolean isArrayContainer(FieldDescriptionRow row, List<FieldDescriptionRow> rows) {
        if (row.path().contains("[]") || row.forwardName().isBlank()) {
            return false;
        }
        for (FieldDescriptionRow other : rows) {
            if (childRest(row.path(), other.path()) != null) {
                return true;
            }
        }
        return false;
    }

    private static boolean isChildOfContainer(FieldDescriptionRow row, List<FieldDescriptionRow> rows) {
        for (FieldDescriptionRow container : rows) {
            if (container == row || container.forwardName().isBlank()) {
                continue;
            }
            if (childRest(container.path(), row.path()) != null && isArrayContainer(container, rows)) {
                return true;
            }
        }
        return false;
    }

    /** 行内路径相对列表行的后半段。对不上则不是这一列表的行。 */
    private static String childRest(String containerPath, String childPath) {
        String bracket = containerPath + "[].";
        if (childPath.startsWith(bracket)) {
            return childPath.substring(bracket.length());
        }
        String dot = containerPath + ".";
        if (childPath.startsWith(dot)) {
            return childPath.substring(dot.length());
        }
        return null;
    }

    private static List<Map<String, Object>> projectItems(
            Map<String, Object> fields,
            FieldDescriptionRow container,
            List<FieldDescriptionRow> rows
    ) {
        List<Object> rawItems = FieldDescriptionQualifier.valuesAtPath(fields, container.path());
        if (rawItems.size() == 1 && rawItems.get(0) instanceof Collection<?> collection) {
            rawItems = new ArrayList<>(collection);
        }
        List<Map<String, Object>> items = new ArrayList<>();
        for (Object raw : rawItems) {
            Map<String, Object> item = new LinkedHashMap<>();
            for (FieldDescriptionRow row : rows) {
                String rest = childRest(container.path(), row.path());
                if (row.forwardName().isBlank() || rest == null || rest.isBlank()) {
                    continue;
                }
                List<Object> values = FieldDescriptionQualifier.valuesAtPath(raw, rest);
                if (!values.isEmpty()) {
                    item.put(row.forwardName(), values.size() == 1 ? values.get(0) : List.copyOf(values));
                }
            }
            if (!item.isEmpty()) {
                items.add(item);
            }
        }
        return items;
    }
}
