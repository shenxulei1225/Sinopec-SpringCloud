package cn.cheers.x.module.dynamicbusiness.service.entity.mount;

import cn.cheers.x.framework.common.exception.ServiceException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 挂载点字段（payload_mounts）。
 *
 * 负责：读这台主机上的多行，算出哪些设备要改上级。
 * 每一行是挂载点编号，和可选的一台设备。
 * 不负责：落库、改 treePath。调用方按这里给出的移动去改上级。
 * 禁止：挂载点空着还选了设备；同一台主机上编号重复；同一台设备出现在两行。
 */
public final class PayloadMounts {

    public static final String FIELD_CODE = "payload_mounts";

    private PayloadMounts() {
    }

    /** 一行挂载点。equipmentId 为空表示这一行还没选设备。 */
    public record Row(String mountPoint, Long equipmentId) {
    }

    /** 把一台设备的上级改成 newParentId。空表示升为没有上级。 */
    public record Move(long equipmentId, Long newParentId) {
    }

    public static List<Row> parse(Object raw) {
        if (raw == null) {
            return List.of();
        }
        JSONArray array = asArray(raw);
        if (array == null) {
            throw new ServiceException(400, "挂载点要按行填写");
        }
        List<Row> rows = new ArrayList<>();
        Set<String> points = new LinkedHashSet<>();
        Set<Long> devices = new LinkedHashSet<>();
        for (int i = 0; i < array.size(); i++) {
            Object item = array.get(i);
            if (!(item instanceof JSONObject object)) {
                throw new ServiceException(400, "挂载点要按行填写");
            }
            String point = normalizePoint(object.get("mountPoint"));
            Long equipmentId = readEquipmentId(object.get("equipmentId"));
            if (point == null) {
                if (equipmentId == null && isBlankPoint(object.get("mountPoint"))) {
                    continue;
                }
                throw new ServiceException(400, "挂载点要填数字");
            }
            if (!points.add(point)) {
                throw new ServiceException(400, "同一台设备上挂载点不能重复");
            }
            if (equipmentId != null && !devices.add(equipmentId)) {
                throw new ServiceException(400, "同一台设备不能挂在两个挂载点");
            }
            rows.add(new Row(point, equipmentId));
        }
        return List.copyOf(rows);
    }

    /**
     * 对比保存前后的行，得到要改的上级。
     * currentParentById 里要有每一个被选中、以及从行里去掉的设备的当前上级。没有这台设备就报错。
     */
    public static List<Move> plan(long hostId,
                                   List<Row> previous,
                                   List<Row> next,
                                   Map<Long, Long> currentParentById) {
        if (hostId <= 0) {
            throw new ServiceException(400, "挂载点所在的设备无效");
        }
        Set<Long> nextIds = equipmentIds(next);
        Set<Long> previousIds = equipmentIds(previous);
        if (nextIds.contains(hostId) || previousIds.contains(hostId)) {
            throw new ServiceException(400, "不能把这台设备挂到自己下面");
        }
        Map<Long, Long> parents = currentParentById == null ? Map.of() : currentParentById;
        List<Move> moves = new ArrayList<>();
        for (Long removed : previousIds) {
            if (nextIds.contains(removed)) {
                continue;
            }
            Long parent = requireParent(parents, removed);
            if (parent != null && parent == hostId) {
                moves.add(new Move(removed, null));
            }
        }
        for (Long added : nextIds) {
            Long parent = requireParent(parents, added);
            if (parent == null || parent != hostId) {
                moves.add(new Move(added, hostId));
            }
        }
        return List.copyOf(moves);
    }

    public static Set<Long> equipmentIds(List<Row> rows) {
        Set<Long> ids = new LinkedHashSet<>();
        if (rows == null) {
            return ids;
        }
        for (Row row : rows) {
            if (row != null && row.equipmentId() != null && row.equipmentId() > 0) {
                ids.add(row.equipmentId());
            }
        }
        return ids;
    }

    private static Long requireParent(Map<Long, Long> parents, Long equipmentId) {
        if (!parents.containsKey(equipmentId)) {
            throw new ServiceException(400, "选中的设备不存在");
        }
        return parents.get(equipmentId);
    }

    private static JSONArray asArray(Object raw) {
        if (raw instanceof JSONArray array) {
            return array;
        }
        if (raw instanceof List<?> list) {
            return JSON.parseArray(JSON.toJSONString(list));
        }
        if (raw instanceof String text) {
            String trimmed = text.trim();
            if (trimmed.isEmpty()) {
                return new JSONArray();
            }
            try {
                Object parsed = JSON.parse(trimmed);
                return parsed instanceof JSONArray array ? array : null;
            } catch (RuntimeException ex) {
                return null;
            }
        }
        return null;
    }

    private static boolean isBlankPoint(Object raw) {
        return raw == null || String.valueOf(raw).trim().isEmpty() || "null".equals(String.valueOf(raw).trim());
    }

    static String normalizePoint(Object raw) {
        if (raw == null) {
            return null;
        }
        String text = String.valueOf(raw).trim();
        if (!text.matches("\\d+")) {
            return null;
        }
        try {
            int value = Integer.parseInt(text);
            if (value < 0) {
                return null;
            }
            return Integer.toString(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static Long readEquipmentId(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number number) {
            long value = number.longValue();
            return value > 0 ? value : null;
        }
        String text = String.valueOf(raw).trim();
        if (text.isEmpty() || "null".equals(text)) {
            return null;
        }
        try {
            long value = Long.parseLong(text);
            return value > 0 ? value : null;
        } catch (NumberFormatException ex) {
            throw new ServiceException(400, "挂载点上的设备无效");
        }
    }

}
