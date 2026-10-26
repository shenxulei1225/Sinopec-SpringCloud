package cn.cheers.x.iot.gateway.protocol.emqx;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 把页面连接的订阅、取消、断开告诉推送订阅目录。
 * 地址或口令没配时不发送，并记一条错误。EMQX 自己的订阅不受影响。
 * 不负责查询目录来决定投递给谁。
 */
@Slf4j
@Component
public class PushSubscriptionCatalogNotifier {

    static final String TOKEN_HEADER = "X-Push-Catalog-Token";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    public void forward(String catalogUrl, String catalogToken, String event, String clientId,
                        String username, String topic) {
        if (StrUtil.hasBlank(catalogUrl, catalogToken)) {
            log.error("[forward][推送目录地址或口令没配，订阅没有写入目录][event={}, clientId={}]", event, clientId);
            return;
        }
        PushPageConnection.Identity identity = PushPageConnection.parseUsername(username);
        if (identity == null) {
            log.error("[forward][页面连接的用户名无法识别，订阅没有写入目录][username={}]", username);
            return;
        }
        JSONObject body = new JSONObject();
        body.set("event", event);
        body.set("clientId", clientId);
        body.set("username", username);
        body.set("topic", topic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(catalogUrl.trim()))
                .timeout(Duration.ofSeconds(3))
                .header("Content-Type", "application/json")
                .header("tenant-id", Long.toString(identity.tenantId()))
                .header(TOKEN_HEADER, catalogToken.trim())
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.error("[forward][推送目录没有收下这次订阅事件][status={}, body={}]",
                        response.statusCode(), response.body());
            }
        } catch (Exception ex) {
            log.error("[forward][推送目录回调失败][event={}, clientId={}]", event, clientId, ex);
        }
    }
}
