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
            if (StringUtils.hasText(entityType.getDedicatedTableName())) {
                return TenantPhysicalTableNames.ensureTenantSuffix(entityType.getDedicatedTableName().trim());
            }
            String baseCode = StringUtils.hasText(entityType.getBaseEntityTypeCode())
                    ? entityType.getBaseEntityTypeCode().trim()
                    : entityTypeCode;
            return TenantPhysicalTableNames.entityPhysicalTable(baseCode);
        }

        // 创建过程中元数据可能尚未可见：按约定路由到当前租户物理表
        return TenantPhysicalTableNames.entityPhysicalTable(entityTypeCode);
    }

    private boolean isDedicatedStorage(EntityTypeDO entityType) {
        StorageTypeEnum storageType = StorageTypeEnum.getByCode(entityType.getStorageType());
        return storageType != null && storageType.isDedicated();
    }
}
