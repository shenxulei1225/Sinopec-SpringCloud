package cn.cheers.x.module.dynamicbusiness.service.capability;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * System 资源 → 写请求 VO 类名映射（与 Controller SaveReqVO / MenuSaveVO 对齐）。
 */
public final class SystemVoSchemaRegistry {

    public record VoBinding(String saveVoClassName, Set<String> createOnlyFieldKeys) {
        public VoBinding(String saveVoClassName) {
            this(saveVoClassName, Set.of());
        }
    }

    private static final Map<String, VoBinding> BY_RESOURCE = build();

    /** 无 Save VO 的只读资源，不生成写 endpoint */
    private static final Set<String> READ_ONLY_RESOURCES = Set.of(
            "loginLog", "operateLog", "notifyMessage", "notifyMessageMy",
            "mailLog", "smsLog", "oauth2Token", "socialUser");

    private SystemVoSchemaRegistry() {
    }

    public static Optional<VoBinding> find(String resourceCode) {
        if (resourceCode == null || resourceCode.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(BY_RESOURCE.get(resourceCode));
    }

    public static boolean supportsWrite(String resourceCode) {
        return resourceCode != null && !READ_ONLY_RESOURCES.contains(resourceCode);
    }

    private static Map<String, VoBinding> build() {
        Map<String, VoBinding> map = new LinkedHashMap<>();
        map.put("dept", vo("cn.iocoder.yudao.module.system.controller.admin.dept.vo.dept.DeptSaveReqVO"));
        map.put("user", vo("cn.iocoder.yudao.module.system.controller.admin.user.vo.user.UserSaveReqVO", "password"));
        map.put("post", vo("cn.iocoder.yudao.module.system.controller.admin.dept.vo.post.PostSaveReqVO"));
        map.put("role", vo("cn.iocoder.yudao.module.system.controller.admin.permission.vo.role.RoleSaveReqVO"));
        map.put("menu", vo("cn.iocoder.yudao.module.system.controller.admin.permission.vo.menu.MenuSaveVO"));
        map.put("dictType", vo("cn.iocoder.yudao.module.system.controller.admin.dict.vo.type.DictTypeSaveReqVO"));
        map.put("dictData", vo("cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataSaveReqVO"));
        map.put("notice", vo("cn.iocoder.yudao.module.system.controller.admin.notice.vo.NoticeSaveReqVO"));
        map.put("tenant", vo("cn.iocoder.yudao.module.system.controller.admin.tenant.vo.tenant.TenantSaveReqVO"));
        map.put("tenantPackage", vo("cn.iocoder.yudao.module.system.controller.admin.tenant.vo.packages.TenantPackageSaveReqVO"));
        map.put("notifyTemplate", vo("cn.iocoder.yudao.module.system.controller.admin.notify.vo.template.NotifyTemplateSaveReqVO"));
        map.put("mailAccount", vo("cn.iocoder.yudao.module.system.controller.admin.mail.vo.account.MailAccountSaveReqVO"));
        map.put("mailTemplate", vo("cn.iocoder.yudao.module.system.controller.admin.mail.vo.template.MailTemplateSaveReqVO"));
        map.put("smsChannel", vo("cn.iocoder.yudao.module.system.controller.admin.sms.vo.channel.SmsChannelSaveReqVO"));
        map.put("smsTemplate", vo("cn.iocoder.yudao.module.system.controller.admin.sms.vo.template.SmsTemplateSaveReqVO"));
        map.put("oauth2Client", vo("cn.iocoder.yudao.module.system.controller.admin.oauth2.vo.client.OAuth2ClientSaveReqVO"));
        map.put("socialClient", vo("cn.iocoder.yudao.module.system.controller.admin.socail.vo.client.SocialClientSaveReqVO"));
        return Collections.unmodifiableMap(map);
    }

    private static VoBinding vo(String className, String... createOnly) {
        return new VoBinding(className, Set.of(createOnly));
    }
}
