package cn.cheers.x.module.dynamicbusiness.util;

import java.util.List;

/**
 * 稀疏排序工具（前后端统一规则对应后端实现）
 *
 * 规则：
 * - MIN_SORT = 1
 * - STEP = 1024
 * - 中间插入：floor((prev + next) / 2)
 * - 最前插入：floor((MIN_SORT + first) / 2)
 * - 最后插入：last + STEP
 * - 无空隙时触发重排（rebase）
 */
public final class SparseSortUtils {

    public static final int MIN_SORT = 1;
    public static final int STEP = 1024;

    private SparseSortUtils() {
    }

    /**
     * 末尾追加排序值。
     */
    public static Integer next(Integer maxSort) {
        if (maxSort == null || maxSort < MIN_SORT) {
            return STEP;
        }
        long next = (long) maxSort + STEP;
        return next > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) next;
    }

    /**
     * 兼容已有调用：按位置重建稀疏排序值。
     */
    public static Integer reindexSortByPosition(int index) {
        if (index < 0) {
            index = 0;
        }
        long value = (long) (index + 1) * STEP;
        return value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }

    /**
     * 最前插入排序值。若无空隙返回 null，由调用方触发 rebase。
     */
    public static Integer beforeFirst(Integer firstSort) {
        if (firstSort == null || firstSort <= MIN_SORT + 1) {
            return null;
        }
        int value = (MIN_SORT + firstSort) / 2;
        return value >= MIN_SORT ? value : null;
    }

    /**
     * 中间插入排序值。若无空隙返回 null，由调用方触发 rebase。
     */
    public static Integer between(Integer prevSort, Integer nextSort) {
        if (prevSort == null || nextSort == null || nextSort - prevSort <= 1) {
            return null;
        }
        return (prevSort + nextSort) / 2;
    }

    /**
     * 归一化排序值：非法值回退到 null（由上层按场景补默认）。
     */
    public static Integer normalize(Integer sort) {
        if (sort == null || sort < MIN_SORT) {
            return null;
        }
        return sort;
    }

    /**
     * 是否存在无空隙区间（需要 rebase）。
     */
    public static boolean needsRebase(List<Integer> orderedSorts) {
        if (orderedSorts == null || orderedSorts.size() <= 1) {
            return false;
        }
        Integer prev = null;
        for (Integer current : orderedSorts) {
            if (current == null) {
                continue;
            }
            if (prev != null && current - prev <= 1) {
                return true;
            }
            prev = current;
        }
        return false;
    }

    /**
     * 重排并回写稀疏排序值。
     */
    public static void rebaseSorts(List<? extends SortHolder> holders) {
        if (holders == null || holders.isEmpty()) {
            return;
        }
        int idx = 0;
        for (SortHolder holder : holders) {
            if (holder == null) {
                continue;
            }
            holder.setSort(reindexSortByPosition(idx++));
        }
    }

    public interface SortHolder {
        Integer getSort();

        void setSort(Integer sort);
    }
}
