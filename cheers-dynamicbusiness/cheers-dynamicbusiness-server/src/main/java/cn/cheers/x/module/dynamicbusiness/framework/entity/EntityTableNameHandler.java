package cn.cheers.x.module.dynamicbusiness.framework.entity;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.StorageTypeEnum;
import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 动态表名：实体占位符 → 租户专用 {@code ent_*_t{id}}；关联基表 → {@code *_t{id}}。
 */
@Component
@RequiredArgsConstructor
public class EntityTableNameHandler implements TableNameHandler {

    private static final Logger log = LoggerFactory.getLogger(EntityTableNameHandler.class);

    public static final String ENTITY_TABLE_PLACEHOLDER = "__entity_dynamic__";

    private static final Pattern VALID_CODE_PATTERN = Pattern.compile("^[a-zA-Z]\\w{0,49}$");

    private final EntityTypeMapper entityTypeMapper;

    @Override
    public String dynamicTableName(String sql, String tableName) {
        if (tableName == null || tableName.isBlank()) {
            return tableName;
        }
        if (ENTITY_TABLE_PLACEHOLDER.equals(tableName)) {
            String entityTypeCode = EntityTableNameContext.get();
            if (!StringUtils.hasText(entityTypeCode)) {
                throw new IllegalStateException(
                        "实体表访问缺少 EntityTableNameContext（必须经 EntityRepository 并传入 entityTypeCode），"
                                + "禁止回落物理表");
            }
            String resolved = validateAndResolveTableName(entityTypeCode.trim());
            if (log.isDebugEnabled()) {
                log.debug("[dynamicTableName] placeholder={} entityTypeCode={} -> {}",
                        tableName, entityTypeCode, resolved);
            }
            return resolved;
        }
        if (TenantPhysicalTableNames.isIsolatedAssociationBase(tableName)) {
            String physical = TenantPhysicalTableNames.requirePhysical(tableName);
            if (log.isDebugEnabled()) {
                log.debug("[dynamicTableName] association {} -> {}", tableName, physical);
            }
            return physical;
        }
        return tableName;
    }

    /**
     * 解析 entityTypeCode 对应的物理表名（供原生 SQL / JDBC 使用）。
     */
    public String resolvePhysicalTableName(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new IllegalArgumentException("entityTypeCode 不能为空");
        }
        return validateAndResolveTableName(entityTypeCode.trim());
    }

    public boolean isDynamicTable(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            return false;
        }
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode.trim());
        if (entityType == null) {
            return false;
        }
        return isDedicatedStorage(entityType);
    }

    /**
     * 记录存在同一张物理表里的全部数据目录编码（含这个编码自己）。
     *
     * <p>不同表的记录编号会重复，关联表里只有「记录编号 + 目录编码」能定位到一条记录。
     * 删一条记录的关联时，按这组编码限定，不会删到别的表里同编号记录的关联。
     * 同表判断只认找表规则（专用表名优先，其次底座类型），不另猜：例如订阅目录自己是原生目录，
     * 但它的表登记的是推送订阅表。</p>
     */
    public List<String> listCodesSharingPhysicalTable(String entityTypeCode) {
        String table = resolvePhysicalTableName(entityTypeCode);
        Set<String> codes = new LinkedHashSet<>();
        codes.add(entityTypeCode.trim());
        for (EntityTypeDO type : entityTypeMapper.selectList()) {
            if (StringUtils.hasText(type.getCode()) && isDedicatedStorage(type)
                    && table.equals(tableOf(type, type.getCode().trim()))) {
                codes.add(type.getCode().trim());
            }
        }
        return new ArrayList<>(codes);
    }

    private String validateAndResolveTableName(String entityTypeCode) {
        if (!VALID_CODE_PATTERN.matcher(entityTypeCode).matches()) {
            throw new IllegalArgumentException("非法的 entityTypeCode: " + entityTypeCode);
        }

        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode);
        if (entityType != null) {
            if (!isDedicatedStorage(entityType)) {
                throw new IllegalStateException(
                        "实体类型未使用专用表存储，已禁止 GENERIC/dynamic_entity：code=" + entityTypeCode
                                + ", storageType=" + entityType.getStorageType());
            }
            return tableOf(entityType, entityTypeCode);
        }

        // 创建过程中元数据可能尚未可见：按约定路由到当前租户物理表
        return TenantPhysicalTableNames.entityPhysicalTable(entityTypeCode);
    }

    private String tableOf(EntityTypeDO entityType, String entityTypeCode) {
        if (StringUtils.hasText(entityType.getDedicatedTableName())) {
            return TenantPhysicalTableNames.ensureTenantSuffix(entityType.getDedicatedTableName().trim());
        }
        String baseCode = StringUtils.hasText(entityType.getBaseEntityTypeCode())
                ? entityType.getBaseEntityTypeCode().trim()
                : entityTypeCode;
        return TenantPhysicalTableNames.entityPhysicalTable(baseCode);
    }

    private boolean isDedicatedStorage(EntityTypeDO entityType) {
        StorageTypeEnum storageType = StorageTypeEnum.getByCode(entityType.getStorageType());
        return storageType != null && storageType.isDedicated();
    }
}
