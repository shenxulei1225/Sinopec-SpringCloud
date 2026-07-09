package cn.cheers.x.module.dynamicbusiness.service.capability.system;

import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 系统业务能力注册表（启动时写入 business_capability + projection）。
 *
 * <p>第一版覆盖组件库配置器常用模块；后续可扩展或改为注解扫描。</p>
 */
public final class SystemCapabilityCatalog {

    private static final Map<String, SystemCapabilityDefinition> BY_CODE = new LinkedHashMap<>();

    static {
        register(dept("dept", "部门", "/system/dept/list", false));
        register(page("user", "用户", "/system/user/page",
                fields("id", "ID", false, "username", "用户名", true, "nickname", "昵称", false, "mobile", "手机号", true),
                fields("username", "用户名", true, "mobile", "手机号", true, "status", "状态", false)));
        register(page("role", "角色", "/system/role/page",
                fields("id", "ID", false, "name", "名称", true, "code", "编码", true),
                fields("name", "名称", true, "status", "状态", false)));
        register(page("post", "岗位", "/system/post/page",
                fields("id", "ID", false, "name", "名称", true, "code", "编码", true),
                fields("name", "名称", true, "status", "状态", false)));
        register(dept("menu", "菜单", "/system/menu/list", false));
        register(page("dictType", "字典类型", "/system/dict-type/page",
                fields("id", "ID", false, "name", "名称", true, "type", "类型", true),
                fields("name", "名称", true, "status", "状态", false)));
        register(page("dictData", "字典数据", "/system/dict-data/page",
                fields("id", "ID", false, "label", "标签", true, "value", "值", true),
                fields("label", "标签", true, "status", "状态", false)));
        register(page("notice", "通知公告", "/system/notice/page",
                fields("id", "ID", false, "title", "标题", true),
                fields("title", "标题", true, "status", "状态", false)));
        register(page("tenant", "租户", "/system/tenant/page",
                fields("id", "ID", false, "name", "名称", true),
                fields("name", "名称", true, "status", "状态", false)));
        register(page("tenantPackage", "租户套餐", "/system/tenant-package/page",
                fields("id", "ID", false, "name", "名称", true),
                fields("name", "名称", true, "status", "状态", false)));

        // 日志
        registerGenericPage("loginLog", "登录日志", "/system/login-log/page",
                "userIp", "username", "status");
        registerGenericPage("operateLog", "操作日志", "/system/operate-log/page",
                "type", "action", "createTime");

        // 站内信
        registerGenericPage("notifyTemplate", "通知模板", "/system/notify-template/page",
                "code", "name", "status");
        registerGenericPage("notifyMessage", "站内信", "/system/notify-message/page",
                "templateCode", "templateType", "createTime");
        registerGenericPage("notifyMessageMy", "我的站内信", "/system/notify-message/my-page",
                "readStatus", "createTime");

        // 邮件
        registerGenericPage("mailAccount", "邮件账号", "/system/mail-account/page", "mail", "username");
        registerGenericPage("mailTemplate", "邮件模板", "/system/mail-template/page",
                "code", "name", "status");
        registerGenericPage("mailLog", "邮件日志", "/system/mail-log/page", "toMail", "sendStatus");

        // 短信
        registerGenericPage("smsChannel", "短信渠道", "/system/sms-channel/page", "signature", "status");
        registerGenericPage("smsTemplate", "短信模板", "/system/sms-template/page", "code", "status");
        registerGenericPage("smsLog", "短信日志", "/system/sms-log/page", "mobile", "sendStatus");

        // OAuth2
        registerGenericPage("oauth2Client", "OAuth2 客户端", "/system/oauth2-client/page", "name", "status");
        registerGenericPage("oauth2Token", "OAuth2 令牌", "/system/oauth2-token/page", "userId", "clientId");

        // 社交登录
        registerGenericPage("socialClient", "社交客户端", "/system/social-client/page", "name", "socialType");
        registerGenericPage("socialUser", "社交用户", "/system/social-user/page", "nickname", "openid");
    }

    private SystemCapabilityCatalog() {
    }

    public static List<SystemCapabilityDefinition> all() {
        return List.copyOf(BY_CODE.values());
    }

