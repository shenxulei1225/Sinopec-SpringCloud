package cn.cheers.x.module.dynamicbusiness.framework.entitytype;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 把请求里的数据类型编码解析成实际存储类型。
 *
 * <p>侧边栏注册项编码（如子数据类型入口 {@code task_patrol}）只是入口编号，
 * 实体、型号、分类关联一律用实际存储类型（如 {@code task}）作身份。
 * 需要区分业务域时用实体行上的 domain，不用注册编码。</p>
 */
@Component
@RequiredArgsConstructor
public class EntityTypeScopeResolver {

    private final EntityTypeMapper entityTypeMapper;

    /**
     * 解析入口编码对应的存储类型 / 业务域上下文；编码为空或未登记时返回 null。
     */
    public EntityTypeScopeContext resolve(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            return null;
        }
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode.trim());
        return EntityTypeScopeContext.from(entityType);
    }

    /**
     * 解析实际存储类型；未登记的编码按原值返回（沿用「约定优于配置」的动态表路由）。
     */
    public String resolveStorageEntityTypeCode(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            return null;
        }
        EntityTypeScopeContext scope = resolve(entityTypeCode);
        return scope != null ? scope.getStorageEntityTypeCode() : entityTypeCode.trim();
    }
}
