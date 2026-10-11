package cn.cheers.x.device.protocolgateway.protocol.monitor;

import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorEventDTO;
import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorFieldDTO;
import cn.cheers.x.device.protocolgateway.datacollection.CatalogLookup;
import cn.cheers.x.device.protocolgateway.datacollection.DeviceProtocolVersionCatalog;
import cn.cheers.x.device.protocolgateway.datacollection.FieldDescriptionQualifier;
import cn.cheers.x.device.protocolgateway.datacollection.FieldDescriptionRow;
import cn.cheers.x.device.protocolgateway.datacollection.ProtocolInstructionCatalog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 按字段说明把报文里的值填上中文名，给监控页右边那一栏。
 * <p>名字只用说明书上的 label。设备没登记协议版本时，多份说明书不一致就写原因，不挑一份。
 * <p>说明书里的每一行都留下，对象和列表只占一层，值写在它下面的字段上。
 * <p>列表里的每一条指令单独成一步。这一步只带自己的序号、操作码和参数，不把后面各步的同一项拼进同一格。
 * <p>不负责：改窗口原文、改协议合格结论、把整段对象再铺成 JSON。
 * <p>禁止：说明书没有的路径自己起一个中文名。禁止给没有序号的指令编一个操作码。
 */
@Component
@RequiredArgsConstructor
public class ProtocolMonitorFieldReader implements ProtocolMonitorFieldPresenter {

    private static final int SUCCESS_CODE = 200;

    private final ProtocolInstructionCatalog catalog;
    private final DeviceProtocolVersionCatalog versions;
    private final ObjectMapper objectMapper;

    @Override
    public ProtocolMonitorEventDTO present(ProtocolMonitorEventDTO event) {
        if (event == null || event.opcode() == null || event.payloadJson() == null || event.payloadJson().isBlank()) {
            return event;
        }
        Map<String, Object> fields = readObject(event.payloadJson());
        if (fields == null) {
            return event.withParsed(List.of(), "原文不是对象，对不上字段说明");
        }
        String version = versions.firstVersion(event.deviceId()).orElse(null);
        CatalogLookup lookup = catalog.findVolume(event.opcode(), version, volumeOf(fields));
        if (!lookup.ok()) {
            return event.withParsed(List.of(), lookup.error());
        }
        return event.withParsed(project(fields, lookup.rows()), null);
    }

    /**
     * 带返回码的是响应：200 用正常响应卷，其它返回码用异常响应卷。没有返回码的用下发/上报正文卷。
     */
    private static String volumeOf(Map<String, Object> fields) {
        Object code = fields.get("code");
        if (code instanceof Number number) {
            return number.intValue() == SUCCESS_CODE
                    ? ProtocolInstructionCatalog.VOLUME_INBOUND_SUCCESS
                    : ProtocolInstructionCatalog.VOLUME_INBOUND_FAILURE;
        }
        return ProtocolInstructionCatalog.VOLUME_OUTBOUND;
    }

