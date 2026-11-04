package cn.cheers.x.module.dynamicbusiness.dataprocess;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 把读方向的布尔采集点收成策略下拉里的条件。
 * 工程值是网关已经转好的 0 或 1。不读点位说明。
 * 同一个采集点、同一个工程值，出现在两个及以上型号上，就不带型号名。
 * 只属于一个型号的点，带上型号名。
 * 不读数，也不写策略。
 */
public final class AlarmConditionCatalog {

    public record PointSentence(
            long modelId, String modelName, String pointCode, String side, String pointName, String phrase) {
    }

    public record Published(String label, String wideLabel, String code, java.util.List<String> modelIds) {
    }

    private AlarmConditionCatalog() {
    }

    /** 先放各型号一致的句子，再放带型号名的句子。 */
    public static List<Published> conditions(List<PointSentence> rows) {
        Map<String, List<PointSentence>> groups = new LinkedHashMap<>();
        if (rows != null) {
            for (PointSentence row : rows) {
                if (row == null || row.pointCode() == null || row.pointCode().isBlank()
                        || row.pointName() == null || row.pointName().isBlank()
                        || row.phrase() == null || row.phrase().isBlank() || row.side() == null) {
                    continue;
                }
                groups.computeIfAbsent(row.pointCode() + "\0" + row.side() + "\0" + row.phrase(),
                        key -> new ArrayList<>()).add(row);
            }
        }
        List<Published> shared = new ArrayList<>();
        List<Published> specific = new ArrayList<>();
        for (List<PointSentence> group : groups.values()) {
            Set<Long> models = new LinkedHashSet<>();
            for (PointSentence row : group) {
                models.add(row.modelId());
            }
            PointSentence first = group.get(0);
            String label = first.pointName() + "：" + first.phrase();
            java.util.List<String> modelIds = new java.util.ArrayList<>();
            StringBuilder names = new StringBuilder();
            for (PointSentence row : group) {
                String id = String.valueOf(row.modelId());
                if (!modelIds.contains(id)) {
                    modelIds.add(id);
                    if (names.length() > 0) {
                        names.append("、");
                    }
                    names.append(row.modelName());
                }
            }
            String wide = names + " · " + label;
            if (models.size() > 1) {
                shared.add(new Published(label, wide, "bit:" + first.pointCode() + ":" + first.side(), modelIds));
            } else {
                specific.add(new Published(
                        label, wide, "bit:" + first.modelId() + ":" + first.pointCode() + ":" + first.side(), modelIds));
            }
        }
        shared.addAll(specific);
        return shared;
    }

    /**
     * 这条条件在哪些型号上能读到哪个点。
     * 编号对不上，或这个型号没有这句，返回空。空表示这条条件对那个型号不用。
     */
    public static Map<Long, AlarmPolicy.Expect> resolve(String code, List<PointSentence> rows) {
        Map<Long, AlarmPolicy.Expect> found = new LinkedHashMap<>();
        if (code == null) {
            return found;
        }
        if (code.startsWith("bit:")) {
            String[] parts = code.split(":", 4);
            if (rows == null) {
                return found;
            }
            if (parts.length == 3) {
                for (PointSentence row : rows) {
                    if (parts[1].equals(row.pointCode()) && parts[2].equals(row.side())) {
                        found.put(row.modelId(), new AlarmPolicy.Expect(row.pointCode(), Double.parseDouble(parts[2])));
                    }
                }
                return found;
            }
            if (parts.length == 4 && !parts[2].isBlank()) {
                found.put(Long.parseLong(parts[1]),
                        new AlarmPolicy.Expect(parts[2], Double.parseDouble(parts[3])));
            }
            return found;
        }
        if (code.startsWith("state:")) {
            String[] parts = code.split(":", 4);
            if (parts.length != 4 || parts[2].isBlank()) {
                return found;
            }
            found.put(Long.parseLong(parts[1]),
                    new AlarmPolicy.Expect(parts[2], Double.parseDouble(parts[3])));
        }
        return found;
    }
}
