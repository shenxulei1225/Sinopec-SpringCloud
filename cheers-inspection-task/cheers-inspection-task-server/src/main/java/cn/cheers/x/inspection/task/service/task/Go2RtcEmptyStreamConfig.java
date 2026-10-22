package cn.cheers.x.inspection.task.service.task;

import java.util.ArrayList;
import java.util.List;

/**
 * 在 go2rtc 配置正文里补上或拿掉一个收流名字。
 * <p>负责：让流媒体首页多出这一行，或把不再使用的那一行从配置里去掉。设备才能往留下的名字推。
 * <p>不负责：去拉某一路摄像机地址。空名字和配置文件里已有的 robot-live 一样，只等推流。
 */
public final class Go2RtcEmptyStreamConfig {

    private Go2RtcEmptyStreamConfig() {
    }

    /**
     * 配置里已经有这个名字时，原文不动。没有 streams 段时不能登记。
     *
     * @return 写回 go2rtc 的整份配置
     */
    public static String ensureEmptyStream(String configYaml, String streamName) {
        if (configYaml == null || configYaml.isBlank()) {
            throw new IllegalArgumentException("流媒体配置是空的，不能登记收流名字");
        }
        if (streamName == null || !streamName.matches("[A-Za-z0-9._-]+")) {
            throw new IllegalArgumentException("收流名字只能含字母、数字、点、下划线和短横线");
        }
        String normalized = configYaml.replace("\r\n", "\n").replace('\r', '\n');
        String[] lines = normalized.split("\n", -1);
        int streamsLine = -1;
        String keyLine = "  " + streamName + ":";
        for (int i = 0; i < lines.length; i++) {
            if ("streams:".equals(lines[i])) {
                streamsLine = i;
            }
            if (isStreamKey(lines[i], keyLine)) {
                return configYaml;
            }
        }
        if (streamsLine < 0) {
            throw new IllegalArgumentException("流媒体配置没有收流名单，不能登记收流名字");
        }
        StringBuilder updated = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            updated.append(lines[i]);
            if (i < lines.length - 1 || normalized.endsWith("\n")) {
                updated.append('\n');
            }
            if (i == streamsLine) {
                updated.append(keyLine).append('\n');
            }
        }
        return updated.toString();
    }

    /**
     * 配置里没有这个名字时，原文不动。
     * 有这个名字时，连同它下面缩进的地址一起拿掉。同一层的别的名字留下。
     *
     * @return 写回 go2rtc 的整份配置
     */
    public static String removeStream(String configYaml, String streamName) {
        if (configYaml == null || configYaml.isBlank()) {
            throw new IllegalArgumentException("流媒体配置是空的，不能登记收流名字");
        }
        if (streamName == null || !streamName.matches("[A-Za-z0-9._-]+")) {
            throw new IllegalArgumentException("收流名字只能含字母、数字、点、下划线和短横线");
        }
        String normalized = configYaml.replace("\r\n", "\n").replace('\r', '\n');
        String[] lines = normalized.split("\n", -1);
        String keyLine = "  " + streamName + ":";
        boolean found = false;
        StringBuilder updated = new StringBuilder();
        int index = 0;
        while (index < lines.length) {
            if (isStreamKey(lines[index], keyLine)) {
                found = true;
                index++;
                while (index < lines.length && isNestedUnderStream(lines[index])) {
                    index++;
                }
                continue;
            }
            updated.append(lines[index]);
            if (index < lines.length - 1 || normalized.endsWith("\n")) {
                updated.append('\n');
            }
            index++;
        }
        return found ? updated.toString() : configYaml;
    }

    /** streams 段里已经写下的收流名字。没有这段时返回空名单。 */
    public static List<String> streamNames(String configYaml) {
        List<String> names = new ArrayList<>();
        if (configYaml == null || configYaml.isBlank()) {
            return names;
        }
        String normalized = configYaml.replace("\r\n", "\n").replace('\r', '\n');
        boolean inStreams = false;
        for (String line : normalized.split("\n", -1)) {
            if (!inStreams) {
                if ("streams:".equals(line)) {
                    inStreams = true;
                }
                continue;
            }
            if (line.isEmpty()) {
                continue;
            }
            if (!line.startsWith(" ") && !line.startsWith("\t")) {
                break;
            }
            if (!line.startsWith("  ") || line.startsWith("   ") || line.startsWith("\t")) {
                continue;
            }
            String trimmed = line.trim();
            int colon = trimmed.indexOf(':');
            if (colon <= 0) {
                continue;
            }
            String name = trimmed.substring(0, colon).trim();
            if (name.matches("[A-Za-z0-9._-]+")) {
                names.add(name);
            }
        }
        return names;
    }

    private static boolean isNestedUnderStream(String line) {
        return line.startsWith("   ") || line.startsWith("\t");
    }

    private static boolean isStreamKey(String line, String keyLine) {
        return line.equals(keyLine) || line.startsWith(keyLine + " ") || line.startsWith(keyLine + "\t");
    }
}
