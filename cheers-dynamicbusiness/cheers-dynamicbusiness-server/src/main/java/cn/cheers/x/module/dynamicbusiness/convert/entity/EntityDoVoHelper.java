package cn.cheers.x.module.dynamicbusiness.convert.entity;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

    /**
     * 单条转换：优先使用已加载的 {@link EntityDO#getDedicatedBaseFieldValues()}；
     * 若无则走一次按配置 SELECT 全部基础列（详情路径；禁止探列）。
     */
    public static EntityRespVO toRespVO(EntityDO entity,
                                        CustomFieldValidationService customFieldValidationService,
                                        EntityDedicatedColumnService dedicatedColumnService) {
        if (entity == null) {
            return null;
        }
        EntityRespVO respVO = convertWithoutPhysicalMerge(entity, customFieldValidationService);
        if (respVO == null) {
            return null;
        }
        if (hasDedicatedBaseFieldValues(entity) && dedicatedColumnService != null) {
            ensureBaseFieldsMap(respVO);
            dedicatedColumnService.applyDedicatedBaseFieldValues(entity, respVO.getBaseFields());
            stripPhysicalKeysFromCustom(respVO);
        } else if (dedicatedColumnService != null && respVO.getBaseFields() != null) {
            dedicatedColumnService.mergePhysicalColumnsIntoBaseFields(entity, respVO.getBaseFields());
            stripPhysicalKeysFromCustom(respVO);
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
        if (hasDedicatedBaseFieldValues(entity) && dedicatedColumnService != null) {
            ensureBaseFieldsMap(light);
            dedicatedColumnService.applyDedicatedBaseFieldValues(entity, light.getBaseFields());
        } else if (dedicatedColumnService != null && light.getBaseFields() != null) {
            dedicatedColumnService.mergePhysicalColumnsIntoBaseFields(entity, light.getBaseFields());
        }
        return light;
    }

    public static List<EntityRespVO> toLightRespVOList(List<EntityDO> entities) {
        return toLightRespVOList(entities, null);
    }

    /**
     * 轻量列表：仅写入已加载的 dedicatedBaseFieldValues，不调用按行 merge。
     */
    public static List<EntityRespVO> toLightRespVOList(List<EntityDO> entities,
                                                       EntityDedicatedColumnService dedicatedColumnService) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<EntityRespVO> result = new ArrayList<>(entities.size());
        for (EntityDO entity : entities) {
            if (entity == null) {
                continue;
            }
            EntityRespVO light = new EntityRespVO();
            light.setId(entity.getId());
            light.setSort(entity.getSort());
            light.setBaseFields(EntityFieldMapsSupport.buildBaseFieldsFromEntityDO(entity));
            if (hasDedicatedBaseFieldValues(entity) && dedicatedColumnService != null) {
                ensureBaseFieldsMap(light);
                dedicatedColumnService.applyDedicatedBaseFieldValues(entity, light.getBaseFields());
            }
            result.add(light);
        }
        return result;
    }

    public static List<EntityRespVO> toRespVOList(List<EntityDO> entities, CustomFieldValidationService customFieldValidationService) {
        return toRespVOList(entities, customFieldValidationService, null);
    }

    /**
     * 列表转换：DO→VO 后把本页已加载的 dedicatedBaseFieldValues 写入 baseFields（含 REF API 形态）。
     * 不调用 {@link EntityDedicatedColumnService#mergePhysicalColumnsIntoBaseFields}（禁止按行二次读列）。
     */
    public static List<EntityRespVO> toRespVOList(List<EntityDO> entities,
                                                  CustomFieldValidationService customFieldValidationService,
                                                  EntityDedicatedColumnService dedicatedColumnService) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, Map<String, EntityTypeBaseFieldDO>> metaByType = new HashMap<>();
        List<EntityRespVO> result = new ArrayList<>(entities.size());
        for (EntityDO entity : entities) {
            EntityRespVO respVO = convertWithoutPhysicalMerge(entity, customFieldValidationService);
            if (respVO == null) {
                continue;
            }
            if (hasDedicatedBaseFieldValues(entity) && dedicatedColumnService != null) {
                ensureBaseFieldsMap(respVO);
                String typeCode = entity.getEntityTypeCode() == null ? "" : entity.getEntityTypeCode().trim();
                Map<String, EntityTypeBaseFieldDO> meta = metaByType.computeIfAbsent(
                        typeCode, dedicatedColumnService::loadEnabledBaseFieldMeta);
                dedicatedColumnService.applyDedicatedBaseFieldValues(entity, respVO.getBaseFields(), meta);
                stripPhysicalKeysFromCustom(respVO);
            }
            result.add(respVO);
        }
        return result;
    }

    private static EntityRespVO convertWithoutPhysicalMerge(EntityDO entity,
                                                            CustomFieldValidationService customFieldValidationService) {
        EntityRespVO respVO = EntityConvert.INSTANCE.convert(entity);
        if (respVO != null && respVO.getCustomFields() != null && entity.getModelId() != null
                && customFieldValidationService != null) {
            Map<String, Object> decrypted = customFieldValidationService.decryptCustomFields(
                    respVO.getCustomFields(), entity.getModelId());
            respVO.setCustomFields(customFieldValidationService.presentCustomFieldsForApi(
                    decrypted, entity.getModelId()));
        }
        return respVO;
    }

    private static boolean hasDedicatedBaseFieldValues(EntityDO entity) {
        return entity.getDedicatedBaseFieldValues() != null && !entity.getDedicatedBaseFieldValues().isEmpty();
    }

    private static void ensureBaseFieldsMap(EntityRespVO respVO) {
        if (respVO.getBaseFields() == null) {
            respVO.setBaseFields(new LinkedHashMap<>());
        }
    }

    private static void stripPhysicalKeysFromCustom(EntityRespVO respVO) {
        if (respVO.getCustomFields() == null || respVO.getBaseFields() == null) {
            return;
        }
        for (String fieldCode : respVO.getBaseFields().keySet()) {
            if (fieldCode != null && fieldCode.startsWith("FLD-")) {
                respVO.getCustomFields().remove(fieldCode);
            }
        }
    }
}
