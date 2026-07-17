package cn.iocoder.yudao.module.scene.platform.service.asset.convert;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 外部命令插件：按格式读取 yudao.scene-platform.asset-convert.plugins.*。
 */
@Component
public class CliAssetFormatConverter implements AssetFormatConverter {

    @Resource
    private AssetConvertProperties properties;

    @Override
    public String name() {
        return "cli-plugin";
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public boolean supports(String format) {
        if (StrUtil.isBlank(format) || properties == null || properties.getPlugins() == null) {
            return false;
        }
        AssetConvertProperties.PluginConfig cfg = properties.getPlugins().get(format.trim().toLowerCase(Locale.ROOT));
        return cfg != null && cfg.isEnabled() && StrUtil.isNotBlank(cfg.getCommand());
    }

    @Override
    public void convert(Path sourceFile, Path targetGlb) throws Exception {
        String format = extensionOf(sourceFile);
        AssetConvertProperties.PluginConfig cfg = properties.getPlugins().get(format);
        if (cfg == null || !cfg.isEnabled()) {
            throw new IllegalStateException("未启用格式插件: " + format);
        }
        String command = cfg.getCommand()
                .replace("{input}", sourceFile.toAbsolutePath().toString())
                .replace("{output}", targetGlb.toAbsolutePath().toString());
        List<String> argv = splitCommand(command);
        ProcessBuilder pb = new ProcessBuilder(argv);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        boolean finished = process.waitFor(Math.max(1, cfg.getTimeoutSeconds()), TimeUnit.SECONDS);
        String log = new String(process.getInputStream().readAllBytes());
        if (!finished) {
            process.destroyForcibly();
            throw new IllegalStateException("转换超时: " + format + "；输出=" + truncate(log));
        }
        if (process.exitValue() != 0) {
            throw new IllegalStateException("转换命令失败 exit=" + process.exitValue() + "；输出=" + truncate(log));
        }
        if (!Files.isRegularFile(targetGlb) || Files.size(targetGlb) == 0) {
            throw new IllegalStateException("转换命令未生成 GLB: " + targetGlb + "；输出=" + truncate(log));
        }
    }

    private static String extensionOf(Path path) {
        String name = path.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(dot + 1).toLowerCase(Locale.ROOT) : "";
    }

    static List<String> splitCommand(String command) {
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuote = false;
        for (int i = 0; i < command.length(); i++) {
            char ch = command.charAt(i);
            if (ch == '"') {
                inQuote = !inQuote;
                continue;
            }
            if (!inQuote && Character.isWhitespace(ch)) {
                if (cur.length() > 0) {
                    parts.add(cur.toString());
                    cur.setLength(0);
                }
                continue;
            }
            cur.append(ch);
        }
        if (cur.length() > 0) {
            parts.add(cur.toString());
        }
        return parts;
    }

    private static String truncate(String s) {
        if (s == null) {
            return "";
        }
        return s.length() > 500 ? s.substring(0, 500) + "..." : s;
    }
}
