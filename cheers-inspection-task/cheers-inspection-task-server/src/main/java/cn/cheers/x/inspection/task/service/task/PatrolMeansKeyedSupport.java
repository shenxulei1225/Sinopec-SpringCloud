package cn.cheers.x.inspection.task.service.task;

import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 同一份任务字段按巡检方式分组：一种方式一份，互不覆盖。
 * <p>管什么：判断是不是「按方式分格」的形状；读当前方式那一格；写下某一格时把旧的一份收进当时那种方式。
 * <p>不管什么：对象勾选、排期模板、已占窗（全任务一份）；也不按机器人/无人机写死两两分支。
 * <p>禁止：读路径猜一种默认方式；把路线快照整份误当成方式分格。
 */
public final class PatrolMeansKeyedSupport {

    /** 路线快照顶层键。出现这些就不是按方式分格。 */
    private static final Set<String> ROUTE_SHAPE_KEYS = Set.of(
            "stopIds",
            "stops",
            "networkRef",
            "estimatedTravelDuration",
            "estimatedDuration",
            "routePreview",
            "segments",
            "totalDistanceMeters");

    private PatrolMeansKeyedSupport() {
    }

    /**
     * 巡检方式编码：大写字母打头的身份码（ROBOT / UAV / MANUAL / FIXED_CAMERA）。
     * 不是路线字段名，也不是中文。
     */
    public static boolean isMeansCode(String raw) {
        if (!StringUtils.hasText(raw)) {
            return false;
        }
        String key = raw.trim();
        return key.equals(key.toUpperCase(Locale.ROOT)) && key.matches("[A-Z][A-Z0-9_]*");
    }

    /**
     * 整份是按方式分格：每个键都是方式编码，且不是一条路线快照。
     */
    public static boolean isMeansKeyedMap(Object raw) {
        if (!(raw instanceof Map<?, ?> map) || map.isEmpty()) {
            return false;
        }
        int meansKeys = 0;
        for (Object key : map.keySet()) {
            String text = key == null ? "" : String.valueOf(key).trim();
            if (ROUTE_SHAPE_KEYS.contains(text)) {
                return false;
            }
            if (!isMeansCode(text)) {
                return false;
            }
            meansKeys++;
        }
        return meansKeys == map.size();
    }

    /**
     * 读某一种方式那一格。
     * <ul>
     *   <li>已经是分格 → 只取这一格，没有就是空</li>
     *   <li>还是旧的一份 → 整份只属于当初写下的那种方式；当前方式对不上就空，不能把机器人的进度和路回给无人机</li>
     * </ul>
     *
     * @param ownerMeans 旧的一份属于谁。换顶栏后这里仍是当时写下的那种，不是现在点中的那种。
     */
    public static Object readSlice(Object raw, String means, String ownerMeans) {
        if (!StringUtils.hasText(means)) {
            return null;
        }
        if (isMeansKeyedMap(raw)) {
            return ((Map<?, ?>) raw).get(means.trim());
        }
        if (isBlankValue(raw)) {
            return null;
        }
        if (StringUtils.hasText(ownerMeans) && !ownerMeans.trim().equals(means.trim())) {
            return null;
        }
        return raw;
    }

    /**
     * 读当前方式那一格。旧的一份只有「现在看的就是当初写下的那种」才返回。
     */
    public static Object readSlice(Object raw, String means) {
        return readSlice(raw, means, means);
    }

    /**
     * 还没分格时，旧的一份进度最高的那种方式。已经分格则取步数最大的键；并列取先写下的。
     * 用来判断旧的一份路/进度属于谁，避免刷新后还给另一种方式。
     */
    public static String primaryMeansOf(Object unlockedRaw) {
        if (!isMeansKeyedMap(unlockedRaw)) {
            return null;
        }
        String best = null;
        int bestStep = -1;
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) unlockedRaw).entrySet()) {
            String key = entry.getKey() == null ? "" : String.valueOf(entry.getKey()).trim();
            if (!isMeansCode(key)) {
                continue;
            }
            int step = 0;
            if (entry.getValue() instanceof Number number) {
                step = number.intValue();
            }
            if (best == null || step > bestStep) {
                best = key;
                bestStep = step;
            }
        }
        return best;
    }

    /**
     * 旧的一份收进 {@code ownerMeans} 那一格。已经是分格就原样拷贝。
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> ensureMap(Object raw, String ownerMeans) {
        if (isMeansKeyedMap(raw)) {
            return new LinkedHashMap<>((Map<String, Object>) raw);
        }
        Map<String, Object> map = new LinkedHashMap<>();
        if (!isBlankValue(raw) && StringUtils.hasText(ownerMeans)) {
            map.put(ownerMeans.trim(), raw);
        }
        return map;
    }

    /**
     * 写下某一种方式那一格，其它方式原样留下。
     * {@code value} 空则去掉这一格，不删别人的。
     */
    public static Map<String, Object> putSlice(Object raw, String ownerMeans, String means, Object value) {
        Map<String, Object> map = ensureMap(raw, ownerMeans);
        if (!StringUtils.hasText(means)) {
            return map;
        }
        String key = means.trim();
        if (isBlankValue(value)) {
            map.remove(key);
        } else {
            map.put(key, value);
        }
        return map;
    }

    public static boolean isBlankValue(Object raw) {
        if (raw == null) {
            return true;
        }
        if (raw instanceof String text) {
            return !StringUtils.hasText(text);
        }
        if (raw instanceof Map<?, ?> map) {
            return map.isEmpty();
        }
        if (raw instanceof List<?> list) {
            return list.isEmpty();
        }
        return false;
    }
}
