package cn.cheers.x.module.dynamicbusiness.service.model;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.math.BigDecimal;

/**
 * 型号字段上已经存的取值，收成详情上的一句。
 *
 * 负责：普通文本，以及最小、最大都有的范围。
 * 不管什么：去读哪一个型号、哪一台设备。
 * 禁止：缺一边还拼成范围；空值编一个数。
 */
public final class ModelFieldStoredValue {

    private ModelFieldStoredValue() {
    }

    /** 空、或不是数，返回空字符串。范围两边都有数才拼成一句。 */
    public static String display(String raw, String unit) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String text = raw.trim();
        if (text.startsWith("{")) {
            String range = rangeText(text, unit);
            if (range != null) {
                return range;
            }
        }
        BigDecimal number = number(text);
        if (number != null) {
            return withUnit(plain(number), unit);
        }
        return text;
    }

    private static String rangeText(String text, String unit) {
        Object parsed;
        try {
            parsed = JSON.parse(text);
        } catch (RuntimeException ex) {
            return null;
        }
        if (!(parsed instanceof JSONObject object)) {
            return null;
        }
        BigDecimal filled = number(object.get("value"));
        if (filled != null) {
            return withUnit(plain(filled), unit);
        }
        if (!object.containsKey("min") && !object.containsKey("max")) {
            return null;
        }
        BigDecimal min = number(object.get("min"));
        BigDecimal max = number(object.get("max"));
        if (min == null || max == null) {
            return null;
        }
        return withUnit(plain(min), unit) + " ~ " + withUnit(plain(max), unit);
    }

    private static BigDecimal number(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof BigDecimal decimal) {
            return decimal;
        }
        if (raw instanceof Number number) {
            return new BigDecimal(number.toString());
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

    private static String plain(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }

    private static String withUnit(String text, String unit) {
        if (unit == null || unit.isBlank()) {
            return text;
        }
        String trimmed = unit.trim();
        if ("°".equals(trimmed)) {
            return text + "°";
        }
        return text + " " + trimmed;
    }
}
