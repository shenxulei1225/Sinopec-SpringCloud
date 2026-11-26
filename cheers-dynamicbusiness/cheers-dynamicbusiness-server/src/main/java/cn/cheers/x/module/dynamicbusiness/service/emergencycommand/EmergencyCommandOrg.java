package cn.cheers.x.module.dynamicbusiness.service.emergencycommand;

import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandOrgGroupRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandOrgSeatRespVO;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 预案上的指挥班子，以及启动时抄到事件上的那一份。
 *
 * 管什么：各组在哪些等级成立、席位对哪个职务、启动时按当时的任职写下人名。
 * 不管什么：公司组织架构怎么维护。任职仍在组织架构里。
 * 禁止：展示时再去预案上重算人名。事件上没有这份名单，就说明启动时没写下。
 */
public final class EmergencyCommandOrg {

    public static final String SCOPE_UNIT = "unit";
    public static final String SCOPE_COMPANY = "company";

    private EmergencyCommandOrg() {
    }

    public record Seat(String role, String scope, String postName, String backupPostName) {
    }

    public record Group(String name, String duties, List<String> levels, List<Seat> seats) {
    }

    /** 指挥页要显示的组和一句话原因。能显示时原因为空。还没抄到事件上时 groups 为空。 */
    public record Shown(List<EmergencyCommandOrgGroupRespVO> groups, String gap) {
    }

