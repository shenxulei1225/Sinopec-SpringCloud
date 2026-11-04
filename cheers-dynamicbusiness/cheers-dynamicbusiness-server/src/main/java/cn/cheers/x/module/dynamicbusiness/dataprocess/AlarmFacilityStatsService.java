package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityCategoryRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 按这一座站的告警记录，和每条记录挂上的分类，算出统计。
 * 不写告警。不在读的时候补一条记录。缺站、缺级别列就失败。
 */
@Service
public class AlarmFacilityStatsService {

    private static final DateTimeFormatter CLOCK_TEXT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int BATCH = 200;

    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private CategoryTypeMapper categoryTypeMapper;
    @Resource
    private EntityCategoryRelationMapper relationMapper;
    @Resource
    private AlarmCatalogEnsure alarmCatalogEnsure;
    @Resource
    @Lazy
    private EntityRpcFacadeService entityRpcFacadeService;

    /**
     * 这一座站当前的统计。changedId 是刚写入或刚改处理的那一条，对得上才放进结果。
     * 分类树上还没有挂告警的分类也在结果里，数字是 0。
     */
    public AlarmFacilityStatsView snapshot(long facilityId, Long changedId) {
        if (facilityId <= 0) {
            throw new ServiceException(400, "先选择这一座站，再看告警。");
        }
        List<CategoryDO> categories = categoryMapper.selectByCategoryTypeCode(AlarmTypeCatalog.DIRECTORY);
        CategoryTypeDO type = categoryTypeMapper.selectByCategoryTypeCode(AlarmTypeCatalog.DIRECTORY);
        Long rootId = type == null ? null : type.getTopLevelCategoryId();
        String levelCode = alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_LEVEL);
        String handlingCode = alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_HANDLING);
        Map<Long, Long> parentOf = new LinkedHashMap<>();
        Map<Long, CategoryDO> byId = new LinkedHashMap<>();
        for (CategoryDO category : categories) {
            if (category.getId() == null) {
                continue;
            }
            byId.put(category.getId(), category);
            Long parentId = category.getParentId();
            parentOf.put(category.getId(), parentId != null && parentId > 0 ? parentId : null);
        }
        Map<Long, Map<String, AlarmFacilityCount.Cell>> direct = new LinkedHashMap<>();
        Map<Long, List<Long>> linked = linksOf(new ArrayList<>(byId.keySet()));
        Map<Long, EntityRespDTO> entities = loadStationAlarms(linked, facilityId);
        Map<Long, Long> countedOn = new LinkedHashMap<>();
        for (EntityRespDTO entity : entities.values()) {
            if (entity.getId() == null) {
                continue;
            }
            String level = text(entity, levelCode);
            if (!AlarmFacilityCount.isCountedLevel(level)) {
                continue;
            }
            String status = text(entity, handlingCode);
            List<Long> deepest = AlarmFacilityCount.deepest(linked.get(entity.getId()), parentOf);
            for (Long categoryId : deepest) {
                Map<String, AlarmFacilityCount.Cell> levels = direct.computeIfAbsent(
                        categoryId, ignored -> AlarmFacilityCount.emptyLevels());
                levels.get(level).addStatus(status);
                countedOn.putIfAbsent(entity.getId(), categoryId);
            }
        }
        Map<Long, Map<String, AlarmFacilityCount.Cell>> rolled = AlarmFacilityCount.rollup(parentOf, direct);
        AlarmFacilityStatsView view = new AlarmFacilityStatsView();
        view.setFacilityId(facilityId);
        view.setRootId(rootId);
        List<AlarmFacilityStatsView.CategoryStat> rows = new ArrayList<>();
        for (CategoryDO category : categories) {
            if (category.getId() == null) {
                continue;
            }
            AlarmFacilityStatsView.CategoryStat row = new AlarmFacilityStatsView.CategoryStat();
            row.setId(category.getId());
            Long parentId = parentOf.get(category.getId());
            row.setParentId(parentId);
            row.setName(category.getName() == null ? "" : category.getName());
            row.setCode(category.getCode() == null ? "" : category.getCode());
            row.setSort(category.getSort());
            row.setLevels(rolled.getOrDefault(category.getId(), AlarmFacilityCount.emptyLevels()));
            rows.add(row);
        }
        view.setCategories(rows);
        if (changedId != null) {
            EntityRespDTO changed = entities.get(changedId);
            if (changed != null) {
                view.setChanged(changedOf(changed, countedOn.get(changedId), levelCode, handlingCode));
            }
        }
        return view;
    }

    public Long facilityOf(long entityId) {
        EntityRespDTO entity = entityRpcFacadeService.get(entityId, AlarmTypeCatalog.DIRECTORY);
        if (entity == null) {
            return null;
        }
        Long fromBase = FacilityOwningFieldCodes.extractId(
                entity.getBaseFields() == null ? null : entity.getBaseFields().get(FacilityOwningFieldCodes.FIELD_CODE));
        if (fromBase != null) {
            return fromBase;
        }
        return FacilityOwningFieldCodes.extractId(
                entity.getCustomFields() == null ? null : entity.getCustomFields().get(FacilityOwningFieldCodes.FIELD_CODE));
    }

    private Map<Long, EntityRespDTO> loadStationAlarms(Map<Long, List<Long>> linked, long facilityId) {
        Map<Long, EntityRespDTO> matched = new LinkedHashMap<>();
        List<Long> entityIds = new ArrayList<>(linked.keySet());
        for (int offset = 0; offset < entityIds.size(); offset += BATCH) {
            List<Long> batch = entityIds.subList(offset, Math.min(offset + BATCH, entityIds.size()));
            for (EntityRespDTO entity : entityRpcFacadeService.listByIds(batch, AlarmTypeCatalog.DIRECTORY)) {
                if (entity.getId() == null) {
                    continue;
                }
                Long stationId = FacilityOwningFieldCodes.extractId(
                        entity.getBaseFields() == null ? null : entity.getBaseFields().get(FacilityOwningFieldCodes.FIELD_CODE));
                if (stationId == null && entity.getCustomFields() != null) {
                    stationId = FacilityOwningFieldCodes.extractId(
                            entity.getCustomFields().get(FacilityOwningFieldCodes.FIELD_CODE));
                }
                if (stationId != null && stationId == facilityId) {
                    matched.put(entity.getId(), entity);
                }
            }
        }
        return matched;
    }

    private Map<Long, List<Long>> linksOf(List<Long> categoryIds) {
        Map<Long, List<Long>> linked = new LinkedHashMap<>();
        if (categoryIds.isEmpty()) {
            return linked;
        }
        List<EntityCategoryRelationDO> relations = relationMapper.selectByCategoryIds(
                categoryIds, AlarmTypeCatalog.DIRECTORY);
        for (EntityCategoryRelationDO relation : relations) {
            if (relation.getEntityId() == null || relation.getCategoryId() == null) {
                continue;
            }
            linked.computeIfAbsent(relation.getEntityId(), ignored -> new ArrayList<>()).add(relation.getCategoryId());
        }
        return linked;
    }

    private AlarmFacilityStatsView.ChangedAlarm changedOf(
            EntityRespDTO entity, Long categoryId, String levelCode, String handlingCode) {
        AlarmFacilityStatsView.ChangedAlarm changed = new AlarmFacilityStatsView.ChangedAlarm();
        changed.setId(entity.getId());
        changed.setCategoryId(categoryId);
        changed.setName(baseText(entity, "name"));
        changed.setLevel(text(entity, levelCode));
        changed.setStatus(text(entity, handlingCode));
        changed.setContent(text(entity, alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_CONTENT)));
        changed.setDevice(text(entity, alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_DEVICE)));
        changed.setLocation(text(entity, alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_LOCATION)));
        changed.setTriggerValue(text(entity, alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_TRIGGER)));
        changed.setThreshold(text(entity, alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_THRESHOLD)));
        changed.setPoint(text(entity, alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_POINT)));
        changed.setReview(text(entity, alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_REVIEW)));
        changed.setTrace(text(entity, alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_TRACE)));
        LocalDateTime created = entity.getCreateTime();
        changed.setAtText(created == null ? "" : CLOCK_TEXT.format(created));
        if (changed.getName() == null || changed.getName().isBlank()) {
            changed.setName(changed.getContent() == null || changed.getContent().isBlank() ? "告警" : changed.getContent());
        }
        return changed;
    }

    private static String text(EntityRespDTO entity, String code) {
        if (code == null) {
            return "";
        }
        String fromCustom = textValue(entity.getCustomFields() == null ? null : entity.getCustomFields().get(code));
        if (!fromCustom.isBlank()) {
            return fromCustom;
        }
        return textValue(entity.getBaseFields() == null ? null : entity.getBaseFields().get(code));
    }

    private static String baseText(EntityRespDTO entity, String code) {
        return textValue(entity.getBaseFields() == null ? null : entity.getBaseFields().get(code));
    }

    private static String textValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Map<?, ?> map) {
            Object label = map.get("label");
            if (label == null) {
                label = map.get("value");
            }
            if (label == null) {
                label = map.get("name");
            }
            return textValue(label);
        }
        return String.valueOf(value).trim();
    }
}
