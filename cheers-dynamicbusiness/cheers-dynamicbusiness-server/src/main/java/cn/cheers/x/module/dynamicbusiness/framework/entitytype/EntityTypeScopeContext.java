package cn.cheers.x.module.dynamicbusiness.framework.entitytype;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.EntityTypeEntryKindEnum;
import lombok.Builder;
import lombok.Value;
import org.springframework.util.StringUtils;

/**
 * 解析数据类型入口与存储类型、业务域（dataScope）的对应关系。
 */
@Value
@Builder
public class EntityTypeScopeContext {

    String registryCode;
    String storageEntityTypeCode;
    String dataScope;
    boolean scoped;
    /** 分类数据入口：复用基础类型存储，不按业务域过滤。 */
    boolean categoryLinked;

    public static EntityTypeScopeContext from(EntityTypeDO entityType) {
        if (entityType == null) {
            return null;
        }
        EntityTypeEntryKindEnum kind = EntityTypeEntryKindEnum.fromCode(entityType.getEntryKind());
        if (kind.reusesBaseStorage() && StringUtils.hasText(entityType.getBaseEntityTypeCode())) {
            return EntityTypeScopeContext.builder()
                    .registryCode(entityType.getCode())
                    .storageEntityTypeCode(entityType.getBaseEntityTypeCode().trim())
                    .dataScope(kind.isScoped() ? normalizeScope(entityType.getDataScope()) : null)
                    .scoped(kind.isScoped())
                    .categoryLinked(kind.isCategory())
                    .build();
        }
        return EntityTypeScopeContext.builder()
                .registryCode(entityType.getCode())
                .storageEntityTypeCode(entityType.getCode())
                .dataScope(null)
                .scoped(false)
                .categoryLinked(false)
                .build();
    }

    public static String normalizeScope(String dataScope) {
        if (!StringUtils.hasText(dataScope)) {
            return null;
        }
        return dataScope.trim();
    }

    public static boolean scopesEqual(String left, String right) {
        String a = normalizeScope(left);
        String b = normalizeScope(right);
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }
}
