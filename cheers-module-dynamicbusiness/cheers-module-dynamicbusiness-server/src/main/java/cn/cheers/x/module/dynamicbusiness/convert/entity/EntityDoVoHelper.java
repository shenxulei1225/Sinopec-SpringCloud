package cn.cheers.x.module.dynamicbusiness.convert.entity;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 实体 DO/VO 转换辅助工具。
 */
public final class EntityDoVoHelper {

    private EntityDoVoHelper() {
    }

    public static EntityRespVO toRespVO(EntityDO entity, CustomFieldValidationService customFieldValidationService) {
        if (entity == null) {
            return null;
        }
        EntityRespVO respVO = EntityConvert.INSTANCE.convert(entity);
        if (respVO != null && respVO.getCustomFields() != null && entity.getModelId() != null) {
            Map<String, Object> decrypted = customFieldValidationService.decryptCustomFields(
                    respVO.getCustomFields(), entity.getModelId());
            respVO.setCustomFields(customFieldValidationService.presentCustomFieldsForApi(
                    decrypted, entity.getModelId()));
        }
        return respVO;
    }

    public static EntityRespVO toLightRespVO(EntityDO entity) {
        if (entity == null) {
            return null;
        }
        EntityRespVO light = new EntityRespVO();
        light.setId(entity.getId());
        light.setName(entity.getName());
        light.setParentId(entity.getParentId());
        light.setModelId(entity.getModelId());
        light.setBusinessTypeCode(entity.getBusinessTypeCode());
        light.setSort(entity.getSort());
        return light;
    }

    public static List<EntityRespVO> toLightRespVOList(List<EntityDO> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<EntityRespVO> result = new ArrayList<>(entities.size());
        for (EntityDO entity : entities) {
            EntityRespVO respVO = toLightRespVO(entity);
            if (respVO != null) {
                result.add(respVO);
            }
        }
        return result;
    }

    public static List<EntityRespVO> toRespVOList(List<EntityDO> entities, CustomFieldValidationService customFieldValidationService) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<EntityRespVO> result = new ArrayList<>(entities.size());
        for (EntityDO entity : entities) {
            EntityRespVO respVO = toRespVO(entity, customFieldValidationService);
            if (respVO != null) {
                result.add(respVO);
            }
        }
        return result;
    }
}
