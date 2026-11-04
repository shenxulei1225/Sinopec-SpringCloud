package cn.cheers.x.module.dynamicbusiness.dataprocess;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 从采集点字段的说明里取出 0 和 1 各自的那一句，给策略表单显示。
 * 判断仍用网关转好的工程值。说明对不上时，这一侧不出现在下拉里。
 */
public final class AlarmPointMeaning {

    private static final Pattern WHEN = Pattern.compile("为\\s*([01])\\s*时([^。]+)");
    private static final Pattern AT = Pattern.compile("处于([^，。]{1,24})时为\\s*([01])(?:，([^。]+))?");
    private static final Pattern NAMED = Pattern.compile("([^，。]{1,24})时为\\s*([01])");

    private AlarmPointMeaning() {
    }

    /** 键是 0 或 1，值是点名后面要显示的那一句。 */
    public static Map<String, String> phrases(String description) {
        Map<String, String> out = new LinkedHashMap<>();
        if (description == null || description.isBlank()) {
            return out;
        }
        Matcher when = WHEN.matcher(description);
        while (when.find()) {
            put(out, when.group(1), when.group(2));
        }
        Matcher at = AT.matcher(description);
        while (at.find()) {
            String phrase = "处于" + at.group(1).trim();
            if (at.group(3) != null && !at.group(3).isBlank()) {
                phrase = phrase + "，" + at.group(3).trim();
            }
            put(out, at.group(2), phrase);
        }
        Matcher named = NAMED.matcher(description);
        while (named.find()) {
            String phrase = named.group(1).trim();
            if (phrase.startsWith("处于") || phrase.startsWith("为")) {
                continue;
            }
            put(out, named.group(2), phrase);
        }
        return out;
    }

    private static void put(Map<String, String> out, String side, String phrase) {
        if (phrase == null) {
            return;
        }
        String text = phrase.trim();
        if (text.startsWith("设备")) {
            text = text.substring("设备".length()).trim();
        }
        if (text.startsWith("这一路")) {
            text = text.substring("这一路".length()).trim();
        }
        if (!text.isBlank()) {
            out.putIfAbsent(side, text);
        }
    }
}