    public static Optional<SystemCapabilityDefinition> find(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            return Optional.empty();
        }
        return Optional.ofNullable(BY_CODE.get(entityTypeCode.trim()));
    }

    public static boolean isSystemCapability(String entityTypeCode) {
        return find(entityTypeCode).isPresent();
    }

    private static void register(SystemCapabilityDefinition definition) {
        BY_CODE.put(definition.getEntityTypeCode(), definition);
    }

    private static SystemCapabilityDefinition dept(
            String code, String name, String url, boolean paginated) {
        return SystemCapabilityDefinition.builder()
                .entityTypeCode(code)
                .entityTypeName(name)
                .readUrl(url)
                .readMethod("GET")
                .paginated(paginated)
                .displayFields(fields("id", "ID", false, "name", "名称", true))
                .filterFields(fields("name", "名称", true, "status", "状态", false))
                .build();
    }

    private static SystemCapabilityDefinition page(
            String code, String name, String url,
            List<SystemCapabilityDefinition.SystemFieldDefinition> displayFields,
            List<SystemCapabilityDefinition.SystemFieldDefinition> filterFields) {
        return SystemCapabilityDefinition.builder()
                .entityTypeCode(code)
                .entityTypeName(name)
                .readUrl(url)
                .readMethod("GET")
                .paginated(true)
                .displayFields(displayFields)
                .filterFields(filterFields)
                .build();
    }

    /** 按字段 key 快速注册分页系统能力（展示 id + 前若干 key；筛选用 status 或可搜索 key）。 */
    private static void registerGenericPage(String code, String name, String url, String... keys) {
        List<Object> displayTriples = new java.util.ArrayList<>();
        displayTriples.add("id");
        displayTriples.add("ID");
        displayTriples.add(false);
        int displayCount = 0;
        for (String key : keys) {
            if (displayCount >= 4) {
                break;
            }
            displayTriples.add(key);
            displayTriples.add(defaultFieldLabel(key));
            displayTriples.add(isSearchableFieldKey(key));
            displayCount++;
        }

        List<Object> filterTriples = new java.util.ArrayList<>();
        for (String key : keys) {
            if ("status".equals(key) || isSearchableFieldKey(key)) {
                filterTriples.add(key);
                filterTriples.add(defaultFieldLabel(key));
                filterTriples.add(isSearchableFieldKey(key));
            }
        }
        if (filterTriples.isEmpty() && keys.length > 0) {
            filterTriples.add(keys[0]);
            filterTriples.add(defaultFieldLabel(keys[0]));
            filterTriples.add(true);
        }

        register(page(
                code,
                name,
                url,
                fields(displayTriples.toArray()),
                fields(filterTriples.toArray())));
    }

    private static String defaultFieldLabel(String fieldKey) {
        return switch (fieldKey) {
            case "userIp" -> "IP";
            case "username" -> "用户名";
            case "nickname" -> "昵称";
            case "mobile" -> "手机号";
            case "readStatus" -> "已读状态";
            case "toMail" -> "收件邮箱";
            case "sendStatus" -> "发送状态";
            case "templateCode" -> "模板编码";
            case "templateType" -> "模板类型";
            case "socialType" -> "社交类型";
            case "openid" -> "OpenID";
            case "clientId" -> "客户端编号";
            case "createTime" -> "创建时间";
            case "status" -> "状态";
            case "name" -> "名称";
            case "code" -> "编码";
            case "title" -> "标题";
            case "type" -> "类型";
            case "action" -> "操作";
            default -> fieldKey;
        };
    }

    private static boolean isSearchableFieldKey(String fieldKey) {
        return !"status".equals(fieldKey)
                && !"createTime".equals(fieldKey)
                && !"readStatus".equals(fieldKey)
                && !"sendStatus".equals(fieldKey);
    }

    private static List<SystemCapabilityDefinition.SystemFieldDefinition> fields(Object... triples) {
        if (triples.length % 3 != 0) {
            throw new IllegalArgumentException("fields 须为 fieldKey, label, searchable 三元组");
        }
        List<SystemCapabilityDefinition.SystemFieldDefinition> list = new java.util.ArrayList<>();
        for (int i = 0; i < triples.length; i += 3) {
            list.add(SystemCapabilityDefinition.SystemFieldDefinition.builder()
                    .fieldKey(String.valueOf(triples[i]))
                    .label(String.valueOf(triples[i + 1]))
                    .searchable(Boolean.parseBoolean(String.valueOf(triples[i + 2])))
                    .build());
        }
        return Collections.unmodifiableList(list);
    }
}
