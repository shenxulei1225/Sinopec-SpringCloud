package cn.cheers.x.module.dynamicbusiness.framework.tenant;

import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.framework.common.exception.ServiceException;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 租户物理表名：基表名 + {@code _t{tenantId}}。
 *
 * <p>见 {@code docs/动态业务/多租户物理隔离定稿.md}。</p>
 */
public final class TenantPhysicalTableNames {

    private static final Pattern SAFE_BASE = Pattern.compile("^[a-z][a-z0-9_]*$");

    /**
     * 必须按租户分表的基表名（无后缀）。含全部实体专用表前缀约定外的关联/索引/权限表。
     */
    public static final Set<String> ISOLATED_ASSOCIATION_BASE_TABLES = Set.of(
            "dynamic_entity_relation",
            "dynamic_entity_category_relation",
            "dynamic_category_entity_link",
            "dynamic_entity_field_index",
            "dynamic_model_category_relation",
            "dynamic_model_entity_relation",
            "dynamic_category_category_relation",
            "dynamic_entity_access_permission",
            "dynamic_entity_field_permission",
            "dynamic_entity_operation_permission",
            "dynamic_precomputed_value",
            "dynamic_category_user_relation",
            "dynamic_category_permission"
    );

    private TenantPhysicalTableNames() {
    }

    /**
     * 当前租户上下文下的物理表名。缺租户或非法基名直接失败（禁止静默共表）。
     */
    public static String requirePhysical(String baseTableName) {
        return requirePhysical(baseTableName, requireTenantId());
    }

    public static String requirePhysical(String baseTableName, Long tenantId) {
        String base = normalizeBase(baseTableName);
        if (tenantId == null || tenantId < 0) {
            throw new ServiceException(500, "物理隔离表需要有效 tenantId，base=" + base);
        }
        return base + "_t" + tenantId;
    }

    /**
     * 实体专用表基名 {@code ent_{code}}（小写），不含租户后缀。
     */
    public static String entityBaseTable(String entityTypeCode) {
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        String code = entityTypeCode.trim().toLowerCase(Locale.ROOT);
        if (!SAFE_BASE.matcher("ent_" + code.replace('-', '_')).matches() && !code.matches("^[a-z][a-z0-9_]*$")) {
            throw new ServiceException(400, "非法 entityTypeCode: " + entityTypeCode);
        }
        String safe = code.replace('-', '_');
        return "ent_" + safe;
    }

    /**
     * 当前租户的实体专用表全名，如 {@code ent_zone_t1}。
     */
    public static String entityPhysicalTable(String entityTypeCode) {
        return requirePhysical(entityBaseTable(entityTypeCode));
    }

    public static String entityPhysicalTable(String entityTypeCode, Long tenantId) {
        return requirePhysical(entityBaseTable(entityTypeCode), tenantId);
    }

    public static boolean isIsolatedAssociationBase(String tableName) {
        if (tableName == null || tableName.isBlank()) {
            return false;
        }
        return ISOLATED_ASSOCIATION_BASE_TABLES.contains(normalizeBase(tableName));
    }

    /**
     * 若已是 {@code *_t数字} 则原样返回；否则按当前租户加后缀（用于兼容已写入 dedicated_table_name 的值）。
     */
    public static String ensureTenantSuffix(String tableName) {
        if (tableName == null || tableName.isBlank()) {
            throw new ServiceException(400, "表名不能为空");
        }
        String name = tableName.trim().toLowerCase(Locale.ROOT);
        if (name.matches("^.+_t\\d+$")) {
            return name;
        }
        return requirePhysical(name);
    }

    public static Long requireTenantId() {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) {
            throw new ServiceException(500, "当前上下文缺少 tenantId，无法解析物理隔离表");
        }
        return tenantId;
    }

    private static String normalizeBase(String tableName) {
        if (tableName == null || tableName.isBlank()) {
            throw new ServiceException(400, "表名不能为空");
        }
        String name = tableName.trim().toLowerCase(Locale.ROOT);
        // 已带后缀时剥掉再规范化调用方应避免；此处仅校验基名形态
        if (name.matches("^.+_t\\d+$")) {
            int idx = name.lastIndexOf("_t");
            name = name.substring(0, idx);
        }
        if (!SAFE_BASE.matcher(name).matches()) {
            throw new ServiceException(400, "非法表名: " + tableName);
        }
        return name;
    }
}
