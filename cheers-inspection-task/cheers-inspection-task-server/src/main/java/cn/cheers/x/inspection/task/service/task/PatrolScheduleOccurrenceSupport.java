package cn.cheers.x.inspection.task.service.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 按总任务已保存的排期模板展开计划时刻。
 * <p>规则与建任务日历预生成一致：启用模板 + 重复模式 + 重复范围 + 当日时刻。
 * <p>不负责占窗、冲突求解、不把日历点另存一份。缺模板或展不开则返回空，由调用方显式失败。
 */
public final class PatrolScheduleOccurrenceSupport {

    public static final ZoneId DISPLAY_ZONE = ZoneId.of("Asia/Shanghai");

    private static final int SCHEDULE_FIXED = 1;
    private static final int SCHEDULE_INTERVAL = 2;
    private static final int SCHEDULE_AUTO = 3;
    private static final int REPEAT_ONCE = 1;
    private static final int REPEAT_DAILY = 2;
    private static final int REPEAT_WEEKLY = 3;
    private static final int REPEAT_MONTHLY = 4;
    private static final int REPEAT_SPECIFIC = 5;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private PatrolScheduleOccurrenceSupport() {
    }

    /**
     * 展开启用模板的计划开始时刻，按时间排序。
     * <p>与页面日历同一套规则；未启用、缺重复范围、当日不匹配的项不产出时刻。
     */
    public static List<OffsetDateTime> expandPlannedStarts(Object scheduleConfig) {
        Map<String, Object> config = asMap(scheduleConfig);
        if (config == null) {
            return List.of();
        }
        List<Object> items = asList(config.get("items"));
        List<OffsetDateTime> starts = new ArrayList<>();
        for (Object rawItem : items) {
            Map<String, Object> item = asMap(rawItem);
            if (item == null || !asBoolean(item.get("enabled"))) {
                continue;
            }
            starts.addAll(expandItem(item));
        }
        starts.sort(Comparator.naturalOrder());
        return starts;
    }

