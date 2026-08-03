package cn.cheers.x.module.dynamicbusiness.framework.entitytype;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.EntityTypeEntryKindEnum;
import lombok.Builder;
import lombok.Value;
import org.springframework.util.StringUtils;

/**
 * 解析数据类型入口与存储类型、业务域（Domain）、划分（Scope）的对应关系。
 * <p>
 * domainEntry 与 scopeEntry 互斥；REUSE 亦复用底座但不带域、不圈选。
 * normalizeDomain / domainsEqual 为业务域工具，保留在本类便于调用方就地使用。
 */
@Value
@Builder
public class EntityTypeScopeContext {

    String registryCode;
    String storageEntityTypeCode;
    String domain;
    /** 子数据类型入口：复用基础类型存储，按业务域过滤。 */
    boolean domainEntry;
    /** 划分数据入口：复用基础类型存储，成员由 dynamic_entity_type_scope 维护。 */
    boolean scopeEntry;
    /** 使用已有数据入口：复用基础类型存储，可读写，无 domain / scope 过滤。 */
    boolean reuseEntry;
    /** 分类数据入口：自有存储；树节点 1:1 绑实体。 */
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
                    .domain(kind.isDomainEntry() ? normalizeDomain(entityType.getDomain()) : null)
                    .domainEntry(kind.isDomainEntry())
                    .scopeEntry(kind.isScopeEntry())
                    .reuseEntry(kind.isReuseEntry())
                    .categoryLinked(kind.isCategory())
                    .build();
        }
        return EntityTypeScopeContext.builder()
                .registryCode(entityType.getCode())
                .storageEntityTypeCode(entityType.getCode())
                .domain(null)
                .domainEntry(false)
                .scopeEntry(false)
                .reuseEntry(false)
                .categoryLinked(kind.isCategory())
                .build();
    }

    /**
     * 「未划域」筛选标记：查询参数里传该值表示只要业务域为空的数据。
     * 业务域本身不允许取这个值，型号列表的分组轨道用它表达「未划域」分组。
     */
    public static final String DOMAIN_FILTER_NONE = "__none__";

    /** 该筛选值是否表示「未划域」。 */
    public static boolean isNoneDomainFilter(String domainFilter) {
        return domainFilter != null && DOMAIN_FILTER_NONE.equalsIgnoreCase(domainFilter.trim());
    }

    /**
     * 业务域筛选：筛选值为空表示不过滤；{@link #DOMAIN_FILTER_NONE} 表示只要未划域；否则按业务域相等。
     */
    public static boolean matchesDomainFilter(String value, String domainFilter) {
        if (!StringUtils.hasText(domainFilter)) {
            return true;
        }
        if (isNoneDomainFilter(domainFilter)) {
            return normalizeDomain(value) == null;
        }
        return domainsEqual(value, domainFilter);
    }

    public static String normalizeDomain(String domain) {
        if (!StringUtils.hasText(domain)) {
            return null;
        }
        return domain.trim();
    }

    public static boolean domainsEqual(String left, String right) {
        String a = normalizeDomain(left);
        String b = normalizeDomain(right);
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }
}
