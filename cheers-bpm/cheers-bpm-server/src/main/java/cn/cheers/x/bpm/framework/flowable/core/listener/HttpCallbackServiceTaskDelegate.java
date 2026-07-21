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

    @Value("${bpm.callback.emergency-start-response-url:http://127.0.0.1:48085/rpc-api/emergency/process/service-task/start-response}")
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

        log.info("[httpCallbackServiceTaskDelegate] POST {} eventId={}", url, body.get("eventId"));
        HttpResponse response = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(body))
                .timeout(60_000)
                .execute();
        if (!response.isOk()) {
            throw new IllegalStateException("service task callback failed, http="
                    + response.getStatus() + ", body=" + response.body());
        }
    }
}
