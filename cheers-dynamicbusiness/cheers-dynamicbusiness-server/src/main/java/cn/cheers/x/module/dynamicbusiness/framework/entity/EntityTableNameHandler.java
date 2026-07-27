package cn.cheers.x.module.dynamicbusiness.framework.entity;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.StorageTypeEnum;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 实体动态表名处理器。
 *
 * <p>{@link cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO} 注解上的表名是
 * <b>非物理占位符</b>{@link #ENTITY_TABLE_PLACEHOLDER}，仅供本 Handler 识别并改写为 {@code ent_*}。
 * 该占位符不是业务表，也不是旧 GENERIC 的 {@code dynamic_entity}。</p>
 *
 * <p>必须先由 {@link EntityTableNameContext} 注入存储类型编码；缺上下文或非专用表存储时直接失败，
 * 禁止静默回落到任何默认物理表。</p>
 */
@Component
@RequiredArgsConstructor
public class EntityTableNameHandler implements TableNameHandler {

    private static final Logger log = LoggerFactory.getLogger(EntityTableNameHandler.class);

    /**
     * EntityDO / EntityMapper SQL 中的占位表名（非物理表）。
     * 刻意不用 dynamic_entity，避免与已废止的 GENERIC 真表同名。
     */
    public static final String ENTITY_TABLE_PLACEHOLDER = "__entity_dynamic__";

    private static final String DYNAMIC_TABLE_PREFIX = "ent_";

    private static final Pattern VALID_CODE_PATTERN = Pattern.compile("^[a-zA-Z]\\w{0,49}$");

    private final EntityTypeMapper entityTypeMapper;

    @Override
    public String dynamicTableName(String sql, String tableName) {
        if (!ENTITY_TABLE_PLACEHOLDER.equals(tableName)) {
            return tableName;
        }

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

    /**
     * 解析 entityTypeCode 对应的物理表名（供原生 SQL 使用；调用方须保证 code 合法）。
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
                return entityType.getDedicatedTableName().trim();
            }
            String baseCode = StringUtils.hasText(entityType.getBaseEntityTypeCode())
                    ? entityType.getBaseEntityTypeCode().trim()
                    : entityTypeCode;
            return DYNAMIC_TABLE_PREFIX + baseCode.toLowerCase();
        }

        // 创建过程中元数据可能尚未可见：按约定路由到 ent_{code}
        return DYNAMIC_TABLE_PREFIX + entityTypeCode.toLowerCase();
    }

    private boolean isDedicatedStorage(EntityTypeDO entityType) {
        StorageTypeEnum storageType = StorageTypeEnum.getByCode(entityType.getStorageType());
        return storageType != null && storageType.isDedicated();
    }
}
