package cn.cheers.x.device.protocolgateway.vehicle;

import cn.cheers.x.device.protocolgateway.api.vehicle.VehicleGateCommandResp;
import cn.cheers.x.device.protocolgateway.datacollection.DeviceProtocolVersionCatalog;
import cn.cheers.x.device.protocolgateway.instructiondispatch.InstructionPacketFiller;
import cn.cheers.x.device.protocolgateway.instructiondispatch.InstructionTemplate;
import cn.cheers.x.device.protocolgateway.instructiondispatch.MappingRow;
import cn.cheers.x.device.protocolgateway.instructiondispatch.ProtocolMappingCatalog;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 按指令对照发送车辆 HTTPS 指令。
 * <p>负责：设备协议版本、动作编码、已经配好的对照。地址只认说明书上的完整 http 地址。
 * <p>不负责：写进出账。巡检 MQTT 下发。
 * <p>禁止：没有对照或没有完整地址时编一个厂商主机。把相对路径拼到某家官网上。
 */
@Service
public class VehicleHttpsCommandService {

    static final String ACTION_TYPE = "action";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final Set<String> TRANSPORT_KEYS = Set.of("httpMethod", "path", "url");

    private final DeviceProtocolVersionCatalog versions;
    private final ProtocolMappingCatalog mappings;
    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;
    private final HttpExchange exchange;

    public VehicleHttpsCommandService(
            DeviceProtocolVersionCatalog versions,
            ProtocolMappingCatalog mappings,
            EntityRpcApi entityRpcApi,
            ObjectMapper objectMapper) {
        this(versions, mappings, entityRpcApi, objectMapper, VehicleHttpsCommandService::exchange);
    }

    VehicleHttpsCommandService(
            DeviceProtocolVersionCatalog versions,
            ProtocolMappingCatalog mappings,
            EntityRpcApi entityRpcApi,
            ObjectMapper objectMapper,
            HttpExchange exchange) {
        this.versions = versions;
        this.mappings = mappings;
        this.entityRpcApi = entityRpcApi;
        this.objectMapper = objectMapper;
        this.exchange = exchange;
    }

    /**
     * 发出一条车辆指令。没配对照、地址不是完整 http 地址、厂商没接受，都返回 sent=false 并写明原因。
     */
    public VehicleGateCommandResp send(String deviceCode, String actionCode, Map<String, Object> params) {
        if (deviceCode == null || deviceCode.isBlank()) {
            return failed("这台设备还没有设备编号");
        }
        if (actionCode == null || actionCode.isBlank()) {
            return failed("没有要下发的动作");
        }
        Optional<String> version = versions.firstVersion(deviceCode.trim());
        if (version.isEmpty()) {
            return failed("这台设备还没有协议版本");
        }
        EntityRespDTO action = actionOf(actionCode.trim());
        if (action == null || action.getId() == null || action.getId() <= 0) {
            return failed("动作库里还没有编码为「" + actionCode.trim() + "」的动作");
        }
        MappingRow mapping;
        try {
            mapping = mappings.find(action.getId(), version.get()).orElse(null);
        } catch (RuntimeException ex) {
            String text = ex.getMessage() == null ? "读不到指令对照" : ex.getMessage();
            return failed(text);
        }
        if (mapping == null) {
            return failed("「" + actionCode.trim() + "」还没有指令对照");
        }
        InstructionTemplate template = mappings.loadInstruction(mapping.instructionId());
        if (!"https".equalsIgnoreCase(template.communicationProtocol())) {
            return failed("这条指令不是 HTTPS，车辆进出不从这里发");
        }
        Map<String, Object> packet;
        try {
            packet = InstructionPacketFiller.fill(template.outbound(), mapping.slots(), params);
        } catch (RuntimeException ex) {
            String text = ex.getMessage() == null ? "指令没有填上" : ex.getMessage();
            return failed(text);
        }
        String address = absoluteAddress(packet);
        if (address.isEmpty()) {
            return failed("这条指令还没有服务地址");
        }
        String method = text(packet.get("httpMethod"));
        if (method.isEmpty()) {
            method = "POST";
        }
        String body = jsonBody(packet);
        try {
            int status = exchange.send(method, address, body);
            if (status >= 200 && status < 300) {
                return new VehicleGateCommandResp(true, "");
            }
            return failed("厂商返回 " + status);
        } catch (Exception ex) {
            return failed("指令没有发出");
        }
    }

    /** 说明书上的 url 或 path 必须是 http 或 https 开头的完整地址。相对路径不当成能发。 */
    static String absoluteAddress(Map<String, Object> packet) {
        String url = text(packet.get("url"));
        if (isAbsolute(url)) {
            return url;
        }
        String path = text(packet.get("path"));
        if (isAbsolute(path)) {
            return path;
        }
        return "";
    }

    private String jsonBody(Map<String, Object> packet) {
        Map<String, Object> body = new LinkedHashMap<>();
        Object explicit = packet.get("body");
        if (explicit instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                body.put(String.valueOf(entry.getKey()), entry.getValue());
            }
        } else {
            for (Map.Entry<String, Object> entry : packet.entrySet()) {
                if (!TRANSPORT_KEYS.contains(entry.getKey())) {
                    body.put(entry.getKey(), entry.getValue());
                }
            }
        }
        if (body.isEmpty()) {
            return "";
        }
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("指令正文写不成 JSON");
        }
    }

    private EntityRespDTO actionOf(String actionCode) {
        CommonResult<EntityRespDTO> result = entityRpcApi.getEntityByCode(actionCode, ACTION_TYPE);
        if (result == null || !result.isSuccess()) {
            return null;
        }
        return result.getData();
    }

    private static boolean isAbsolute(String value) {
        return value.startsWith("http://") || value.startsWith("https://");
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private static VehicleGateCommandResp failed(String message) {
        return new VehicleGateCommandResp(false, message);
    }

    private static int exchange(String method, String address, String body) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(address)).timeout(TIMEOUT);
        if (body == null || body.isBlank()) {
            builder.method(method.toUpperCase(), HttpRequest.BodyPublishers.noBody());
        } else {
            builder.header("Content-Type", "application/json");
            builder.method(method.toUpperCase(), HttpRequest.BodyPublishers.ofString(body));
        }
        HttpResponse<String> response = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build()
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    @FunctionalInterface
    interface HttpExchange {
        int send(String method, String address, String body) throws Exception;
    }
}