    private Map<String, Object> readObject(String payloadJson) {
        try {
            return objectMapper.readValue(payloadJson, new TypeReference<>() {
            });
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 普通字段仍按说明书顺序各占一行。
     * 带 [] 的路径是列表里的一项：按报文里的顺序，每条指令先写一步，再写这一步自己的字段。
     */
    private static List<ProtocolMonitorFieldDTO> project(Map<String, Object> fields, List<FieldDescriptionRow> rows) {
        List<ProtocolMonitorFieldDTO> shown = new ArrayList<>();
        int index = 0;
        while (index < rows.size()) {
            FieldDescriptionRow row = rows.get(index);
            if (!row.path().contains("[]")) {
                shown.add(line(fields, row, row.path()));
                index++;
                continue;
            }
            String root = arrayRoot(row.path());
            List<FieldDescriptionRow> children = new ArrayList<>();
            while (index < rows.size() && rows.get(index).path().startsWith(root + "[]")) {
                children.add(rows.get(index));
                index++;
            }
            appendActions(shown, fields, root, children);
        }
        return shown;
    }

    /**
     * 列表里的每一项是一条指令。点这一步时，原文里对上的是 packages[序号] 这一段。
     */
    private static void appendActions(
            List<ProtocolMonitorFieldDTO> shown,
            Map<String, Object> fields,
            String root,
            List<FieldDescriptionRow> children
    ) {
        List<?> items = listAt(fields, root);
        for (int index = 0; index < items.size(); index++) {
            Object element = items.get(index);
            shown.add(actionHeading(root, index, element, children));
            Map<String, Object> one = sliceAt(root, element);
            for (FieldDescriptionRow child : children) {
                if (!pathExists(one, child.path())) {
                    continue;
                }
                String path = child.path().replaceFirst("\\[]", "[" + index + "]");
                shown.add(line(one, child, path));
            }
        }
    }

    private static ProtocolMonitorFieldDTO actionHeading(
            String root,
            int index,
            Object element,
            List<FieldDescriptionRow> children
    ) {
        return new ProtocolMonitorFieldDTO(
                actionTitle(element, children, root, index),
                root + "[" + index + "]",
                "",
                null,
                "object",
                false
        );
    }

    /**
     * 一步的标题用说明书上的「动作序号」「内层动作码」加上这一步报文里的值。
     * 这两项都没有时，只标这一步在列表中的位置。
     */
    private static String actionTitle(Object element, List<FieldDescriptionRow> children, String root, int index) {
        if (!(element instanceof Map<?, ?> map)) {
            return "第 " + (index + 1) + " 项";
        }
        List<String> parts = new ArrayList<>();
        addTitlePart(parts, children, root + "[].sequence", map.get("sequence"));
        addTitlePart(parts, children, root + "[].opcode", map.get("opcode"));
        if (parts.isEmpty()) {
            return "第 " + (index + 1) + " 项";
        }
        return String.join(" · ", parts);
    }

    private static void addTitlePart(List<String> parts, List<FieldDescriptionRow> children, String path, Object value) {
        String text = scalarText(value);
        if (text == null) {
            return;
        }
        String label = children.stream()
                .filter(row -> row.path().equals(path))
                .map(FieldDescriptionRow::label)
                .findFirst()
                .orElse(null);
        if (label == null) {
            return;
        }
        parts.add(label + " " + text);
    }

    private static ProtocolMonitorFieldDTO line(Map<String, Object> fields, FieldDescriptionRow row, String path) {
        List<String> texts = new ArrayList<>();
        for (Object value : FieldDescriptionQualifier.valuesAtPath(fields, row.path())) {
            String text = scalarText(value);
            if (text != null) {
                texts.add(text);
            }
        }
        return new ProtocolMonitorFieldDTO(
                row.label(),
                path,
                String.join("、", texts),
                FieldDescriptionQualifier.describeIssue(fields, row),
                row.type(),
                row.required()
        );
    }

    private static String scalarText(Object value) {
        if (value == null || value instanceof Map<?, ?> || value instanceof Collection<?>) {
            return null;
        }
        String text = String.valueOf(value);
        return text.isBlank() ? null : text;
    }

    private static String arrayRoot(String path) {
        int bracket = path.indexOf("[]");
        return path.substring(0, bracket);
    }

    private static List<?> listAt(Map<String, Object> fields, String root) {
        Object node = fields;
        for (String part : root.split("\\.")) {
            if (!(node instanceof Map<?, ?> map)) {
                return List.of();
            }
            node = map.get(part);
        }
        return node instanceof List<?> list ? list : List.of();
    }

    /**
     * 这一步报文里没有的项不列出来。别的动作才有的参数，不在这一步显示成空。
     */
    private static boolean pathExists(Object root, String path) {
        Object node = root;
        for (String rawPart : path.split("\\.")) {
            boolean each = rawPart.endsWith("[]");
            String key = each ? rawPart.substring(0, rawPart.length() - 2) : rawPart;
            if (!(node instanceof Map<?, ?> map) || !map.containsKey(key)) {
                return false;
            }
            Object child = map.get(key);
            if (!each) {
                node = child;
                continue;
            }
            if (!(child instanceof List<?> list) || list.isEmpty()) {
                return false;
            }
            node = list.get(0);
        }
        return true;
    }

    /** 只放这一条指令，让说明书路径 packages[].… 只命中这一步。 */
    private static Map<String, Object> sliceAt(String root, Object element) {
        String[] parts = root.split("\\.");
        Map<String, Object> top = new LinkedHashMap<>();
        Map<String, Object> cursor = top;
        for (int index = 0; index < parts.length - 1; index++) {
            Map<String, Object> next = new LinkedHashMap<>();
            cursor.put(parts[index], next);
            cursor = next;
        }
        cursor.put(parts[parts.length - 1], List.of(element));
        return top;
    }
}
