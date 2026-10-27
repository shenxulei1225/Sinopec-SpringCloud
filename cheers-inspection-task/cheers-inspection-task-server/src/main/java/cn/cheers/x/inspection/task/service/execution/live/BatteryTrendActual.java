package cn.cheers.x.inspection.task.service.execution.live;

import cn.cheers.x.inspection.task.controller.admin.vo.execution.BatteryTrendPointVO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 从这笔执行账的步骤结果收实际电量线。
 * <p>只认结果里写明的电量和回传时刻。缺一项的那一步不画。
 * <p>不负责预计线，不补默认电量。
 */
public final class BatteryTrendActual {

    private static final Pattern BATTERY_FIELD = Pattern.compile(
            "^(electricity|battery|batterylevel|power|soc|电量)$", Pattern.CASE_INSENSITIVE);

    private BatteryTrendActual() {
    }

    /**
     * 按回传时刻从早到晚排。分钟从最早那一点算起。
     */
    public static List<BatteryTrendPointVO> fromStepResults(Object stepResults) {
        if (!(stepResults instanceof Map<?, ?> results)) {
            return List.of();
        }
        List<Sample> samples = new ArrayList<>();
        for (Object item : results.values()) {
            if (!(item instanceof Map<?, ?> result)) {
                continue;
            }
            Object payload = result.get("resultPayload");
            if (payload == null) {
                payload = result.get("result_payload");
            }
            Sample sample = sampleOf(payload);
            if (sample != null) {
                samples.add(sample);
            }
        }
        if (samples.isEmpty()) {
            return List.of();
        }
        samples.sort(Comparator.comparingLong(Sample::epochMs));
        long start = samples.get(0).epochMs();
        List<BatteryTrendPointVO> points = new ArrayList<>();
        for (Sample sample : samples) {
            int minute = (int) Math.max(0, (sample.epochMs() - start) / 60_000L);
            points.add(new BatteryTrendPointVO(minute, sample.percent().doubleValue()));
        }
        return List.copyOf(points);
    }

    private static Sample sampleOf(Object payload) {
        BigDecimal percent = findPercent(payload);
        Long epochMs = findEpochMs(payload);
        if (percent == null || epochMs == null || epochMs <= 0) {
            return null;
        }
        return new Sample(epochMs, percent);
    }

    private static BigDecimal findPercent(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof List<?> list) {
            for (Object item : list) {
                BigDecimal found = findPercent(item);
                if (found != null) {
                    return found;
                }
            }
            return null;
        }
        if (!(value instanceof Map<?, ?> map)) {
            return null;
        }
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            if (BATTERY_FIELD.matcher(String.valueOf(entry.getKey()).trim()).matches()) {
                BigDecimal percent = percentOf(entry.getValue());
                if (percent != null) {
                    return percent;
                }
            }
        }
        for (Object child : map.values()) {
            BigDecimal found = findPercent(child);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private static Long findEpochMs(Object value) {
        if (value instanceof Map<?, ?> map) {
            Object raw = map.get("receivedAtEpochMs");
            if (raw == null) {
                raw = map.get("received_at_epoch_ms");
            }
            Long direct = longOf(raw);
            if (direct != null) {
                return direct;
            }
            for (Object child : map.values()) {
                Long found = findEpochMs(child);
                if (found != null) {
                    return found;
                }
            }
        }
        if (value instanceof List<?> list) {
            for (Object item : list) {
                Long found = findEpochMs(item);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static BigDecimal percentOf(Object raw) {
        if (raw == null) {
            return null;
        }
        String text = String.valueOf(raw).trim();
        if (text.endsWith("%")) {
            text = text.substring(0, text.length() - 1).trim();
        }
        try {
            BigDecimal value = new BigDecimal(text);
            if (value.signum() < 0 || value.compareTo(BigDecimal.valueOf(100)) > 0) {
                return null;
            }
            return value;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static Long longOf(Object raw) {
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw == null) {
            return null;
        }
        try {
            return Long.valueOf(String.valueOf(raw).trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private record Sample(long epochMs, BigDecimal percent) {
    }
}
