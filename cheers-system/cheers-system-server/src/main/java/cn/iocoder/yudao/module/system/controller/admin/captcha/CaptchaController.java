package cn.iocoder.yudao.module.system.controller.admin.captcha;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.system.service.auth.ArithmeticCaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "管理后台 - 验证码")
@RestController("adminCaptchaController")
@RequestMapping("/system/captcha")
public class CaptchaController {

    @Resource
    private ArithmeticCaptchaService arithmeticCaptchaService;

    @PostMapping({"/get"})
    @Operation(summary = "获得验证码")
    @PermitAll
    @TenantIgnore
    public ArithmeticCaptchaService.CaptchaResponse get(@RequestBody(required = false) Map<String, Object> data) {
        return arithmeticCaptchaService.createArithmeticCaptcha();
    }

    @PostMapping("/check")
    @Operation(summary = "校验验证码")
    @PermitAll
    @TenantIgnore
    public boolean check(@RequestBody Map<String, Object> data) {
        String captchaVerification = data == null ? null : (String) data.get("captchaVerification");
        return arithmeticCaptchaService.validateCaptchaVerification(captchaVerification, true);
    }

    public static String getRemoteId(HttpServletRequest request) {
        String ip = ServletUtils.getClientIP(request);
        String ua = request.getHeader("user-agent");
        if (StrUtil.isNotBlank(ip)) {
            return ip + ua;
        }
        return request.getRemoteAddr() + ua;
    }

}