    private static List<OffsetDateTime> expandItem(Map<String, Object> item) {
        Map<String, Object> values = asMap(item.get("values"));
        if (values == null) {
            return List.of();
        }
        Map<String, Object> range = asMap(values.get("repeatRange"));
        String startDate = range == null ? null : asText(range.get("startDate"));
        String endDate = range == null ? null : asText(range.get("endDate"));
        if (!StringUtils.hasText(startDate) || !StringUtils.hasText(endDate)) {
            return List.of();
        }
        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startDate.trim());
            end = LocalDate.parse(endDate.trim());
        } catch (DateTimeParseException ex) {
            return List.of();
        }
        if (end.isBefore(start)) {
            return List.of();
        }

        List<OffsetDateTime> starts = new ArrayList<>();
        for (LocalDate cursor = start; !cursor.isAfter(end); cursor = cursor.plusDays(1)) {
            for (LocalTime time : dayTimes(values, cursor)) {
                starts.add(cursor.atTime(time).atZone(DISPLAY_ZONE).toOffsetDateTime());
            }
        }
        return starts;
    }

    private static List<LocalTime> dayTimes(Map<String, Object> values, LocalDate date) {
        int scheduleMode = asInt(values.get("scheduleMode"), SCHEDULE_FIXED);
        return switch (scheduleMode) {
            case SCHEDULE_FIXED -> fixedTimes(values, date);
            case SCHEDULE_INTERVAL -> intervalTimes(values, date);
            case SCHEDULE_AUTO -> dayMatchesRepeat(values, date) ? autoTimes(values) : List.of();
            default -> List.of();
        };
    }

    private static List<LocalTime> fixedTimes(Map<String, Object> values, LocalDate date) {
        if (!dayMatchesRepeat(values, date)) {
            return List.of();
        }
        return parseTimes(asStringList(values.get("timePoints")));
    }

    private static List<LocalTime> intervalTimes(Map<String, Object> values, LocalDate date) {
        if (!dayMatchesRepeat(values, date)) {
            return List.of();
        }
        LocalTime anchor = parseTime(asText(values.get("anchorTime")), LocalTime.of(9, 0));
        int gap = Math.max(1, asInt(values.get("taskCycleMinutes"), 60));
        int count = Math.max(1, asInt(values.get("dailyExecutionCount"), 1));
        List<LocalTime> times = new ArrayList<>();
        int hour = anchor.getHour();
        int minute = anchor.getMinute();
        for (int i = 0; i < count; i++) {
            if (hour >= 24) {
                break;
            }
            times.add(LocalTime.of(hour, minute));
            minute += gap;
            while (minute >= 60) {
                minute -= 60;
                hour += 1;
            }
        }
        return times;
    }

    private static List<LocalTime> autoTimes(Map<String, Object> values) {
        int count = asInt(values.get("dailyExecutionCount"), 0);
        if (count <= 0) {
            return List.of();
        }
        int interval = (24 * 60) / (count + 1);
        List<LocalTime> times = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            int total = interval * i;
            times.add(LocalTime.of(total / 60, total % 60));
        }
        return times;
    }

    /**
     * 一次性 / 每日：范围内每天都算；每周 / 每月 / 指定日期按勾选项过滤。
     */
    private static boolean dayMatchesRepeat(Map<String, Object> values, LocalDate date) {
        int repeatMode = asInt(values.get("repeatMode"), REPEAT_DAILY);
        return switch (repeatMode) {
            case REPEAT_ONCE, REPEAT_DAILY -> true;
            case REPEAT_WEEKLY -> asIntList(values.get("weekDays")).contains(date.getDayOfWeek().getValue());
            case REPEAT_MONTHLY -> asIntList(values.get("monthDays")).contains(date.getDayOfMonth());
            case REPEAT_SPECIFIC -> asStringList(values.get("specificDates")).contains(date.toString());
            default -> false;
        };
    }

    private static List<LocalTime> parseTimes(List<String> raw) {
        List<LocalTime> times = new ArrayList<>();
        for (String text : raw) {
            LocalTime time = parseTime(text, null);
            if (time != null) {
                times.add(time);
            }
        }
        return times;
    }

    private static LocalTime parseTime(String text, LocalTime fallback) {
        if (!StringUtils.hasText(text)) {
            return fallback;
        }
        try {
            String trimmed = text.trim();
            if (trimmed.length() == 5) {
                return LocalTime.parse(trimmed);
            }
            return LocalTime.parse(trimmed.substring(0, Math.min(8, trimmed.length())));
        } catch (DateTimeParseException ex) {
            return fallback;
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Map<?, ?> map) {
            return new LinkedHashMap<>((Map<String, Object>) map);
        }
        if (raw instanceof String json && StringUtils.hasText(json)) {
            try {
                return OBJECT_MAPPER.readValue(json.trim(), new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception ignored) {
                return null;
            }
        }
        return null;
    }

    private static List<Object> asList(Object raw) {
        if (!(raw instanceof List<?> list)) {
            return List.of();
        }
        return new ArrayList<>(list);
    }

    private static List<String> asStringList(Object raw) {
        List<String> out = new ArrayList<>();
        if (!(raw instanceof List<?> list)) {
            return out;
        }
        for (Object item : list) {
            String text = asText(item);
            if (StringUtils.hasText(text)) {
                out.add(text);
            }
        }
        return out;
    }

    private static List<Integer> asIntList(Object raw) {
        List<Integer> out = new ArrayList<>();
        if (!(raw instanceof List<?> list)) {
            return out;
        }
        for (Object item : list) {
            if (item instanceof Number number) {
                out.add(number.intValue());
            } else if (item != null) {
                try {
                    out.add(Integer.parseInt(String.valueOf(item).trim()));
                } catch (NumberFormatException ignored) {
                    // 非法勾选项跳过，不猜星期/日期
                }
            }
        }
        return out;
    }

    private static String asText(Object raw) {
        return raw == null ? null : String.valueOf(raw).trim();
    }

    private static boolean asBoolean(Object raw) {
        if (raw instanceof Boolean bool) {
            return bool;
        }
        return raw != null && Boolean.parseBoolean(String.valueOf(raw));
    }

    private static int asInt(Object raw, int fallback) {
        if (raw instanceof Number number) {
            return number.intValue();
        }
        if (raw == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(String.valueOf(raw).trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
