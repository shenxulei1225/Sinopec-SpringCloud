package cn.cheers.x.module.dynamicbusiness.framework.security;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityFieldMapsSupport;
import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogEntryAccessService;
import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogRoleGrantService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 接口上的目录权限，给 {@code @PreAuthorize} 用。
 * 注解传入这次打开的数据目录注册编码、请求里的类型编码、对象（分类、型号、实体、详情）和操作。
 * 本页目录上要有这个对象的这项授权。请求的类型是另一本目录时（例如检查项页里的设备栏），
 * 那本目录上也要有同一对象、同一项操作的授权。
 * 分类树是公共的筛选视角：查看分类只要本页有任一查看授权；改分类节点要本页的分类授权。
 * 不负责选表、不核对记录或型号是否属于目录（见 CatalogEntryAccessService）。
 * 请求里没有目录注册编码时返回 false，接口 403。
 */
@Component("catalogAccess")
public class CatalogAccess {

    @Resource
    private CatalogRoleGrantService catalogRoleGrantService;
    @Resource
    private CatalogEntryAccessService catalogEntryAccessService;

    static final String ENTITY = "entity";
    static final String DETAIL = "detail";

    /** 实体：只核对本页目录。用于请求里不带类型编码的接口（导入按行另核对）。 */
    public boolean allow(String registryCode, String operation) {
        return allowSubject(registryCode, null, ENTITY, operation);
    }

    /** 实体：核对本页目录；请求的类型是另一本目录时，再核对那本目录。 */
    public boolean allow(String registryCode, String requestedTypeCode, String operation) {
        return allowSubject(registryCode, requestedTypeCode, ENTITY, operation);
    }

    /**
     * 指定对象（model、entity、detail、category）：核对本页目录；请求的类型是另一本目录时，再核对那本目录。
     */
    public boolean allowSubject(String registryCode, String requestedTypeCode, String subject, String operation) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (!catalogRoleGrantService.allow(userId, registryCode, subject, operation)) {
            return false;
        }
        String other = catalogEntryAccessService.otherCatalogCode(registryCode, requestedTypeCode);
        return other == null || catalogRoleGrantService.allow(userId, other, subject, operation);
    }

    /**
     * 实体之间的关联（详情页里的关联块）：本页目录上要有详情的这项授权。
     * 关联的另一端是别的目录时，那本目录上要有实体查看授权，否则等于借关联看到没授权的数据。
     * 没传另一端类型（按一端列关联）时只核对本页。
     */
    public boolean allowRelation(String registryCode, String sourceTypeCode, String targetTypeCode, String operation) {
        if (!allowSubject(registryCode, sourceTypeCode, DETAIL, operation)) {
            return false;
        }
        if (targetTypeCode == null || targetTypeCode.isBlank()) {
            return true;
        }
        String other = catalogEntryAccessService.otherCatalogCode(registryCode, targetTypeCode);
        return other == null
                || catalogRoleGrantService.allow(SecurityFrameworkUtils.getLoginUserId(), other, ENTITY, "query");
    }

    /** 查看分类树：本页目录上有任一查看授权即可。分类树不属于任何目录，不再核对第二本。 */
    public boolean allowCategoryQuery(String registryCode) {
        return catalogRoleGrantService.allowAnyQuery(SecurityFrameworkUtils.getLoginUserId(), registryCode);
    }

    /** 新建、修改：类型编码写在 baseFields 里。 */
    public boolean allowBaseFields(String registryCode, Map<String, Object> baseFields, String operation) {
        return allow(registryCode, EntityFieldMapsSupport.getEntityTypeCode(baseFields), operation);
    }

    /**
     * 导入、批量新建：每一行可带自己的类型编码，逐个核对；任一行没有授权就整批 403。
     * 不跳过、不按有权限的行部分导入。
     */
    public void assertAllowTypes(String registryCode, Collection<String> typeCodes, String operation) {
        Set<String> distinct = new LinkedHashSet<>();
        if (typeCodes != null) {
            for (String code : typeCodes) {
                distinct.add(code == null ? "" : code.trim());
            }
        }
        if (distinct.isEmpty()) {
            distinct.add("");
        }
        for (String code : distinct) {
            if (!allow(registryCode, code.isEmpty() ? null : code, operation)) {
                throw new ServiceException(GlobalErrorCodeConstants.FORBIDDEN);
            }
        }
    }
}
