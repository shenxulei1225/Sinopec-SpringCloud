package cn.iocoder.yudao.module.emergency.framework.rpc.config;

import cn.cheers.x.framework.common.biz.infra.logger.ApiAccessLogCommonApi;
import cn.cheers.x.framework.common.biz.infra.logger.dto.ApiAccessLogCreateReqDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Feign 测试控制器
 *
 * 用于测试 Feign 客户端调用 infra-server
 */
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class FeignTestController {

    private final ApiAccessLogCommonApi apiAccessLogApi;

    @GetMapping("/feign")
    public String testFeign() {
        try {
            log.info("开始测试 Feign 调用 infra-server...");

            ApiAccessLogCreateReqDTO dto = new ApiAccessLogCreateReqDTO();
            dto.setTraceId("test-feign-" + System.currentTimeMillis());
            dto.setUserId(1L);
            dto.setUserType(2); // 使用正确的用户类型，2表示会员用户
            dto.setApplicationName("emergency-server");
            dto.setRequestMethod("GET");
            dto.setRequestUrl("/admin-api/emergency/test/feign");
            dto.setRequestParams("{}");
            dto.setUserIp("127.0.0.1");
            dto.setUserAgent("test-agent");
            dto.setBeginTime(LocalDateTime.now().minusSeconds(1));
            dto.setEndTime(LocalDateTime.now());
            dto.setDuration(1000); // 确保是int类型
            dto.setResultCode(0);
            dto.setResultMsg("测试 Feign 调用");

            log.info("准备调用 apiAccessLogApi.createApiAccessLog...");
            apiAccessLogApi.createApiAccessLog(dto);
            log.info("Feign 调用成功！");

            return "Feign 调用成功！";
        } catch (Exception e) {
            log.error("Feign 调用失败", e);
            log.error("异常类型: {}", e.getClass().getName());
            log.error("异常消息: {}", e.getMessage());
            if (e.getCause() != null) {
                log.error("根本原因: {}", e.getCause().getMessage());
            }
            return "Feign 调用失败: " + e.getMessage() + " (类型: " + e.getClass().getSimpleName() + ")";
        }
    }

        @GetMapping("/feign-async")
        public String testFeignAsync() {
            try {
                log.info("开始测试异步 Feign 调用 infra-server...");

                ApiAccessLogCreateReqDTO dto = new ApiAccessLogCreateReqDTO();
                dto.setTraceId("test-async-feign-" + System.currentTimeMillis());
                dto.setUserId(1L);
                dto.setUserType(2); // 使用正确的用户类型
                dto.setApplicationName("emergency-server");
                dto.setRequestMethod("GET");
                dto.setRequestUrl("/admin-api/emergency/test/feign-async");
                dto.setRequestParams("{}");
                dto.setUserIp("127.0.0.1");
                dto.setUserAgent("test-agent");
                dto.setBeginTime(LocalDateTime.now().minusSeconds(1));
                dto.setEndTime(LocalDateTime.now());
                dto.setDuration(1000);
                dto.setResultCode(0);
                dto.setResultMsg("测试异步 Feign 调用");

                log.info("准备调用 apiAccessLogApi.createApiAccessLogAsync...");
                apiAccessLogApi.createApiAccessLogAsync(dto);
                log.info("异步 Feign 调用已发起！");

                return "异步 Feign 调用已发起！";
            } catch (Exception e) {
                log.error("异步 Feign 调用失败", e);
                return "异步 Feign 调用失败: " + e.getMessage();
            }
        }

}
