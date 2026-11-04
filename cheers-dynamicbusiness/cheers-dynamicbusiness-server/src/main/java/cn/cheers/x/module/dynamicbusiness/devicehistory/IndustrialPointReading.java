package cn.cheers.x.module.dynamicbusiness.devicehistory;

/**
 * 把点位说明里的「为 1 / 为 0」读成给人看的一句。
 * 说明里写了能不能控制，就按那一句。说明对不上时不猜。
 */
public final class IndustrialPointReading {

    private IndustrialPointReading() {
    }

    public record Line(String text, Boolean allowsControl) {
    }

    /** 没有说明，或说明里没有为 1、为 0，返回 null。 */
    public static Line read(String description, double value) {
        if (description == null || value != 0.0d && value != 1.0d) {
            return null;
        }
        String clause = clause(description, value == 1.0d ? 1 : 0);
        if (clause == null || clause.isBlank()) {
            return null;
        }
        Boolean allows = null;
        if (clause.contains("平台可以控制")) {
            allows = true;
        } else if (clause.contains("平台不能控制")) {
            allows = false;
        }
        return new Line(clause, allows);
    }

    private static String clause(String description, int bit) {
        int marker = description.indexOf(bit == 1 ? "为 1" : "为 0");
        if (marker < 0 || description.indexOf(bit == 1 ? "为 0" : "为 1") < 0) {
            return null;
        }
        int start = marker + 3;
        int end = description.indexOf('。', start);
        String raw = end < 0 ? description.substring(start) : description.substring(start, end);
        return clean(raw);
    }

    private static String clean(String raw) {
        String text = raw.trim();
        if (text.startsWith("时") || text.startsWith("，")) {
            text = text.substring(1).trim();
        }
        while (text.endsWith("。") || text.endsWith("，")) {
            text = text.substring(0, text.length() - 1).trim();
        }
        return text;
    }
}
