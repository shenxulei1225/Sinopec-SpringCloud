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
     * 单条转换：仅写入已加载的 {@link EntityDO#getDedicatedBaseFieldValues()}。
     * <p>调用方须先 {@code listByIdsWithDedicatedBaseFields}（或等价一次加载）；
     * 禁止在此按行 {@code mergePhysicalColumnsIntoBaseFields}。</p>
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
     * 列表转换：核心列 + 本页已加载基础字段（含 REF API 形态）。
     * <p>不做扩展字段加解密 / 键映射（不调 getFieldConfigs）：列表不展示扩展字段，
     * 与浏览流程无关；详情单条仍走 {@link #toRespVO}。</p>
     * 禁止按行 {@code mergePhysicalColumnsIntoBaseFields}。
     */
    public static List<EntityRespVO> toRespVOList(List<EntityDO> entities,
                                                  CustomFieldValidationService customFieldValidationService,
                                                  EntityDedicatedColumnService dedicatedColumnService) {
        // customFieldValidationService 保留参数以兼容旧调用方；列表路径刻意不使用
        return toRespVOListSkipCustomPresent(entities, dedicatedColumnService);
    }

    /**
     * 列表装 VO：扩展字段原样保留（仅供 LIGHT 偶发从 custom 提升 REF）；不做型号字段配置查询。
     */
    public static List<EntityRespVO> toRespVOListSkipCustomPresent(List<EntityDO> entities,
                                                                   EntityDedicatedColumnService dedicatedColumnService) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, Map<String, EntityTypeBaseFieldDO>> metaByType = new HashMap<>();
        List<EntityRespVO> result = new ArrayList<>(entities.size());
        for (EntityDO entity : entities) {
            EntityRespVO respVO = EntityConvert.INSTANCE.convert(entity);
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

    /**
     * 详情/写读回显：扩展字段按型号配置解密并规范为 fieldCode。
     * 仅单条路径使用；列表禁止走此逻辑。
     */
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
