package cn.cheers.x.device.protocolgateway.datacollection;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 从设备台账读协议版本数组。
 * <p>禁止：读失败假装有版本；多台设备对上同一逻辑标识还猜一台。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EntityRpcDeviceProtocolVersionCatalog implements DeviceProtocolVersionCatalog {

    static final String EQUIPMENT_TYPE = "equipment";
    static final String FIELD_VERSIONS = "protocol_versions";
    static final String DEVICE_CODE = "device_code";
    static final String CODE = "code";

    private final EntityRpcApi entityRpcApi;
    private final VersionCache cache = new VersionCache();

    @Override
    public Optional<String> firstVersion(String logicalDeviceId) {
        if (logicalDeviceId == null || logicalDeviceId.isBlank()) {
            return Optional.empty();
        }
        String deviceId = logicalDeviceId.trim();
        List<DeviceRow> rows;
        try {
            rows = cache.getOrLoad(this::loadRows);
        } catch (Exception ex) {
            log.error("[device-protocol] 读不到设备协议版本 deviceId={}", deviceId, ex);
            return Optional.empty();
        }
        DeviceRow matched = null;
        for (DeviceRow row : rows) {
            if (row.matches(deviceId)) {
                if (matched != null) {
                    log.error("[device-protocol] 多台设备对上同一逻辑标识 deviceId={}", deviceId);
                    return Optional.empty();
                }
                matched = row;
            }
        }
        if (matched == null || matched.versions().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(matched.versions().get(0));
    }

    private List<DeviceRow> loadRows() {
        CommonResult<List<EntityRespDTO>> result = entityRpcApi.listEntities(EQUIPMENT_TYPE, null);
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw new IllegalStateException(result == null ? "设备台账无响应" : result.getMsg());
        }
        List<DeviceRow> rows = new ArrayList<>();
        for (EntityRespDTO entity : result.getData()) {
            DeviceRow row = parseRow(entity);
            if (row != null) {
                rows.add(row);
            }
        }
        return List.copyOf(rows);
    }

    DeviceRow parseRow(EntityRespDTO entity) {
        if (entity == null) {
            return null;
        }
        Map<String, Object> bag = bagOf(entity);
        return new DeviceRow(
                stringOf(bag.get(DEVICE_CODE)),
                stringOf(bag.get(CODE)),
                readVersions(bag.get(FIELD_VERSIONS))
        );
    }

    static List<String> readVersions(Object raw) {
        List<String> versions = new ArrayList<>();
        if (raw instanceof List<?> list) {
            for (Object item : list) {
                String value = stringOf(item);
                if (!value.isBlank()) {
                    versions.add(value);
                }
            }
            return List.copyOf(versions);
        }
        String text = stringOf(raw);
        if (text.isBlank() || "[]".equals(text)) {
            return List.of();
        }
        String inner = text;
        if (text.startsWith("[") && text.endsWith("]")) {
            inner = text.substring(1, text.length() - 1);
        }
        for (String part : inner.split(",")) {
            String value = part.trim().replace("\"", "");
            if (!value.isBlank()) {
                versions.add(value);
            }
        }
        return List.copyOf(versions);
    }

    private static Map<String, Object> bagOf(EntityRespDTO entity) {
        Map<String, Object> bag = new LinkedHashMap<>();
        if (entity.getBaseFields() != null) {
            bag.putAll(entity.getBaseFields());
        }
        if (entity.getCustomFields() != null) {
            bag.putAll(entity.getCustomFields());
        }
        return bag;
    }

    private static String stringOf(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    record DeviceRow(String deviceCode, String code, List<String> versions) {
        boolean matches(String logicalDeviceId) {
            return logicalDeviceId.equals(deviceCode) || logicalDeviceId.equals(code);
        }
    }

    static final class VersionCache {
        private static final long TTL_MS = 60_000L;
        private volatile List<DeviceRow> rows = List.of();
        private volatile long expireAtEpochMs = 0L;

        synchronized List<DeviceRow> getOrLoad(java.util.function.Supplier<List<DeviceRow>> loader) {
            long now = System.currentTimeMillis();
            if (now < expireAtEpochMs && !rows.isEmpty()) {
                return rows;
            }
            List<DeviceRow> loaded = loader.get();
            rows = loaded == null ? List.of() : List.copyOf(loaded);
            expireAtEpochMs = now + TTL_MS;
            return rows;
        }
    }
}
