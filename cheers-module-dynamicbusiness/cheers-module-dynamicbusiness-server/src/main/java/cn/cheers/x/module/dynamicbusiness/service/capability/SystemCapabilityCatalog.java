package cn.cheers.x.module.dynamicbusiness.service.capability;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * System 模块能力目录（与 ecs-react systemListAdapters 路径、筛选字段对齐）。
 */
public final class SystemCapabilityCatalog {

    private static final List<SystemCapabilityResourceDef> ALL = List.of(
            resource("dept", "部门", "/system/dept/list", false, true,
                    filters(input("name", "部门名称", true), dict("status", "状态")),
                    displays(text("name", "部门名称", 0), text("id", "编号", 1),
                            dict("status", "状态", 2), text("sort", "排序", 3))),
            resource("user", "用户", "/system/user/page", true, false,
                    filters(input("username", "用户账号", true), input("mobile", "手机号", true),
                            dict("status", "状态"), ref("deptId", "部门", "system:dept"),
                            ref("roleId", "角色", "system:role")),
                    displays(text("username", "用户账号", 0), text("nickname", "昵称", 1),
                            text("mobile", "手机号", 2), dict("status", "状态", 3))),
            resource("post", "岗位", "/system/post/page", true, false,
                    filters(input("code", "岗位编码", true), input("name", "岗位名称", true), dict("status", "状态")),
                    displays(text("name", "岗位名称", 0), text("code", "编码", 1), dict("status", "状态", 2))),
            resource("role", "角色", "/system/role/page", true, false,
                    filters(input("name", "角色名称", true), input("code", "角色编码", true), dict("status", "状态")),
                    displays(text("name", "角色名称", 0), text("code", "编码", 1), dict("status", "状态", 2))),
            resource("menu", "菜单", "/system/menu/list", false, true,
                    filters(input("name", "菜单名称", true), dict("status", "状态")),
                    displays(text("name", "菜单名称", 0), text("id", "编号", 1), dict("status", "状态", 2))),
            resource("dictType", "字典类型", "/system/dict-type/page", true, false,
                    filters(input("name", "字典名称", true), input("type", "字典类型", true), dict("status", "状态")),
                    displays(text("name", "字典名称", 0), text("type", "类型", 1), dict("status", "状态", 2))),
            resource("dictData", "字典数据", "/system/dict-data/page", true, false,
                    filters(input("label", "字典标签", true), input("dictType", "字典类型", true), dict("status", "状态")),
                    displays(text("label", "字典标签", 0), text("value", "字典键值", 1), dict("status", "状态", 2))),
            resource("notice", "通知公告", "/system/notice/page", true, false,
                    filters(input("title", "公告标题", true), dict("status", "状态")),
                    displays(text("title", "公告标题", 0), dict("status", "状态", 1))),
            resource("tenant", "租户", "/system/tenant/page", true, false,
                    filters(input("name", "租户名", true), input("contactName", "联系人", true),
                            input("contactMobile", "联系手机", true), dict("status", "状态")),
                    displays(text("name", "租户名", 0), text("contactName", "联系人", 1), dict("status", "状态", 2))),
            resource("tenantPackage", "租户套餐", "/system/tenant-package/page", true, false,
                    filters(input("name", "套餐名", true), dict("status", "状态")),
                    displays(text("name", "套餐名", 0), dict("status", "状态", 1))),
            resource("loginLog", "登录日志", "/system/login-log/page", true, false,
                    filters(input("userIp", "登录 IP", true), input("username", "用户账号", true), dict("status", "状态")),
                    displays(text("username", "用户账号", 0), text("userIp", "登录 IP", 1), dict("status", "状态", 2))),
            resource("operateLog", "操作日志", "/system/operate-log/page", true, false,
                    filters(input("type", "操作模块", true), input("subType", "操作名", true)),
                    displays(text("type", "操作模块", 0), text("subType", "操作名", 1), text("action", "操作内容", 2))),
            resource("notifyTemplate", "站内信模板", "/system/notify-template/page", true, false,
                    filters(input("code", "模板编码", true), input("name", "模板名称", true), dict("status", "状态")),
                    displays(text("name", "模板名称", 0), text("code", "编码", 1), dict("status", "状态", 2))),
            resource("notifyMessage", "站内信消息", "/system/notify-message/page", true, false,
                    filters(input("templateCode", "模板编码", true)),
                    displays(text("templateCode", "模板编码", 0), text("id", "编号", 1))),
            resource("notifyMessageMy", "我的站内信", "/system/notify-message/my-page", true, false,
                    filters(dict("readStatus", "是否已读")),
                    displays(text("templateCode", "模板编码", 0), dict("readStatus", "是否已读", 1))),
            resource("mailAccount", "邮箱账号", "/system/mail-account/page", true, false,
                    filters(input("mail", "邮箱", true), input("username", "用户名", true)),
                    displays(text("mail", "邮箱", 0), text("username", "用户名", 1))),
            resource("mailTemplate", "邮件模板", "/system/mail-template/page", true, false,
                    filters(input("code", "模板编码", true), input("name", "模板名称", true), dict("status", "状态")),
                    displays(text("name", "模板名称", 0), text("code", "编码", 1), dict("status", "状态", 2))),
            resource("mailLog", "邮件日志", "/system/mail-log/page", true, false,
                    filters(input("toMail", "收件邮箱", true), dict("sendStatus", "发送状态")),
                    displays(text("toMail", "收件邮箱", 0), dict("sendStatus", "发送状态", 1))),
            resource("smsChannel", "短信渠道", "/system/sms-channel/page", true, false,
                    filters(input("signature", "短信签名", true), dict("status", "状态")),
                    displays(text("signature", "短信签名", 0), dict("status", "状态", 1))),
            resource("smsTemplate", "短信模板", "/system/sms-template/page", true, false,
                    filters(input("code", "模板编码", true), dict("status", "状态")),
                    displays(text("code", "模板编码", 0), dict("status", "状态", 1))),
            resource("smsLog", "短信日志", "/system/sms-log/page", true, false,
                    filters(input("mobile", "手机号", true), dict("sendStatus", "发送状态")),
                    displays(text("mobile", "手机号", 0), dict("sendStatus", "发送状态", 1))),
            resource("oauth2Client", "OAuth2 客户端", "/system/oauth2-client/page", true, false,
                    filters(input("name", "应用名", true), dict("status", "状态")),
                    displays(text("name", "应用名", 0), dict("status", "状态", 1))),
            resource("oauth2Token", "OAuth2 令牌", "/system/oauth2-token/page", true, false,
                    filters(input("clientId", "客户端编号", true)),
                    displays(text("clientId", "客户端", 0), text("id", "编号", 1))),
            resource("socialClient", "社交客户端", "/system/social-client/page", true, false,
                    filters(input("name", "应用名", true), dict("status", "状态")),
                    displays(text("name", "应用名", 0), dict("status", "状态", 1))),
            resource("socialUser", "社交用户", "/system/social-user/page", true, false,
                    filters(input("nickname", "昵称", true), input("openid", "OpenID", true)),
                    displays(text("nickname", "昵称", 0), text("openid", "OpenID", 1))));

