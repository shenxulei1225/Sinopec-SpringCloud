package cn.iocoder.yudao.module.system.service.auth;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.AUTH_LOGIN_CAPTCHA_CODE_ERROR;

@Service
@Slf4j
public class ArithmeticCaptchaService {

    private static final String CAPTCHA_PREFIX = "system:captcha:arithmetic:";
    private static final long CAPTCHA_TTL_MINUTES = 2L;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public CaptchaResponse createArithmeticCaptcha() {
        CaptchaChallenge challenge = generateChallenge();

        String captchaKey = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(buildCaptchaRedisKey(captchaKey), challenge.getAnswer(), CAPTCHA_TTL_MINUTES, TimeUnit.MINUTES);

        return CaptchaResponse.builder()
                .captchaKey(captchaKey)
                .captchaImage(buildSvgDataUri(challenge.getExpression()))
                .build();
    }

    public void validateCaptchaVerificationOrThrow(String captchaVerification, String username) {
        if (!validateCaptchaVerification(captchaVerification, true)) {
            log.warn("[validateCaptchaVerificationOrThrow] captcha validation failed, username={}, captchaVerification={}", username, captchaVerification);
            throw exception(AUTH_LOGIN_CAPTCHA_CODE_ERROR, "验证码错误或已过期");
        }
    }

    public boolean validateCaptchaVerification(String captchaVerification, boolean deleteAfterValidation) {
        String[] parts = parseCaptchaVerification(captchaVerification);
        if (parts == null) {
            return false;
        }

        String redisKey = buildCaptchaRedisKey(parts[0]);
        String expectedCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (deleteAfterValidation) {
            stringRedisTemplate.delete(redisKey);
        }

        return StrUtil.isNotBlank(expectedCode) && StrUtil.equals(expectedCode, StrUtil.trim(parts[1]));
    }

    private String[] parseCaptchaVerification(String captchaVerification) {
        if (StrUtil.isBlank(captchaVerification)) {
            return null;
        }
        String[] parts = StrUtil.splitToArray(StrUtil.trim(captchaVerification), '@', 2);
        if (parts == null || parts.length != 2 || StrUtil.hasBlank(parts[0], parts[1])) {
            return null;
        }
        return parts;
    }

    private String buildCaptchaRedisKey(String captchaKey) {
        return CAPTCHA_PREFIX + captchaKey;
    }

    private String buildSvgDataUri(String expression) {
        String safeExpression = expression
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
        String svg = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"111\" height=\"36\" viewBox=\"0 0 111 36\">"
                + "<rect width=\"111\" height=\"36\" rx=\"6\" ry=\"6\" fill=\"#f5f7fa\"/>"
                + "<text x=\"55.5\" y=\"24\" text-anchor=\"middle\" font-size=\"20\" font-family=\"Arial, Microsoft YaHei, sans-serif\" fill=\"#303133\">"
                + safeExpression
                + "</text></svg>";
        return "data:image/svg+xml;base64," + Base64.encode(svg.getBytes(StandardCharsets.UTF_8));
    }

    private CaptchaChallenge generateChallenge() {
        int left = RandomUtil.randomInt(1, 10);
        int right = RandomUtil.randomInt(1, 10);
        boolean plus = RandomUtil.randomBoolean();
        if (plus) {
            return new CaptchaChallenge(left + "+" + right + "=?", String.valueOf(left + right));
        }
        if (left < right) {
            int temp = left;
            left = right;
            right = temp;
        }
        return new CaptchaChallenge(left + "-" + right + "=?", String.valueOf(left - right));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CaptchaResponse {
        private String captchaKey;
        private String captchaImage;
    }

    private static class CaptchaChallenge {
        private final String expression;
        private final String answer;

        private CaptchaChallenge(String expression, String answer) {
            this.expression = expression;
            this.answer = answer;
        }

        public String getExpression() {
            return expression;
        }

        public String getAnswer() {
            return answer;
        }
    }
}
