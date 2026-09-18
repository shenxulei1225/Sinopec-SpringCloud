package cn.cheers.x.infra.service.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.infra.enums.ErrorCodeConstants.FILE_MAGIKA_RESULT_INVALID;
import static cn.cheers.x.infra.enums.ErrorCodeConstants.FILE_MAGIKA_TIMEOUT;
import static cn.cheers.x.infra.enums.ErrorCodeConstants.FILE_MAGIKA_UNAVAILABLE;
import static cn.cheers.x.infra.enums.ErrorCodeConstants.FILE_TYPE_NOT_ALLOWED;

/**
 * 上传入口的文件安全校验。
 *
 * 管什么：调用 Magika 识别真实内容，再按白名单决定能不能存。
 * 不管什么：文件落盘、前端选择框扩展名、预览。
 * 禁止：按扩展名假装识别成功；空文件用文件名冒充 PDF/Word。
 */
@Service
@Slf4j
public class FileUploadSecurityService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 配置里可写用户习惯名（7z/gz），识别后会归一到 Magika 标签。
     */
    static final String DEFAULT_ALLOWED_TYPES =
            "pdf,doc,docx,xls,xlsx,ppt,pptx,txt,md,markdown,zip,rar,sevenzip,7z,7zip,tar,gzip,gz,bzip,bzip2,xz";

    /** 拒绝时告诉用户当前能传什么，不要只丢内部标签。 */
    static final String ALLOWED_TYPES_USER_HINT =
            "PDF、Word、Excel、PPT、文本（可以是空文件）、Markdown、压缩包（zip / rar / 7z / tar / gz）";

    private static final Map<String, String> TYPE_DISPLAY_NAMES = typeDisplayNames();

    @Value("${cheers.infra.file.magika.command:magika}")
    private String magikaCommand;

    @Value("${cheers.infra.file.magika.timeout-seconds:10}")
    private long magikaTimeoutSeconds;

    @Value("${cheers.infra.file.allowed-types:" + DEFAULT_ALLOWED_TYPES + "}")
    private String allowedTypesConfig;

    @PostConstruct
    public void logStartupConfig() {
        Set<String> whitelist = parseAllowedTypes(allowedTypesConfig);
        log.info("[file-upload-security] Magika command='{}', timeoutSeconds={}, allowedTypes={}",
                magikaCommand, magikaTimeoutSeconds, whitelist);
    }

    public record FileDetectionResult(String detectedType, String mime) {
    }

    public FileDetectionResult detectAndValidate(byte[] content, String originalFilename, String fallbackMime) {
        // 空文件没有内容可识别，不跑 Magika，也不按扩展名猜类型。
        if (content == null || content.length == 0) {
            log.info("[file-upload-security] empty file allowed filename='{}'", originalFilename);
            return emptyFileResult(fallbackMime);
        }
        FileDetectionResult detected = detectByMagika(content, originalFilename, fallbackMime);
        return acceptOrReject(detected, originalFilename);
    }

    FileDetectionResult acceptOrReject(FileDetectionResult detected, String originalFilename) {
        String normalizedType = normalizeType(detected.detectedType());
        if ("empty".equals(normalizedType)) {
            return new FileDetectionResult("empty", detected.mime());
        }
        Set<String> whitelist = parseAllowedTypes(allowedTypesConfig);
        if (!whitelist.contains(normalizedType)) {
            String display = describeDetectedType(normalizedType);
            log.warn("[file-upload-security] rejected type='{}' display='{}' filename='{}' whitelist={}",
                    normalizedType, display, originalFilename, whitelist);
            throw exception(FILE_TYPE_NOT_ALLOWED, display, ALLOWED_TYPES_USER_HINT);
        }
        return new FileDetectionResult(normalizedType, detected.mime());
    }

    private FileDetectionResult detectByMagika(byte[] content, String originalFilename, String fallbackMime) {
        String suffix = resolveTempSuffix(originalFilename);
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("magika-upload-", suffix);
            Files.write(tempFile, content);
            String[] baseCommand = tokenizeCommand(magikaCommand);
            if (baseCommand.length == 0) {
                throw new IllegalStateException("上传失败：Magika 命令未配置");
            }
            Process process = new ProcessBuilder(buildMagikaCommand(baseCommand, tempFile.toString())).start();

            boolean finished = process.waitFor(magikaTimeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw exception(FILE_MAGIKA_TIMEOUT);
            }
            String stdout = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            if (process.exitValue() != 0) {
                log.error("[file-upload-security] magika execute failed, stderr={}", stderr.trim());
                throw exception(FILE_MAGIKA_UNAVAILABLE);
            }
            FileDetectionResult parsed = parseMagikaJson(stdout, fallbackMime);
            if (parsed.detectedType() == null || parsed.detectedType().isBlank()) {
                throw exception(FILE_MAGIKA_RESULT_INVALID);
            }
            return parsed;
        } catch (cn.cheers.x.framework.common.exception.ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("[file-upload-security] magika unavailable", ex);
            throw exception(FILE_MAGIKA_UNAVAILABLE);
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (Exception ignored) {
                    // no-op
                }
            }
        }
    }

    private static FileDetectionResult emptyFileResult(String fallbackMime) {
        String mime = fallbackMime == null ? "" : fallbackMime.trim();
        if (mime.isBlank()) {
            mime = "application/octet-stream";
        }
        return new FileDetectionResult("empty", mime);
    }

    private static String[] tokenizeCommand(String command) {
        return Arrays.stream(command.trim().split("\\s+"))
                .filter(part -> !part.isBlank())
                .toArray(String[]::new);
    }

    private static List<String> buildMagikaCommand(String[] base, String filePath) {
        List<String> cmd = new ArrayList<>(base.length + 2);
        cmd.addAll(Arrays.asList(base));
        cmd.add("--json");
        cmd.add(filePath);
        return cmd;
    }

    private static FileDetectionResult parseMagikaJson(String json, String fallbackMime) throws Exception {
        JsonNode root = OBJECT_MAPPER.readTree(json);
        JsonNode entry = root.isArray() ? root.path(0) : root;
        // Magika 0.x：顶层 output.label；1.x：result.value.output.label
        JsonNode output = firstPresent(
                entry.path("output"),
                entry.path("result").path("value").path("output"),
                entry.path("result").path("value").path("dl"));
        String label = text(output, "label");
        if (label.isBlank()) {
            label = text(entry, "label");
        }
        String mime = text(output, "mime_type");
        if (mime.isBlank()) {
            mime = text(output, "mimeType");
        }
        if (mime.isBlank()) {
            mime = fallbackMime == null ? "" : fallbackMime.trim();
        }
        return new FileDetectionResult(label, mime);
    }

    private static JsonNode firstPresent(JsonNode... nodes) {
        for (JsonNode node : nodes) {
            if (node != null && !node.isMissingNode() && !node.isNull() && node.isObject()) {
                return node;
            }
        }
        return OBJECT_MAPPER.nullNode();
    }

    private static String text(JsonNode node, String field) {
        if (node == null || node.isMissingNode()) return "";
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) return "";
        return value.asText("").trim();
    }

    private static String resolveTempSuffix(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) return ".bin";
        int dot = originalFilename.lastIndexOf('.');
        if (dot < 0 || dot == originalFilename.length() - 1) return ".bin";
        String ext = originalFilename.substring(dot).trim();
        if (ext.length() > 12) return ".bin";
        return ext;
    }

    static Set<String> parseAllowedTypes(String allowedTypes) {
        Set<String> out = new LinkedHashSet<>();
        Arrays.stream(allowedTypes.split(","))
                .map(String::trim)
                .filter(v -> !v.isBlank())
                .map(FileUploadSecurityService::normalizeType)
                .forEach(out::add);
        return out;
    }

    static String normalizeType(String type) {
        String value = type == null ? "" : type.trim().toLowerCase(Locale.ROOT);
        if (value.equals("markdown")) return "md";
        if (value.equals("msword")) return "doc";
        if (value.equals("plain_text") || value.equals("plaintext") || value.equals("text") || value.equals("ascii")) {
            return "txt";
        }
        if (value.equals("7z") || value.equals("7zip")) return "sevenzip";
        if (value.equals("gz")) return "gzip";
        if (value.equals("bz2") || value.equals("bzip2")) return "bzip";
        return value;
    }

    static String describeDetectedType(String normalizedType) {
        String key = normalizeType(normalizedType);
        if (key.isBlank()) {
            return "无法识别的类型";
        }
        String named = TYPE_DISPLAY_NAMES.get(key);
        if (named != null) {
            return named;
        }
        return "未知类型（" + key + "）";
    }

    private static Map<String, String> typeDisplayNames() {
        Map<String, String> names = new LinkedHashMap<>();
        names.put("pdf", "PDF 文档");
        names.put("doc", "Word 文档");
        names.put("docx", "Word 文档");
        names.put("xls", "Excel 表格");
        names.put("xlsx", "Excel 表格");
        names.put("ppt", "PPT 演示文稿");
        names.put("pptx", "PPT 演示文稿");
        names.put("txt", "文本");
        names.put("md", "Markdown");
        names.put("zip", "ZIP 压缩包");
        names.put("rar", "RAR 压缩包");
        names.put("sevenzip", "7z 压缩包");
        names.put("tar", "TAR 压缩包");
        names.put("gzip", "GZ 压缩包");
        names.put("bzip", "BZ2 压缩包");
        names.put("xz", "XZ 压缩包");
        names.put("empty", "空文件");
        names.put("unknown", "无法识别的类型");
        names.put("undefined", "无法识别的类型");
        names.put("png", "图片（PNG）");
        names.put("jpeg", "图片（JPEG）");
        names.put("jpg", "图片（JPEG）");
        names.put("gif", "图片（GIF）");
        names.put("webp", "图片（WebP）");
        names.put("bmp", "图片（BMP）");
        names.put("exe", "可执行文件");
        names.put("pebin", "可执行文件");
        names.put("elf", "可执行文件");
        names.put("apk", "安装包（APK）");
        names.put("dmg", "磁盘镜像");
        names.put("iso", "光盘镜像");
        return names;
    }
}
