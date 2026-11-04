package cn.cheers.x.module.dynamicbusiness.dataprocess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 一座站的告警统计怎么加。
 * 只加紧急、重要、一般。提示不进这三档。
 * 一条告警加在它实际挂上的分类上。又挂了上级又挂了下级时，只加在最下面那一级，避免上级把同一条再加一遍。
 * 某一级对外的数字，是自己这条加上它下面每一级已经累计好的数字。
 * 不负责读库，也不负责推送。
 */
public final class AlarmFacilityCount {

    public static final List<String> LEVELS = List.of("紧急", "重要", "一般");

    private AlarmFacilityCount() {
    }

    /** 全部含已忽略。待处理含未处理、复核中、处理中、维修中、应急事件处理。已消除是已处理。 */
    public static final class Cell {
        private int all;
        private int open;
        private int cleared;

        public int getAll() {
            return all;
        }

        public int getOpen() {
            return open;
        }

        public int getCleared() {
            return cleared;
        }

        public void addStatus(String status) {
            all += 1;
            if ("未处理".equals(status) || "处理中".equals(status) || "复核中".equals(status)
                    || "维修中".equals(status) || "应急事件处理".equals(status)) {
                open += 1;
            } else if ("已处理".equals(status)) {
                cleared += 1;
            }
        }

        public Cell copy() {
            Cell next = new Cell();
            next.all = all;
            next.open = open;
            next.cleared = cleared;
            return next;
        }

        public void add(Cell other) {
            if (other == null) {
                return;
            }
            all += other.all;
            open += other.open;
            cleared += other.cleared;
        }
    }

    public static Map<String, Cell> emptyLevels() {
        Map<String, Cell> levels = new LinkedHashMap<>();
        for (String level : LEVELS) {
            levels.put(level, new Cell());
        }
        return levels;
    }

    public static boolean isCountedLevel(String level) {
        return LEVELS.contains(level);
    }

    /**
     * 这条告警要算进哪些分类。
     * 挂在上级、又挂在它下面某一级时，只留下面那一级。
     */
    public static List<Long> deepest(Collection<Long> linked, Map<Long, Long> parentOf) {
        List<Long> ids = new ArrayList<>();
        if (linked == null) {
            return ids;
        }
        for (Long id : linked) {
            if (id != null && parentOf.containsKey(id) && !ids.contains(id)) {
                ids.add(id);
            }
        }
        List<Long> kept = new ArrayList<>();
        for (Long id : ids) {
            boolean coversAnother = false;
            for (Long other : ids) {
                if (!id.equals(other) && isAncestor(id, other, parentOf)) {
                    coversAnother = true;
                    break;
                }
            }
            if (!coversAnother) {
                kept.add(id);
            }
        }
        return kept;
    }

    /**
     * 每一级的数字改成「自己加上下面各级」。
     * parentOf 里要有这棵树上的每一个分类。没有父级时父编号为空。
     */
    public static Map<Long, Map<String, Cell>> rollup(
            Map<Long, Long> parentOf, Map<Long, Map<String, Cell>> direct) {
        Map<Long, List<Long>> children = new LinkedHashMap<>();
        for (Long id : parentOf.keySet()) {
            children.put(id, new ArrayList<>());
        }
        for (Map.Entry<Long, Long> entry : parentOf.entrySet()) {
            Long parentId = entry.getValue();
            if (parentId != null && children.containsKey(parentId)) {
                children.get(parentId).add(entry.getKey());
            }
        }
        Map<Long, Map<String, Cell>> rolled = new LinkedHashMap<>();
        for (Long id : parentOf.keySet()) {
            fill(id, children, direct, rolled, new java.util.HashSet<>());
        }
        return rolled;
    }

    private static boolean isAncestor(Long ancestor, Long node, Map<Long, Long> parentOf) {
        Long cursor = parentOf.get(node);
        int guard = 0;
        while (cursor != null && guard < 64) {
            if (cursor.equals(ancestor)) {
                return true;
            }
            cursor = parentOf.get(cursor);
            guard += 1;
        }
        return false;
    }

    private static Map<String, Cell> fill(
            Long id,
            Map<Long, List<Long>> children,
            Map<Long, Map<String, Cell>> direct,
            Map<Long, Map<String, Cell>> rolled,
            java.util.Set<Long> visiting) {
        Map<String, Cell> ready = rolled.get(id);
        if (ready != null) {
            return ready;
        }
        if (!visiting.add(id)) {
            return emptyLevels();
        }
        Map<String, Cell> sum = copyLevels(direct.get(id));
        for (Long childId : children.getOrDefault(id, List.of())) {
            Map<String, Cell> child = fill(childId, children, direct, rolled, visiting);
            for (String level : LEVELS) {
                sum.get(level).add(child.get(level));
            }
        }
        visiting.remove(id);
        rolled.put(id, sum);
        return sum;
    }

    private static Map<String, Cell> copyLevels(Map<String, Cell> source) {
        Map<String, Cell> copy = emptyLevels();
        if (source == null) {
            return copy;
        }
        for (String level : LEVELS) {
            Cell cell = source.get(level);
            if (cell != null) {
                copy.put(level, cell.copy());
            }
        }
        return copy;
    }
}
