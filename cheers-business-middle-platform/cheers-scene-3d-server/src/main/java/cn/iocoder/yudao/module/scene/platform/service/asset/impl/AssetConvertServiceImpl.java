package cn.iocoder.yudao.module.scene.platform.service.asset.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.asset.AssetResourceDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.asset.AssetResourceMapper;
import cn.iocoder.yudao.module.scene.platform.service.asset.AssetConvertService;
import cn.iocoder.yudao.module.scene.platform.service.asset.convert.AssetConvertProperties;
import cn.iocoder.yudao.module.scene.platform.service.asset.convert.AssetFormatConverter;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class AssetConvertServiceImpl implements AssetConvertService {

    public static final String METADATA_RUNTIME_URL = "runtimeUrl";
    public static final String METADATA_CONVERT_STATUS = "convertStatus";
    public static final String METADATA_CONVERT_MESSAGE = "convertMessage";
    public static final String METADATA_CONVERTER_NAME = "converterName";

    public static final String STATUS_READY = "ready";
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_FAILED = "failed";

    @Resource
    private AssetResourceMapper assetResourceMapper;

    @Resource
    private List<AssetFormatConverter> converters;

    @Resource
    private AssetConvertProperties convertProperties;

    @Value("${yudao.scene-platform.asset-upload.local-base-path:}")
    private String localBasePath;

    @Value("${yudao.scene-platform.asset-upload.local-base-url:}")
    private String localBaseUrl;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void convertToGlb(Long assetId) {
        AssetResourceDO asset = assetResourceMapper.selectById(assetId);
        if (asset == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "资源不存在");
        }

        String format = resolveFormat(asset.getFormat(), asset.getAssetUrl());
        Map<String, Object> metadata = parseMetadata(asset.getMetadataJson());

        if (!convertProperties.isEnabled()) {
            metadata.put(METADATA_CONVERT_STATUS, STATUS_PENDING);
            metadata.put(METADATA_CONVERT_MESSAGE, "资产转换总开关已关闭");
            metadata.remove(METADATA_RUNTIME_URL);
            persist(asset, metadata);
            return;
        }

        // 老栈导入：优先用 legacyModelHint 真源，勿把占位 GLB 当已转换完成
        Path legacySource = resolveLegacyHintSource(metadata);
        if (legacySource != null) {
            String hintFormat = resolveFormat(null, legacySource.getFileName().toString());
            Optional<AssetFormatConverter> hintConverter = selectConverter(hintFormat);
            if (hintConverter.isEmpty() && ("glb".equals(hintFormat) || "gltf".equals(hintFormat))) {
                // hint 本身已是 glb
                metadata.put(METADATA_RUNTIME_URL, legacySource.toUri().toString());
                metadata.put(METADATA_CONVERT_STATUS, STATUS_READY);
                metadata.put(METADATA_CONVERTER_NAME, "legacy-hint-passthrough");
                metadata.remove(METADATA_CONVERT_MESSAGE);
                persist(asset, metadata);
                return;
            }
            if (hintConverter.isPresent()) {
                convertAndPersist(asset, metadata, hintConverter.get(), legacySource);
                return;
            }
            metadata.put(METADATA_CONVERT_STATUS, "fbx".equals(hintFormat) ? STATUS_PENDING : STATUS_FAILED);
            metadata.put(METADATA_CONVERT_MESSAGE,
                    "legacyModelHint 格式 " + hintFormat + " 无可用转换器");
            metadata.remove(METADATA_RUNTIME_URL);
            persist(asset, metadata);
            return;
        }

        // glb/gltf 远程或本地：直通源 URL，无需落盘
        if ("glb".equals(format) || "gltf".equals(format)) {
            metadata.put(METADATA_RUNTIME_URL, asset.getAssetUrl());
            metadata.put(METADATA_CONVERT_STATUS, STATUS_READY);
            metadata.put(METADATA_CONVERTER_NAME, "builtin-passthrough");
            metadata.remove(METADATA_CONVERT_MESSAGE);
            persist(asset, metadata);
            return;
        }

        Optional<AssetFormatConverter> converterOpt = selectConverter(format);
        if (converterOpt.isEmpty()) {
            if ("fbx".equals(format)) {
                metadata.put(METADATA_CONVERT_STATUS, STATUS_PENDING);
                metadata.put(METADATA_CONVERT_MESSAGE,
                        "FBX 需安装并启用外部转换插件（如 Assimp/Blender），见 yudao.scene-platform.asset-convert.plugins.fbx");
            } else {
                metadata.put(METADATA_CONVERT_STATUS, STATUS_FAILED);
                metadata.put(METADATA_CONVERT_MESSAGE,
                        StrUtil.isBlank(format) ? "无法识别源文件格式" : "暂无可用转换器支持格式 " + format);
            }
            metadata.remove(METADATA_RUNTIME_URL);
            persist(asset, metadata);
            return;
        }

        convertAndPersist(asset, metadata, converterOpt.get(), null);
    }

    private void convertAndPersist(AssetResourceDO asset, Map<String, Object> metadata,
                                   AssetFormatConverter converter, Path preferredSource) {
        try {
            Path source = preferredSource != null ? preferredSource : resolveSourceFile(asset);
            Path workDir = Paths.get(convertProperties.getWorkDir());
            Files.createDirectories(workDir);
            Path target = workDir.resolve(asset.getId() + "-" + System.currentTimeMillis() + ".glb");
            converter.convert(source, target);
            String runtimeUrl = publishGlb(target);
            metadata.put(METADATA_RUNTIME_URL, runtimeUrl);
            metadata.put(METADATA_CONVERT_STATUS, STATUS_READY);
            metadata.put(METADATA_CONVERTER_NAME, converter.name());
            metadata.remove(METADATA_CONVERT_MESSAGE);
            persist(asset, metadata);
        } catch (Exception ex) {
            metadata.put(METADATA_CONVERT_STATUS, STATUS_FAILED);
            metadata.put(METADATA_CONVERT_MESSAGE, StrUtil.blankToDefault(ex.getMessage(), ex.getClass().getSimpleName()));
            metadata.put(METADATA_CONVERTER_NAME, converter.name());
            metadata.remove(METADATA_RUNTIME_URL);
            persist(asset, metadata);
        }
    }

    private static Path resolveLegacyHintSource(Map<String, Object> metadata) {
        Object hint = metadata.get("legacyModelHint");
        if (hint == null) {
            return null;
        }
        Path hintPath = Paths.get(String.valueOf(hint));
        return Files.isRegularFile(hintPath) ? hintPath : null;
    }

    private Optional<AssetFormatConverter> selectConverter(String format) {
        if (converters == null || converters.isEmpty()) {
            return Optional.empty();
        }
        return converters.stream()
                .filter(c -> c.supports(format))
                .max(Comparator.comparingInt(AssetFormatConverter::priority));
    }

    private Path resolveSourceFile(AssetResourceDO asset) throws Exception {
        String url = StrUtil.blankToDefault(asset.getAssetUrl(), "");
        if (url.startsWith("file:")) {
            return Paths.get(URI.create(url));
        }
        Path asPath = Paths.get(url);
        if (Files.isRegularFile(asPath)) {
            return asPath;
        }
        if (StrUtil.isNotBlank(localBasePath)) {
            Path underUpload = Paths.get(localBasePath, "scene-assets", FileUtil.getName(url));
            if (Files.isRegularFile(underUpload)) {
                return underUpload;
            }
        }
        Object hint = parseMetadata(asset.getMetadataJson()).get("legacyModelHint");
        if (hint != null) {
            Path hintPath = Paths.get(String.valueOf(hint));
            if (Files.isRegularFile(hintPath)) {
                return hintPath;
            }
        }
        throw new IllegalStateException("无法解析本地源文件（远程 URL 请先落盘上传）: " + url);
    }

    private String publishGlb(Path glb) throws Exception {
        Path published = glb;
        if (StrUtil.isNotBlank(localBasePath)) {
            published = Paths.get(localBasePath, "scene-assets", glb.getFileName().toString());
            Files.createDirectories(published.getParent());
            Files.copy(glb, published, StandardCopyOption.REPLACE_EXISTING);
        }
        String base = StrUtil.blankToDefault(convertProperties.getOutputBaseUrl(), localBaseUrl);
        if (StrUtil.isBlank(base) && StrUtil.isNotBlank(localBasePath)) {
            base = "file://" + localBasePath + "/scene-assets/";
        }
        if (StrUtil.isNotBlank(base)) {
            if (!base.endsWith("/")) {
                base = base + "/";
            }
            return base + published.getFileName();
        }
        return published.toUri().toString();
    }

    private void persist(AssetResourceDO asset, Map<String, Object> metadata) {
        asset.setMetadataJson(JsonUtils.toJsonString(metadata));
        assetResourceMapper.updateById(asset);
    }

    static String resolveFormat(String format, String assetUrl) {
        if (StrUtil.isNotBlank(format)) {
            return format.trim().toLowerCase(Locale.ROOT);
        }
        if (StrUtil.isBlank(assetUrl)) {
            return "";
        }
        String ext = FileUtil.extName(assetUrl);
        return StrUtil.isBlank(ext) ? "" : ext.trim().toLowerCase(Locale.ROOT);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseMetadata(String metadataJson) {
        if (StrUtil.isBlank(metadataJson)) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> parsed = JsonUtils.parseObject(metadataJson, Map.class);
        return parsed != null ? new LinkedHashMap<>(parsed) : new LinkedHashMap<>();
    }
}
