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
 * 从设备台账读协议版本数组，以及这台设备登记的默认协议。
 * 连接、监控下拉和报文里的设备编号都用设备实例的设备编号（code）。
 * <p>禁止：用 device_code 当连接编号；读失败假装有版本；多台设备对上同一设备编号还猜一台；默认协议空着时改用支持清单的第一种。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EntityRpcDeviceProtocolVersionCatalog implements DeviceProtocolVersionCatalog, DeviceRegistrationLookup {

    static final String EQUIPMENT_TYPE = "equipment";
    static final String FIELD_VERSIONS = "protocol_versions";
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
                    log.error("[device-protocol] 多台设备对上同一设备编号 deviceId={}", deviceId);
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

    /**
     * 模拟下发用这台设备登记的默认协议，不用支持清单里的第一种。
     */
    @Override
    public Registration registrationOf(String logicalDeviceId) {
        if (logicalDeviceId == null || logicalDeviceId.isBlank()) {
            return new Registration("", "必须先选一台设备");
        }
        String deviceId = logicalDeviceId.trim();
        List<DeviceRow> rows;
        try {
            rows = cache.getOrLoad(this::loadRows);
        } catch (Exception ex) {
            log.error("[device-protocol] 读不到设备台账 deviceId={}", deviceId, ex);
            return new Registration("", "读不到巡检设备台账");
        }
        DeviceRow matched = null;
        for (DeviceRow row : rows) {
            if (!row.matches(deviceId)) {
                continue;
            }
            if (matched != null) {
                return new Registration("", "多台设备对上同一设备编号");
            }
            matched = row;
        }
        if (matched == null) {
            return new Registration("", "这台设备不在巡检设备台账里");
        }
        if (matched.platformProtocol().isBlank()) {
            return new Registration("", "这台设备没有登记默认使用的协议");
        }
        return new Registration(matched.platformProtocol(), "");
    }

    /**
     * 监控页名单：已登记默认协议的设备。下拉里的编号就是这台设备实例的设备编号。
     */
    public List<NamedEquipment> equipmentWithProtocol() {
        List<NamedEquipment> rows = new ArrayList<>();
        for (DeviceRow row : loadCached()) {
            if (row.platformProtocol().isBlank() || row.code().isBlank()) {
                continue;
            }
            rows.add(new NamedEquipment(row.code(), row.name(), row.platformProtocol()));
        }
        return List.copyOf(rows);
    }

    /**
     * 按连接上的设备编号找台账。编号就是设备实例的 code。对上多台时不挑名称。
     */
    public Optional<NamedEquipment> findEquipment(String logicalDeviceId) {
        if (logicalDeviceId == null || logicalDeviceId.isBlank()) {
            return Optional.empty();
        }
        NamedEquipment matched = null;
        for (DeviceRow row : loadCached()) {
            if (!row.matches(logicalDeviceId.trim())) {
                continue;
            }
            if (matched != null) {
                return Optional.empty();
            }
            matched = new NamedEquipment(row.code(), row.name(), row.platformProtocol());
        }
        return Optional.ofNullable(matched);
    }

    private List<DeviceRow> loadCached() {
        try {
            return cache.getOrLoad(this::loadRows);
        } catch (Exception ex) {
            log.error("[device-protocol] 读不到设备台账", ex);
            return List.of();
        }
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
                stringOf(bag.get(CODE)),
                readVersions(bag.get(FIELD_VERSIONS)),
                stringOf(bag.get("platform_protocol")),
                stringOf(bag.get("name"))
        );
    }

    public static List<String> readVersions(Object raw) {
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

    record DeviceRow(String code, List<String> versions, String platformProtocol, String name) {
        boolean matches(String deviceId) {
            return deviceId.equals(code);
        }
    }

    public record NamedEquipment(String deviceId, String name, String protocol) {
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
