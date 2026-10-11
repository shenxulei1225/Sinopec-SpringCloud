package cn.cheers.x.intelligentanalysis.service.provider;

import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisCapabilityDO;
import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisProviderDO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 现有 detection 服务的 multipart 适配器。
 *
 * <p>负责把标准分析任务转换成 {@code /upload_tactics}。媒体地址必须命中管理员配置的允许主机。</p>
 * <p>不负责保存任务、选择能力或写业务结果。禁止从动作名称生成算法编号。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LegacyDetectionProviderAdapter implements AnalysisProviderAdapter {

    public static final String ADAPTER_TYPE = "LEGACY_DETECTION_MULTIPART";
    private static final int MAX_MEDIA_BYTES = 32 * 1024 * 1024;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            // 媒体主机是管理员明确允许的边界。禁止跟随跳转绕到未登记主机。
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    @Override
    public boolean supports(String adapterType) {
        return ADAPTER_TYPE.equals(adapterType);
    }

    @Override
    public String configurationError(AnalysisProviderDO provider) {
        try {
            Map<String, Object> config = readMap(provider.getConfigJson());
            if (!StringUtils.hasText(provider.getBaseUrl())) {
                return "服务接入缺少服务地址";
            }
            if (!StringUtils.hasText(text(config.get("markId")))) {
                return "服务接入缺少服务实例标识";
            }
            if (!StringUtils.hasText(text(config.get("callbackUrl")))) {
                return "服务接入缺少结果回调地址";
            }
            if (stringList(config.get("allowedMediaHosts")).isEmpty()) {
                return "服务接入缺少允许读取的媒体主机";
            }
            return null;
        } catch (Exception ex) {
            return "服务接入配置不是合法 JSON";
        }
    }

    @Override
    public ProviderSubmitResult submit(
            AnalysisProviderDO provider,
            AnalysisCapabilityDO capability,
            ProviderSubmitRequest request) {
        try {
            Map<String, Object> config = readMap(provider.getConfigJson());
            String markId = text(config.get("markId"));
            String callbackUrl = text(config.get("callbackUrl"));
            List<String> allowedMediaHosts = stringList(config.get("allowedMediaHosts"));
            String configurationError = configurationError(provider);
            if (configurationError != null) {
                return ProviderSubmitResult.rejected(configurationError);
            }
            URI mediaUri = requireAllowedMediaUri(request.inputRef(), allowedMediaHosts);
            byte[] media = download(mediaUri);
            if (media.length == 0) {
                return ProviderSubmitResult.rejected("媒体文件为空");
            }
            List<Integer> roi = resolveRoi(request.inputType(), request.parameters(), media);
            String filename = filename(mediaUri, request.inputType(), request.jobNo());
            MultipartBody multipart = multipart(
                    "AnalysisBoundary" + System.nanoTime(),
                    markId,
                    callbackUrl,
                    capability.getProviderCapabilityCode(),
                    request,
                    filename,
                    roi,
                    media);
            HttpResponse<String> response = httpClient.send(
                    HttpRequest.newBuilder(uploadUri(provider.getBaseUrl()))
                            .timeout(Duration.ofSeconds(30))
                            .header("Content-Type", "multipart/form-data; boundary=" + multipart.boundary())
                            .POST(HttpRequest.BodyPublishers.ofByteArray(multipart.body()))
                            .build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return ProviderSubmitResult.rejected("分析服务返回 HTTP " + response.statusCode());
            }
            JsonNode root = objectMapper.readTree(response.body());
            if (root.path("code").asInt(-1) != 0) {
                return ProviderSubmitResult.rejected(
                        textOr(root.path("msg").asText(), "分析服务未受理"));
            }
            return ProviderSubmitResult.accepted(request.jobNo());
        } catch (IllegalArgumentException ex) {
            return ProviderSubmitResult.rejected(ex.getMessage());
        } catch (Exception ex) {
            log.error("[intelligent-analysis][submit][jobNo={}][reason={}]",
                    request.jobNo(), ex.getMessage());
            return ProviderSubmitResult.rejected("调用分析服务失败");
        }
    }

    private URI requireAllowedMediaUri(String inputRef, List<String> allowedHosts) {
        if (allowedHosts.isEmpty()) {
            throw new IllegalArgumentException("服务接入尚未配置允许读取的媒体主机");
        }
        URI uri = URI.create(inputRef);
        String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
        String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
        if (!("http".equals(scheme) || "https".equals(scheme)) || host.isBlank()) {
            throw new IllegalArgumentException("媒体引用必须是已登记主机上的 HTTP 地址");
        }
        boolean allowed = allowedHosts.stream()
                .map(value -> value == null ? "" : value.trim().toLowerCase(Locale.ROOT))
                .anyMatch(host::equals);
        if (!allowed) {
            throw new IllegalArgumentException("媒体主机不在服务接入允许名单中");
        }
        return uri;
    }

    private byte[] download(URI uri) throws Exception {
        HttpResponse<byte[]> response = httpClient.send(
                HttpRequest.newBuilder(uri)
                        .timeout(Duration.ofSeconds(30))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalArgumentException("读取媒体失败，HTTP " + response.statusCode());
        }
        byte[] body = response.body();
        if (body == null || body.length == 0 || body.length > MAX_MEDIA_BYTES) {
            throw new IllegalArgumentException("媒体为空或超过 32MB");
        }
        return body;
    }

    private List<Integer> resolveRoi(
            String inputType, Map<String, Object> parameters, byte[] media) throws Exception {
        List<Integer> configured = integerList(parameters == null ? null : parameters.get("roi"));
        if (configured.size() == 4) {
            return configured;
        }
        if (!"IMAGE".equalsIgnoreCase(inputType)) {
            throw new IllegalArgumentException("录像分析必须提供四项 roi 像素坐标");
        }
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(media));
        if (image == null) {
            throw new IllegalArgumentException("无法读取图片尺寸");
        }
        return List.of(0, 0, image.getWidth(), image.getHeight());
    }

    private MultipartBody multipart(
            String boundary,
            String markId,
            String callbackUrl,
            String providerCapabilityCode,
            ProviderSubmitRequest request,
            String filename,
            List<Integer> roi,
            byte[] media) throws Exception {
        Map<String, Object> box = new LinkedHashMap<>();
        box.put("name", providerCapabilityCode);
        box.put("display_name", providerCapabilityCode);
        box.put("box", roi);
        box.put("id", 1);
        box.put("pointId", textOr(request.businessRefId(), request.jobNo()));
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("correct", "");
        info.put("boxs", List.of(box));

        StringBuilder text = new StringBuilder();
        textField(text, boundary, "mark_id", markId);
        textField(text, boundary, "filename", filename);
        textField(text, boundary, "algorithmTempId", request.jobNo());
        textField(text, boundary, "callbackUrl", callbackUrl);
        textField(text, boundary, "info", objectMapper.writeValueAsString(info));
        text.append("--").append(boundary).append("\r\n")
                .append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                .append(filename).append("\"\r\n")
                .append("Content-Type: application/octet-stream\r\n\r\n");
        byte[] head = text.toString().getBytes(StandardCharsets.UTF_8);
        byte[] tail = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);
        byte[] all = new byte[head.length + media.length + tail.length];
        System.arraycopy(head, 0, all, 0, head.length);
        System.arraycopy(media, 0, all, head.length, media.length);
        System.arraycopy(tail, 0, all, head.length + media.length, tail.length);
        return new MultipartBody(boundary, all);
    }

    private record MultipartBody(String boundary, byte[] body) {
    }

    private static void textField(StringBuilder text, String boundary, String name, String value) {
        text.append("--").append(boundary).append("\r\n")
                .append("Content-Disposition: form-data; name=\"").append(name)
                .append("\"\r\n\r\n")
                .append(value == null ? "" : value)
                .append("\r\n");
    }

    private Map<String, Object> readMap(String json) throws Exception {
        if (!StringUtils.hasText(json)) {
            return Map.of();
        }
        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }

    private static List<String> stringList(Object raw) {
        if (!(raw instanceof List<?> list)) {
            return List.of();
        }
        return list.stream().map(LegacyDetectionProviderAdapter::text)
                .filter(StringUtils::hasText).toList();
    }

    private static List<Integer> integerList(Object raw) {
        if (!(raw instanceof List<?> list) || list.size() != 4) {
            return List.of();
        }
        try {
            return list.stream().map(value -> Integer.parseInt(String.valueOf(value))).toList();
        } catch (NumberFormatException ex) {
            return List.of();
        }
    }

    private static URI uploadUri(String baseUrl) {
        String base = baseUrl.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return URI.create(base + "/upload_tactics");
    }

    private static String filename(URI uri, String inputType, String jobNo) {
        String path = uri.getPath();
        String name = path == null ? "" : path.substring(path.lastIndexOf('/') + 1);
        if (StringUtils.hasText(name)) {
            return name;
        }
        return jobNo + ("VIDEO".equalsIgnoreCase(inputType) ? ".mp4" : ".jpg");
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private static String textOr(String value, String fallback) {
        return StringUtils.hasText(value) ? value.trim() : fallback;
    }
}
