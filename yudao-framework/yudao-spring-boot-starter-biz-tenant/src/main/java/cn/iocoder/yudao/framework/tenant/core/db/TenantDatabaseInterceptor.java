package cn.iocoder.yudao.framework.tenant.core.db;

import cn.iocoder.yudao.framework.tenant.config.TenantProperties;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.toolkit.SqlParserUtils;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于 MyBatis Plus 多租户的功能，实现 DB 层面的多租户的功能
 *
 * 
 */
public class TenantDatabaseInterceptor implements TenantLineHandler {

    /**
     * 忽略的表
     *
     * KEY：表名
     * VALUE：是否忽略
     */
    private final Map<String, Boolean> ignoreTables = new HashMap<>();

    public TenantDatabaseInterceptor(TenantProperties properties) {
        // 不同 DB 下，大小写的习惯不同，所以需要都添加进去
        properties.getIgnoreTables().forEach(table -> {
            addIgnoreTable(table, true);
        });
        // 在 OracleKeyGenerator 中，生成主键时，会查询这个表，查询这个表后，会自动拼接 TENANT_ID 导致报错
        addIgnoreTable("DUAL", true);
    }

    @Override
    public Expression getTenantId() {
        return new LongValue(TenantContextHolder.getRequiredTenantId());
    }

    @Override
    public boolean ignoreTable(String tableName) {
        // 情况一，全局忽略多租户
        if (TenantContextHolder.isIgnore()) {
            return true;
        }
        // 情况二，忽略多租户的表
        tableName = SqlParserUtils.removeWrapperSymbol(tableName);
        Boolean ignore = ignoreTables.get(tableName.toLowerCase());
        if (ignore == null) {
            ignore = computeIgnoreTable(tableName);
            synchronized (ignoreTables) {
                addIgnoreTable(tableName, ignore);
            }
        }
        return ignore;
    }

    private void addIgnoreTable(String tableName, boolean ignore) {
        ignoreTables.put(tableName.toLowerCase(), ignore);
        ignoreTables.put(tableName.toUpperCase(), ignore);
    }

    private boolean computeIgnoreTable(String tableName) {
        // 找不到的表，说明不是 yudao 项目里的，不进行拦截（忽略租户）
        TableInfo tableInfo = TableInfoHelper.getTableInfo(tableName);
        if (tableInfo == null) {
            return true;
        }
        // @TenantIgnore 优先：全局参考数据可继承 TenantBaseDO 但仍忽略租户行过滤
        // （否则注解永远达不到，tenant_id=0 的种子在租户请求下会查不到）
        TenantIgnore tenantIgnore = tableInfo.getEntityType().getAnnotation(TenantIgnore.class);
        if (tenantIgnore != null) {
            return true;
        }
        // 继承 TenantBaseDO 且未标忽略：按当前租户过滤
        if (TenantBaseDO.class.isAssignableFrom(tableInfo.getEntityType())) {
            return false;
        }
        // 仅 BaseDO / 无租户列的表：不注入 tenant_id（否则 asset_resource 等会 500）
        return true;
    }

}
