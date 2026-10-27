package cn.cheers.x.inspection.task.service.execution.openrun;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 把电量百分比换成还能用的秒数，或把剩余秒数反查百分比。
 * <p>设备上已经写下耗电曲线时，按曲线两点之间取。没有曲线时，按最长飞行时间匀速取：满电用完整段，0% 为 0。
 * <p>不负责下发，也不编一条曲线写回设备。百分比不直接和秒数比。最长飞行时间空着时不算。
 */
public final class DongguBatteryRemain {

    private DongguBatteryRemain() {
    }

    /**
     * 算得出返回秒数。百分比读不出，或满电秒数没有，返回 null。
     * 没有可用曲线时按电量百分比匀速取满电秒数。
     */
    public static Integer seconds(String electricityPercent, Integer fullBatterySeconds, Object dischargeCurve) {
        BigDecimal percent = percent(electricityPercent);
        BigDecimal ratio = ratio(percent, dischargeCurve);
        if (percent == null || ratio == null || fullBatterySeconds == null || fullBatterySeconds <= 0) {
            return null;
        }
        return ratio.multiply(BigDecimal.valueOf(fullBatterySeconds))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }

    /**
     * 剩余可飞秒数反查电量百分比。满电秒数空着时不算。
     * 没有可用曲线时按剩余秒数占满电秒数的比例匀速换算。
     * 有曲线且剩余为 0 时取曲线上比例为 0 的最高电量，不编一个更低的数。
     */
    public static BigDecimal percentFromRemainSeconds(
            Integer remainSeconds, Integer fullBatterySeconds, Object dischargeCurve) {
        if (remainSeconds == null || remainSeconds < 0
                || fullBatterySeconds == null || fullBatterySeconds <= 0) {
            return null;
        }
        List<Knot> knots = knots(dischargeCurve);
        if (knots.size() < 2) {
            BigDecimal ratio = BigDecimal.valueOf(remainSeconds)
                    .divide(BigDecimal.valueOf(fullBatterySeconds), 8, RoundingMode.HALF_UP);
            if (ratio.compareTo(BigDecimal.ZERO) < 0) {
                ratio = BigDecimal.ZERO;
            }
            if (ratio.compareTo(BigDecimal.ONE) > 0) {
                ratio = BigDecimal.ONE;
            }
            return ratio.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal ratio = BigDecimal.valueOf(remainSeconds)
                .divide(BigDecimal.valueOf(fullBatterySeconds), 8, RoundingMode.HALF_UP);
        if (ratio.compareTo(BigDecimal.ZERO) < 0) {
            ratio = BigDecimal.ZERO;
        }
        if (ratio.compareTo(BigDecimal.ONE) > 0) {
            ratio = BigDecimal.ONE;
        }
        knots.sort(Comparator.comparing(Knot::ratio).thenComparing(Knot::percent));
        Knot first = knots.get(0);
        Knot last = knots.get(knots.size() - 1);
        if (ratio.compareTo(first.ratio) <= 0) {
            return highestPercentAtRatio(knots, first.ratio);
        }
        if (ratio.compareTo(last.ratio) >= 0) {
            return last.percent;
        }
        for (int index = 1; index < knots.size(); index++) {
            Knot right = knots.get(index);
            if (ratio.compareTo(right.ratio) > 0) {
                continue;
            }
            Knot left = knots.get(index - 1);
            BigDecimal span = right.ratio.subtract(left.ratio);
            if (span.signum() == 0) {
                return highestPercentAtRatio(knots, right.ratio);
            }
            BigDecimal place = ratio.subtract(left.ratio).divide(span, 8, RoundingMode.HALF_UP);
            return left.percent.add(right.percent.subtract(left.percent).multiply(place))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return null;
    }

    private static BigDecimal highestPercentAtRatio(List<Knot> knots, BigDecimal ratio) {
        BigDecimal percent = null;
        for (Knot knot : knots) {
            if (knot.ratio.compareTo(ratio) != 0) {
                continue;
            }
            if (percent == null || knot.percent.compareTo(percent) > 0) {
                percent = knot.percent;
            }
        }
        return percent;
    }

    /**
     * 最长飞行时间的单位是分钟。换成满电支持的秒数。空着或不是数时返回 null。
     */
    public static Integer fullBatterySeconds(Object flightMinutes) {
        BigDecimal minutes = decimal(flightMinutes);
        if (minutes == null || minutes.signum() <= 0) {
            return null;
        }
        return minutes.multiply(BigDecimal.valueOf(60))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }

    private static BigDecimal ratio(BigDecimal percent, Object curve) {
        if (percent == null) {
            return null;
        }
        List<Knot> knots = knots(curve);
        if (knots.size() < 2) {
            return percent.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
        }
        knots.sort(Comparator.comparing(Knot::percent));
        Knot first = knots.get(0);
        Knot last = knots.get(knots.size() - 1);
        if (percent.compareTo(first.percent) <= 0) {
            return first.ratio;
        }
        if (percent.compareTo(last.percent) >= 0) {
            return last.ratio;
        }
        for (int index = 1; index < knots.size(); index++) {
            Knot right = knots.get(index);
            if (percent.compareTo(right.percent) > 0) {
                continue;
            }
            Knot left = knots.get(index - 1);
            BigDecimal span = right.percent.subtract(left.percent);
            if (span.signum() == 0) {
                return right.ratio;
            }
            BigDecimal place = percent.subtract(left.percent).divide(span, 8, RoundingMode.HALF_UP);
            return left.ratio.add(right.ratio.subtract(left.ratio).multiply(place));
        }
        return null;
    }

    private static List<Knot> knots(Object curve) {
        if (!(curve instanceof List<?> list)) {
            return List.of();
        }
        List<Knot> knots = new ArrayList<>();
        for (Object item : list) {
            if (!(item instanceof Map<?, ?> map)) {
                continue;
            }
            BigDecimal percent = decimal(map.get("percent"));
            BigDecimal ratio = decimal(map.get("ratio"));
            if (percent == null || ratio == null) {
                continue;
            }
            if (percent.signum() < 0 || percent.compareTo(BigDecimal.valueOf(100)) > 0) {
                continue;
            }
            if (ratio.signum() < 0 || ratio.compareTo(BigDecimal.ONE) > 0) {
                continue;
            }
            knots.add(new Knot(percent, ratio));
        }
        return knots;
    }

    private record Knot(BigDecimal percent, BigDecimal ratio) {
    }

    private static BigDecimal percent(String raw) {
        if (raw == null) {
            return null;
        }
        String text = raw.trim();
        if (text.endsWith("%")) {
            text = text.substring(0, text.length() - 1).trim();
        }
        BigDecimal value = decimal(text);
        if (value == null || value.signum() < 0 || value.compareTo(BigDecimal.valueOf(100)) > 0) {
            return null;
        }
        return value;
    }

    private static BigDecimal decimal(Object raw) {
        if (raw instanceof BigDecimal decimal) {
            return decimal;
        }
        if (raw instanceof Number number) {
            return new BigDecimal(number.toString());
        }
        if (raw == null) {
            return null;
        }
        String text = String.valueOf(raw).trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
