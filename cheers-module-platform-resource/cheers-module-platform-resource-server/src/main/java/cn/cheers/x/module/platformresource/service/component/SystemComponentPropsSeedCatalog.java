package cn.cheers.x.module.platformresource.service.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * System 模块 Props 种子目录（与 dynamicbusiness {@code SystemCapabilityCatalog} 对齐）。
 * <p>
 * - 每个资源至少 1 个 list 模板（propsId 3001–3025）
 * - {@code supportsTree} 资源另增 tree 模板（3101 部门；菜单树种子已省略）
 * - 演示模板 1001/1002/2001 见 Flyway V4
 */
public final class SystemComponentPropsSeedCatalog {

    /** 与 SystemCapabilityCatalog / 前端 SYSTEM_CAPABILITY_RESOURCE_CODES 一致 */
    private static final List<String> RESOURCE_CODES = List.of(
            "dept", "user", "post", "role", "menu", "dictType", "dictData", "notice", "tenant", "tenantPackage",
            "loginLog", "operateLog", "notifyTemplate", "notifyMessage", "notifyMessageMy", "mailAccount",
            "mailTemplate", "mailLog", "smsChannel", "smsTemplate", "smsLog", "oauth2Client", "oauth2Token",
            "socialClient", "socialUser");

    private static final Set<String> TREE_RESOURCE_CODES = Set.of("dept");

    private static final Map<String, Long> LIST_PROPS_ID = Map.ofEntries(
            Map.entry("dept", 3001L), Map.entry("user", 3002L), Map.entry("post", 3003L), Map.entry("role", 3004L),
            Map.entry("menu", 3005L), Map.entry("dictType", 3006L), Map.entry("dictData", 3007L),
            Map.entry("notice", 3008L), Map.entry("tenant", 3009L), Map.entry("tenantPackage", 3010L),
            Map.entry("loginLog", 3011L), Map.entry("operateLog", 3012L), Map.entry("notifyTemplate", 3013L),
            Map.entry("notifyMessage", 3014L), Map.entry("notifyMessageMy", 3015L), Map.entry("mailAccount", 3016L),
            Map.entry("mailTemplate", 3017L), Map.entry("mailLog", 3018L), Map.entry("smsChannel", 3019L),
            Map.entry("smsTemplate", 3020L), Map.entry("smsLog", 3021L), Map.entry("oauth2Client", 3022L),
            Map.entry("oauth2Token", 3023L), Map.entry("socialClient", 3024L), Map.entry("socialUser", 3025L));

    private static final Map<String, Long> TREE_PROPS_ID = Map.of("dept", 3101L);

    private static final Map<String, String> DISPLAY_NAMES = Map.ofEntries(
            Map.entry("dept", "部门"), Map.entry("user", "用户"), Map.entry("post", "岗位"), Map.entry("role", "角色"),
            Map.entry("menu", "菜单"), Map.entry("dictType", "字典类型"), Map.entry("dictData", "字典数据"),
            Map.entry("notice", "通知公告"), Map.entry("tenant", "租户"), Map.entry("tenantPackage", "租户套餐"),
            Map.entry("loginLog", "登录日志"), Map.entry("operateLog", "操作日志"),
            Map.entry("notifyTemplate", "站内信模板"), Map.entry("notifyMessage", "站内信消息"),
            Map.entry("notifyMessageMy", "我的站内信"), Map.entry("mailAccount", "邮箱账号"),
            Map.entry("mailTemplate", "邮件模板"), Map.entry("mailLog", "邮件日志"),
            Map.entry("smsChannel", "短信渠道"), Map.entry("smsTemplate", "短信模板"), Map.entry("smsLog", "短信日志"),
            Map.entry("oauth2Client", "OAuth2 客户端"), Map.entry("oauth2Token", "OAuth2 令牌"),
            Map.entry("socialClient", "社交客户端"), Map.entry("socialUser", "社交用户"));

    public record SeedEntry(
            Long propsId,
            String resourceCode,
            String componentCode,
            String schemaVersion,
            String nameSuffix,
            int sort) {
        public String dataSourceKey() {
            return "system:" + resourceCode;
        }
    }

    private static final List<SeedEntry> ENTRIES = buildEntries();

    private SystemComponentPropsSeedCatalog() {
    }

    public static List<SeedEntry> all() {
        return ENTRIES;
    }

    public static List<SeedEntry> listEntries() {
        return ENTRIES.stream().filter(e -> "list".equals(e.componentCode())).toList();
    }

    public static List<SeedEntry> treeEntries() {
        return ENTRIES.stream().filter(e -> "tree".equals(e.componentCode())).toList();
    }

    private static List<SeedEntry> buildEntries() {
        List<SeedEntry> list = new ArrayList<>();
        int sort = 10;
        for (String code : RESOURCE_CODES) {
            list.add(new SeedEntry(
                    LIST_PROPS_ID.get(code),
                    code,
                    "list",
                    "list@1",
                    DISPLAY_NAMES.getOrDefault(code, code) + "列表",
                    sort));
            sort += 10;
        }
        for (String code : TREE_RESOURCE_CODES) {
            list.add(new SeedEntry(
                    TREE_PROPS_ID.get(code),
                    code,
                    "tree",
                    "tree@1",
                    DISPLAY_NAMES.getOrDefault(code, code) + "树",
                    sort));
            sort += 10;
        }
        return List.copyOf(list);
    }
}