    /** 预案上的指挥班子。写错或空着就得到空列表，不补一组。 */
    public static List<Group> parseTemplate(Object raw) {
        JSONObject object = asObject(raw);
        if (object == null) {
            return List.of();
        }
        JSONArray groups = object.getJSONArray("groups");
        if (groups == null || groups.isEmpty()) {
            return List.of();
        }
        List<Group> parsed = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            JSONObject group = groups.getJSONObject(i);
            if (group == null) {
                continue;
            }
            String name = text(group.get("name"));
            if (name.isEmpty()) {
                continue;
            }
            parsed.add(new Group(name, text(group.get("duties")), texts(group.get("levels")), seats(group.getJSONArray("seats"))));
        }
        return parsed;
    }

    /**
     * 启动时写下的名单。人名按当时的任职填上，A 角没有人再用 B 角。
     * 两个都没有，人名留空，席位仍保留。
     */
    public static JSONObject snapshot(List<Group> groups, Map<String, String> unitHolders, Map<String, String> companyHolders) {
        JSONArray rows = new JSONArray();
        for (Group group : groups) {
            JSONObject row = new JSONObject();
            row.put("name", group.name());
            row.put("duties", group.duties());
            row.put("levels", new JSONArray(group.levels()));
            JSONArray seats = new JSONArray();
            for (Seat seat : group.seats()) {
                JSONObject item = new JSONObject();
                item.put("role", seat.role());
                item.put("scope", seat.scope());
                item.put("postName", seat.postName());
                item.put("personName", person(seat, unitHolders, companyHolders));
                seats.add(item);
            }
            row.put("seats", seats);
            rows.add(row);
        }
        JSONObject snapshot = new JSONObject();
        snapshot.put("groups", rows);
        return snapshot;
    }

    /** 当前等级要成立的组。等级名单空着表示每个等级都成立。 */
    public static JSONArray visibleGroups(Object snapshot, String level) {
        JSONObject object = asObject(snapshot);
        if (object == null) {
            return null;
        }
        JSONArray groups = object.getJSONArray("groups");
        if (groups == null) {
            return new JSONArray();
        }
        String current = text(level);
        JSONArray visible = new JSONArray();
        for (int i = 0; i < groups.size(); i++) {
            JSONObject group = groups.getJSONObject(i);
            if (group == null) {
                continue;
            }
            List<String> levels = texts(group.get("levels"));
            if (levels.isEmpty() || levels.contains(current)) {
                visible.add(group);
            }
        }
        return visible;
    }

    /**
     * 只读启动时写下的名单。事件上还没有这份名单、预案没写班子、或这一级没有要成立的组，用一句话说明。
     */
    public static Shown show(Object snapshot, String level) {
        JSONArray visible = visibleGroups(snapshot, level);
        if (visible == null) {
            return new Shown(List.of(), "启动响应后才列出这一起事件的指挥机构");
        }
        List<EmergencyCommandOrgGroupRespVO> groups = new ArrayList<>();
        for (int i = 0; i < visible.size(); i++) {
            JSONObject group = visible.getJSONObject(i);
            if (group == null) {
                continue;
            }
            EmergencyCommandOrgGroupRespVO row = new EmergencyCommandOrgGroupRespVO();
            row.setName(text(group.get("name")));
            row.setDuties(text(group.get("duties")));
            List<EmergencyCommandOrgSeatRespVO> seats = new ArrayList<>();
            JSONArray seatRows = group.getJSONArray("seats");
            if (seatRows != null) {
                for (int seatIndex = 0; seatIndex < seatRows.size(); seatIndex++) {
                    JSONObject seat = seatRows.getJSONObject(seatIndex);
                    if (seat == null) {
                        continue;
                    }
                    EmergencyCommandOrgSeatRespVO item = new EmergencyCommandOrgSeatRespVO();
                    item.setRole(text(seat.get("role")));
                    item.setScope(SCOPE_COMPANY.equals(text(seat.get("scope"))) ? "公司" : "事发单位");
                    item.setPostName(text(seat.get("postName")));
                    item.setPersonName(text(seat.get("personName")));
                    seats.add(item);
                }
            }
            row.setSeats(seats);
            groups.add(row);
        }
        if (!groups.isEmpty()) {
            return new Shown(groups, "");
        }
        JSONObject source = asObject(snapshot);
        JSONArray all = source == null ? null : source.getJSONArray("groups");
        String gap = all != null && !all.isEmpty() ? "这一级没有要成立的指挥组" : "这份预案还没有指挥机构";
        return new Shown(List.of(), gap);
    }

    static String person(Seat seat, Map<String, String> unitHolders, Map<String, String> companyHolders) {
        Map<String, String> holders = SCOPE_COMPANY.equals(seat.scope()) ? companyHolders : unitHolders;
        String primary = holders.getOrDefault(seat.postName(), "");
        if (!primary.isEmpty()) {
            return primary;
        }
        if (seat.backupPostName().isEmpty()) {
            return "";
        }
        return holders.getOrDefault(seat.backupPostName(), "");
    }

    private static List<Seat> seats(JSONArray raw) {
        List<Seat> seats = new ArrayList<>();
        if (raw == null) {
            return seats;
        }
        for (int i = 0; i < raw.size(); i++) {
            JSONObject seat = raw.getJSONObject(i);
            if (seat == null) {
                continue;
            }
            String role = text(seat.get("role"));
            String postName = text(seat.get("postName"));
            if (role.isEmpty() || postName.isEmpty()) {
                continue;
            }
            String scope = SCOPE_COMPANY.equals(text(seat.get("scope"))) ? SCOPE_COMPANY : SCOPE_UNIT;
            seats.add(new Seat(role, scope, postName, text(seat.get("backupPostName"))));
        }
        return seats;
    }

    private static List<String> texts(Object raw) {
        List<String> values = new ArrayList<>();
        if (raw instanceof JSONArray array) {
            for (int i = 0; i < array.size(); i++) {
                String value = text(array.get(i));
                if (!value.isEmpty()) {
                    values.add(value);
                }
            }
            return values;
        }
        if (raw instanceof List<?> list) {
            for (Object item : list) {
                String value = text(item);
                if (!value.isEmpty()) {
                    values.add(value);
                }
            }
        }
        return values;
    }

    private static JSONObject asObject(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof JSONObject object) {
            return object;
        }
        if (raw instanceof Map<?, ?> map) {
            return new JSONObject(map);
        }
        if (raw instanceof String text) {
            String trimmed = text.trim();
            if (trimmed.isEmpty()) {
                return null;
            }
            return JSON.parseObject(trimmed);
        }
        return JSON.parseObject(JSON.toJSONString(raw));
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }
}
