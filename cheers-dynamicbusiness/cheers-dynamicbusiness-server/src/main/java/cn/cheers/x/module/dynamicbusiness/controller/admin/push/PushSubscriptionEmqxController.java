package cn.cheers.x.module.dynamicbusiness.controller.admin.push;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.security.core.LoginUser;
import cn.cheers.x.framework.common.enums.UserTypeEnum;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.dynamicbusiness.service.push.PushSubscriptionMirrorService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 接收物联网网关转发来的页面订阅事件，并写入推送订阅目录。
 * 口令来自环境变量。没配口令就拒绝。
 * 不负责 EMQX 自己的投递。
 */
@RestController
@RequestMapping("/dynamicbusiness/push-subscription")
@Validated
public class PushSubscriptionEmqxController {

    static final String TOKEN_HEADER = "X-Push-Catalog-Token";

    @Resource
    private PushSubscriptionMirrorService pushSubscriptionMirrorService;

    @Value("${cheers.push-catalog.token:}")
    private String catalogToken;

    @PostMapping("/emqx-event")
    @PermitAll
    public CommonResult<Boolean> emqxEvent(
            @RequestHeader(value = TOKEN_HEADER, required = false) String token,
            @Valid @RequestBody EmqxEventRequest request,
            HttpServletRequest httpRequest) {
        if (!tokenMatches(token)) {
            throw new ServiceException(403, "推送目录回调口令不对");
        }
        PageUser pageUser = PageUser.parse(request.getUsername());
        LoginUser loginUser = new LoginUser();
        loginUser.setId(pageUser.userId);
        loginUser.setUserType(UserTypeEnum.ADMIN.getValue());
        SecurityFrameworkUtils.setLoginUser(loginUser, httpRequest);
        try {
            String event = request.getEvent().trim();
            switch (event) {
                case "session.subscribed" -> pushSubscriptionMirrorService.onSubscribed(
                        request.getClientId(), pageUser.userId, request.getTopic());
                case "session.unsubscribed" -> pushSubscriptionMirrorService.onUnsubscribed(
                        request.getClientId(), request.getTopic());
                case "client.disconnected" -> pushSubscriptionMirrorService.onDisconnected(request.getClientId());
                default -> throw new ServiceException(400, "不认识的推送目录事件");
            }
            return success(true);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private boolean tokenMatches(String token) {
        if (catalogToken == null || catalogToken.isBlank() || token == null || token.isBlank()) {
            return false;
        }
        return MessageDigest.isEqual(
                catalogToken.trim().getBytes(StandardCharsets.UTF_8),
                token.trim().getBytes(StandardCharsets.UTF_8));
    }

    @Data
    public static class EmqxEventRequest {
        @NotBlank
        private String event;
        @NotBlank
        private String clientId;
        @NotBlank
        private String username;
        private String topic;
    }

    /**
     * 页面连接的用户名：page:{租户编号}:{用户编号}。
     * 租户由请求头 tenant-id 交给租户过滤器。这里只取出用户编号，用来记下是谁在听。
     */
    static final class PageUser {
        final long userId;

        private PageUser(long userId) {
            this.userId = userId;
        }

        static PageUser parse(String username) {
            if (username == null || !username.startsWith("page:")) {
                throw new ServiceException(400, "页面连接的用户名无法识别，订阅没有写入目录");
            }
            String[] parts = username.split(":", 3);
            if (parts.length != 3 || !parts[1].matches("[1-9][0-9]*") || !parts[2].matches("[1-9][0-9]*")) {
                throw new ServiceException(400, "页面连接的用户名无法识别，订阅没有写入目录");
            }
            return new PageUser(Long.parseLong(parts[2]));
        }
    }
}