    private static final Map<String, SystemCapabilityResourceDef> BY_CODE = ALL.stream()
            .collect(Collectors.toMap(SystemCapabilityResourceDef::resourceCode, Function.identity()));

    private SystemCapabilityCatalog() {
    }

    public static List<SystemCapabilityResourceDef> all() {
        return ALL;
    }

    public static Optional<SystemCapabilityResourceDef> find(String resourceCode) {
        if (resourceCode == null || resourceCode.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(BY_CODE.get(resourceCode));
    }

    private static SystemCapabilityResourceDef resource(
            String code, String label, String url, boolean paginated, boolean supportsTree,
            List<SystemCapabilityResourceDef.FilterSpec> filters,
            List<SystemCapabilityResourceDef.DisplaySpec> displays) {
        return new SystemCapabilityResourceDef(code, label, url, paginated, filters, displays, supportsTree);
    }

    private static List<SystemCapabilityResourceDef.FilterSpec> filters(
            SystemCapabilityResourceDef.FilterSpec... items) {
        return List.of(items);
    }

    private static List<SystemCapabilityResourceDef.DisplaySpec> displays(
            SystemCapabilityResourceDef.DisplaySpec... items) {
        return List.of(items);
    }

    private static SystemCapabilityResourceDef.FilterSpec input(String fieldKey, String label, boolean searchable) {
        return new SystemCapabilityResourceDef.FilterSpec(fieldKey, label, "input", searchable, null, null);
    }

    private static SystemCapabilityResourceDef.FilterSpec dict(String fieldKey, String label) {
        return new SystemCapabilityResourceDef.FilterSpec(fieldKey, label, "dict", false, "common_status", null);
    }

    private static SystemCapabilityResourceDef.FilterSpec ref(String fieldKey, String label, String targetKey) {
        return new SystemCapabilityResourceDef.FilterSpec(fieldKey, label, "ref-picker", false, null, targetKey);
    }

    private static SystemCapabilityResourceDef.DisplaySpec text(String fieldKey, String label, int order) {
        return new SystemCapabilityResourceDef.DisplaySpec(fieldKey, label, "text", order, order <= 3);
    }

    private static SystemCapabilityResourceDef.DisplaySpec dict(String fieldKey, String label, int order) {
        return new SystemCapabilityResourceDef.DisplaySpec(fieldKey, label, "dict", order, order <= 3);
    }
}
