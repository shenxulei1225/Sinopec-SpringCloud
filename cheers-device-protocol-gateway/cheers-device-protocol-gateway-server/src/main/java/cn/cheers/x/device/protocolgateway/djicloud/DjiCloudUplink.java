package cn.cheers.x.device.protocolgateway.djicloud;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 大疆上云入站应答：机场和 Pilot 2 上线时走这里，设备才会停在「已上云」。
 * <p>负责：拓扑更新（update_topo）、许可下发（config）、组织绑定查询与绑定。不写任务账，不下发航线。
 * <p>禁止：许可没配齐仍回一份假许可；组织没配仍编一个组织名；把认不出的方法当成成功。
 */
public class DjiCloudUplink {

    private static final Pattern STATUS = Pattern.compile("^sys/product/([^/]+)/status$");
    private static final Pattern REQUESTS = Pattern.compile("^thing/product/([^/]+)/requests$");

    private final ObjectMapper objectMapper;

    public DjiCloudUplink(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 决定要不要回这一包。
     * <ul>
     *   <li>拓扑更新：回 result=0，并带上网关序列号和当前挂着的飞机序列号</li>
     *   <li>要许可：三项都有才回；缺了不回，调用方必须让人看见缺口</li>
     *   <li>查绑定：组织没配就回答未绑定；已配则带上组织编号和名称</li>
     *   <li>取组织名或确认绑定：组织没配不回，调用方必须让人看见缺口</li>
     *   <li>其它主题或方法：不回</li>
     * </ul>
     */
    public DjiCloudDecision accept(String topic, byte[] payload, DjiCloudCredentials credentials,
                                   DjiCloudOrganization organization) {
        if (topic == null || payload == null) {
            return new DjiCloudDecision.Ignore("主题或正文为空");
        }
        JsonNode root = read(payload);
        if (root == null || !root.isObject()) {
            return new DjiCloudDecision.Ignore("正文不是 JSON 对象");
        }
        String method = text(root, "method");
        String tid = text(root, "tid");
        String bid = text(root, "bid");
        if (method.isEmpty() || tid.isEmpty() || bid.isEmpty()) {
            return new DjiCloudDecision.Ignore("缺少 method、tid 或 bid，无法对上这一次请求");
        }
        Matcher status = STATUS.matcher(topic);
        if (status.matches()) {
            return acceptStatus(status.group(1), method, tid, bid, root);
        }
        Matcher requests = REQUESTS.matcher(topic);
        if (requests.matches()) {
            return acceptRequests(requests.group(1), method, tid, bid, root, credentials,
                    organization == null ? DjiCloudOrganization.none() : organization);
        }
        return new DjiCloudDecision.Ignore("不是上线用的状态或请求主题");
    }

    /**
     * 不带组织时，绑定类请求按「尚未配置组织」处理。
     */
    public DjiCloudDecision accept(String topic, byte[] payload, DjiCloudCredentials credentials) {
        return accept(topic, payload, credentials, DjiCloudOrganization.none());
    }

    private DjiCloudDecision acceptStatus(String gatewaySn, String method, String tid, String bid, JsonNode root) {
        if (!"update_topo".equals(method)) {
            return new DjiCloudDecision.Ignore("状态主题只应答拓扑更新");
        }
        List<String> subDeviceSns = subDeviceSns(root.get("data"));
        ObjectNode data = objectMapper.createObjectNode();
        data.put("result", 0);
        return new DjiCloudDecision.Reply(
                "sys/product/" + gatewaySn + "/status_reply",
                writeReply(tid, bid, method, data),
                gatewaySn,
                List.copyOf(subDeviceSns),
                true);
    }

    private DjiCloudDecision acceptRequests(String gatewaySn, String method, String tid, String bid, JsonNode root,
                                            DjiCloudCredentials credentials, DjiCloudOrganization organization) {
        if ("config".equals(method)) {
            return configReply(gatewaySn, method, tid, bid, credentials);
        }
        if ("airport_bind_status".equals(method)) {
            return bindStatusReply(gatewaySn, method, tid, bid, root, organization);
        }
        if ("airport_organization_get".equals(method) || "airport_organization_bind".equals(method)) {
            if (organization == null || !organization.complete()) {
                return new DjiCloudDecision.MissingOrganization(gatewaySn);
            }
            ObjectNode data = objectMapper.createObjectNode();
            data.put("result", 0);
            ObjectNode output = data.putObject("output");
            if ("airport_organization_get".equals(method)) {
                output.put("organization_name", organization.name());
            }
            return reply(gatewaySn, method, tid, bid, data);
        }
        return new DjiCloudDecision.Ignore("请求主题只应答许可、绑定状态和组织绑定");
    }

    private DjiCloudDecision configReply(String gatewaySn, String method, String tid, String bid,
                                         DjiCloudCredentials credentials) {
        if (credentials == null || !credentials.licenseComplete()) {
            return new DjiCloudDecision.MissingLicense(gatewaySn);
        }
        ObjectNode data = objectMapper.createObjectNode();
        data.put("app_id", credentials.appId());
        data.put("app_key", credentials.appKey());
        data.put("app_license", credentials.appLicense());
        data.put("ntp_server_host", credentials.ntpServerHost());
        data.put("ntp_server_port", credentials.ntpServerPort());
        return reply(gatewaySn, method, tid, bid, data);
    }

    /**
     * 查询是否已绑定。组织没配时明确回答未绑定，不编组织名。
     */
    private DjiCloudDecision bindStatusReply(String gatewaySn, String method, String tid, String bid,
                                             JsonNode root, DjiCloudOrganization organization) {
        boolean bound = organization != null && organization.complete();
        ObjectNode data = objectMapper.createObjectNode();
        data.put("result", 0);
        var output = data.putObject("output");
        var list = output.putArray("bind_status");
        JsonNode devices = root.path("data").path("devices");
        if (devices.isArray()) {
            for (JsonNode device : devices) {
                String sn = text(device, "sn");
                if (sn.isEmpty()) {
                    continue;
                }
                ObjectNode item = list.addObject();
                item.put("sn", sn);
                item.put("is_device_bind_organization", bound);
                item.put("organization_id", bound ? organization.id() : "");
                item.put("organization_name", bound ? organization.name() : "");
                item.put("device_callsign", sn);
            }
        }
        return reply(gatewaySn, method, tid, bid, data);
    }

    private DjiCloudDecision reply(String gatewaySn, String method, String tid, String bid, ObjectNode data) {
        return new DjiCloudDecision.Reply(
                "thing/product/" + gatewaySn + "/requests_reply",
                writeReply(tid, bid, method, data),
                gatewaySn,
                List.of(),
                false);
    }

    private List<String> subDeviceSns(JsonNode data) {
        if (data == null || !data.isObject()) {
            return List.of();
        }
        JsonNode devices = data.get("sub_devices");
        if (devices == null || !devices.isArray()) {
            return List.of();
        }
        List<String> sns = new ArrayList<>();
        for (JsonNode device : devices) {
            String sn = text(device, "sn");
            if (!sn.isEmpty()) {
                sns.add(sn);
            }
        }
        return sns;
    }

    private String writeReply(String tid, String bid, String method, ObjectNode data) {
        ObjectNode reply = objectMapper.createObjectNode();
        reply.put("tid", tid);
        reply.put("bid", bid);
        reply.put("timestamp", System.currentTimeMillis());
        reply.put("method", method);
        reply.set("data", data);
        try {
            return objectMapper.writeValueAsString(reply);
        } catch (Exception ex) {
            throw new IllegalStateException("上云应答无法写成 JSON", ex);
        }
    }

    private JsonNode read(byte[] payload) {
        try {
            return objectMapper.readTree(payload);
        } catch (Exception ex) {
            return null;
        }
    }

    private static String text(JsonNode node, String field) {
        if (node == null || !node.hasNonNull(field)) {
            return "";
        }
        return node.get(field).asText("");
    }
}
