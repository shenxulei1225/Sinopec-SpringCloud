package cn.cheers.x.module.dynamicbusiness.service.entitytypescope;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytypescope.EntityTypeScopeMapper;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.EntityTypeEntryKindEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.SCOPE_ENTITY_TYPE_NOT_EXISTS;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.SCOPE_ENTITY_TYPE_NOT_SCOPE;

@Service
@Validated
public class EntityTypeScopeQueryServiceImpl implements EntityTypeScopeQueryService {

    @Resource
    private EntityTypeScopeMapper entityTypeScopeMapper;
    @Resource
    private EntityTypeMapper entityTypeMapper;

    @Override
    public List<Long> listEntityIds(String entityTypeCode) {
        EntityTypeDO type = requireScopeType(entityTypeCode);
        return entityTypeScopeMapper.selectEntityIdsByCode(type.getCode());
    }

    private EntityTypeDO requireScopeType(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            throw exception(SCOPE_ENTITY_TYPE_NOT_EXISTS);
        }
        EntityTypeDO type = entityTypeMapper.selectByCode(entityTypeCode.trim());
        if (type == null) {
            throw exception(SCOPE_ENTITY_TYPE_NOT_EXISTS);
        }
        if (!EntityTypeEntryKindEnum.fromCode(type.getEntryKind()).isScopeEntry()) {
            throw exception(SCOPE_ENTITY_TYPE_NOT_SCOPE);
        }
        return type;
    }
}
