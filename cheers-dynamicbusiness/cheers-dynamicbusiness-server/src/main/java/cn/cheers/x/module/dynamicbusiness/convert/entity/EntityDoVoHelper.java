package cn.cheers.x.module.dynamicbusiness.convert.entity;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
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
        return toRespVO(entity, customFieldValidationService, null);
    }

    public static EntityRespVO toRespVO(EntityDO entity,
                                        CustomFieldValidationService customFieldValidationService,
                                        EntityDedicatedColumnService dedicatedColumnService) {
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
        if (respVO != null && dedicatedColumnService != null && respVO.getBaseFields() != null) {
            dedicatedColumnService.mergePhysicalColumnsIntoBaseFields(entity, respVO.getBaseFields());
            if (respVO.getCustomFields() != null) {
                for (String fieldCode : respVO.getBaseFields().keySet()) {
                    if (fieldCode != null && fieldCode.startsWith("FLD-")) {
                        respVO.getCustomFields().remove(fieldCode);
                    }
                }
            }
        }
        return respVO;
    }

    public static EntityRespVO toLightRespVO(EntityDO entity) {
        return toLightRespVO(entity, null);
    }

    public static EntityRespVO toLightRespVO(EntityDO entity, EntityDedicatedColumnService dedicatedColumnService) {
        if (entity == null) {
            return null;
        }
        EntityRespVO light = new EntityRespVO();
        light.setId(entity.getId());
        light.setSort(entity.getSort());
        light.setBaseFields(EntityFieldMapsSupport.buildBaseFieldsFromEntityDO(entity));
        if (dedicatedColumnService != null && light.getBaseFields() != null) {
            dedicatedColumnService.mergePhysicalColumnsIntoBaseFields(entity, light.getBaseFields());
        }
        return light;
    }

    public static List<EntityRespVO> toLightRespVOList(List<EntityDO> entities) {
        return toLightRespVOList(entities, null);
    }

    public static List<EntityRespVO> toLightRespVOList(List<EntityDO> entities,
                                                       EntityDedicatedColumnService dedicatedColumnService) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<EntityRespVO> result = new ArrayList<>(entities.size());
        for (EntityDO entity : entities) {
            EntityRespVO respVO = toLightRespVO(entity, dedicatedColumnService);
            if (respVO != null) {
                result.add(respVO);
            }
        }
        return result;
    }

    public static List<EntityRespVO> toRespVOList(List<EntityDO> entities, CustomFieldValidationService customFieldValidationService) {
        return toRespVOList(entities, customFieldValidationService, null);
    }

    public static List<EntityRespVO> toRespVOList(List<EntityDO> entities,
                                                  CustomFieldValidationService customFieldValidationService,
                                                  EntityDedicatedColumnService dedicatedColumnService) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<EntityRespVO> result = new ArrayList<>(entities.size());
        for (EntityDO entity : entities) {
            EntityRespVO respVO = toRespVO(entity, customFieldValidationService, dedicatedColumnService);
            if (respVO != null) {
                result.add(respVO);
            }
        }
        return result;
    }
}
