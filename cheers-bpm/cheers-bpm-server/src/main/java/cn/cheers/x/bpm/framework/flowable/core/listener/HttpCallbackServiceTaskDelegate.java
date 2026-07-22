package cn.cheers.x.bpm.framework.flowable.core.listener;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用 HTTP 回调委派：按流程变量 eventId 回调业务服务任务 URL。
 * <p>
 * 默认用于应急启动响应；URL 可通过流程变量 {@code serviceTaskCallbackUrl} 覆盖，
 * 或配置 {@code bpm.callback.emergency-start-response-url}。
 */
@Component("httpCallbackServiceTaskDelegate")
@Slf4j
public class HttpCallbackServiceTaskDelegate implements JavaDelegate {

    @Value("${bpm.callback.emergency-start-response-url:http://127.0.0.1:48096/rpc-api/emergency/process/service-task/start-response}")
    private String defaultEmergencyStartResponseUrl;

    @Override
    public void execute(DelegateExecution execution) {
        Object eventIdVar = execution.getVariable("eventId");
        if (eventIdVar == null) {
            throw new IllegalStateException("process variable eventId is required for service task callback");
        }
        Object overrideUrl = execution.getVariable("serviceTaskCallbackUrl");
        String url = overrideUrl != null && !overrideUrl.toString().isBlank()
                ? overrideUrl.toString()
                : defaultEmergencyStartResponseUrl;

        Map<String, Object> body = new HashMap<>();
        body.put("eventId", eventIdVar instanceof Number
                ? ((Number) eventIdVar).longValue()
                : Long.parseLong(eventIdVar.toString()));
        Object responseLevelVar = execution.getVariable("responseLevel");
        if (responseLevelVar != null && !responseLevelVar.toString().isBlank()) {
            body.put("responseLevel", responseLevelVar.toString().trim());
        }

        Object tenantIdVar = execution.getVariable("tenantId");
        String tenantId = tenantIdVar != null && !tenantIdVar.toString().isBlank()
                ? tenantIdVar.toString()
                : "1";

        // 编排等下游 RPC 需登录态；由业务 complete 时写入流程变量 accessToken
        Object accessTokenVar = execution.getVariable("accessToken");
        String authorization = null;
        if (accessTokenVar != null && !accessTokenVar.toString().isBlank()) {
            String token = accessTokenVar.toString().trim();
            authorization = token.regionMatches(true, 0, "Bearer ", 0, 7)
                    ? token
                    : "Bearer " + token;
        }

        log.info("[httpCallbackServiceTaskDelegate] POST {} eventId={} responseLevel={} auth={}",
                url, body.get("eventId"), body.get("responseLevel"), authorization != null);
        HttpRequest httpRequest = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .header("tenant-id", tenantId)
                .body(JSONUtil.toJsonStr(body))
                .timeout(60_000);
        if (authorization != null) {
            httpRequest.header("Authorization", authorization);
        }
        HttpResponse response = httpRequest.execute();
        if (!response.isOk()) {
            throw new IllegalStateException("service task callback failed, http="
                    + response.getStatus() + ", body=" + response.body());
        }
        // 业务信封：HTTP 200 仍可能 code!=0/200，须显式失败以免流程空转结束
        String respBody = response.body();
        if (respBody != null && !respBody.isBlank()) {
            cn.hutool.json.JSONObject json = JSONUtil.parseObj(respBody);
            Integer code = json.getInt("code");
            if (code != null && code != 0 && code != 200) {
                throw new IllegalStateException("service task callback business failed, code="
                        + code + ", msg=" + json.getStr("msg") + ", body=" + respBody);
            }
        }
    }
}
