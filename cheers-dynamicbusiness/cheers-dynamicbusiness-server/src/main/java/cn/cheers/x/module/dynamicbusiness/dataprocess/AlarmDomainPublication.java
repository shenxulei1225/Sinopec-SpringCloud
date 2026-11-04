package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.FacilityOwningFieldEnsureService;
import cn.cheers.x.module.dynamicbusiness.service.strategy.StrategyPublicationReader;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 把告警发布成策略目录里的一个域。
 *
 * 管什么：写一条「告警」域，写它的条件项，并把已经启用的设备告警策略改成这个域里的勾选。
 * 不管什么：不判定，不执行动作。点位是否成立仍由告警调用方看。
 * 禁止：在判定模块里写告警。旧记录停用后不再参加告警判断。
 */
@Service
public class AlarmDomainPublication {

    private static final String CATEGORY_CODE = "strategy-generic";

    @Resource
    private ModelMapper modelMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private EntityRepository entityRepository;
    @Resource
    private EntityService entityService;
    @Resource
    private EntityRpcFacadeService entityRpcFacadeService;
    @Resource
    private EntityCategoryRelationService entityCategoryRelationService;
    @Resource
    private AlarmDomainCatalogEnsure domainCatalog;
    @Resource
    private AlarmStrategyCatalogEnsure strategyCatalog;
    @Resource
    private StrategyPublicationReader publicationReader;
    @Resource
    private FacilityOwningFieldEnsureService facilityOwningFieldEnsureService;

    public void ensure() {
        ModelDO generic = modelMapper.selectByNameAndEntityTypeCode(
                StrategyRecordFields.MODEL, StrategyRecordFields.DIRECTORY);
        if (generic == null || generic.getId() == null) {
            return;
        }
        Long categoryId = categoryId(CATEGORY_CODE);
        ensureDomain(generic.getId(), categoryId);
        ModelDO device = modelMapper.selectByNameAndEntityTypeCode(
                AlarmDomainCatalogEnsure.MODEL, StrategyRecordFields.DIRECTORY);
        if (device == null || device.getId() == null) {
            return;
        }
        List<AlarmConditionCatalog.Published> vocabulary = AlarmConditionCatalog.conditions(domainCatalog.loadPointSentences());
        for (EntityDO row : entityRepository.findByModelId(device.getId(), StrategyRecordFields.DIRECTORY)) {
            if (row == null || row.getId() == null || !Integer.valueOf(1).equals(row.getStatus())) {
                continue;
            }
            migrate(row, generic.getId(), categoryId, vocabulary, facilityOwningFieldEnsureService.onlyStationId());
        }
    }

    private void ensureDomain(Long modelId, Long categoryId) {
        EntityRespDTO existing = entityRpcFacadeService.getByCode(
                StrategyRecordFields.DOMAIN_ALARM, StrategyRecordFields.DIRECTORY);
        if (existing != null && existing.getId() != null) {
            file(existing.getId(), categoryId);
            return;
        }
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(code(StrategyRecordFields.KIND), StrategyRecordFields.KIND_DOMAIN);
        fields.put(code(StrategyRecordFields.CONDITION_CATALOG), StrategyRecordFields.DIRECTORY);
        fields.put(code(StrategyRecordFields.ACTION_CATALOG), AlarmRuleCatalogEnsure.DIRECTORY);
        fields.put(code(StrategyRecordFields.COMBINE), "ALL");
        fields.put(code(StrategyRecordFields.ALLOWED_MODES),
                StrategyRecordFields.ACTION_SKIP + "、" + StrategyRecordFields.ACTION_RUN);
        Long stationId = facilityOwningFieldEnsureService.onlyStationId();
        Long id = create(modelId, categoryId, "告警", StrategyRecordFields.DOMAIN_ALARM, stationId, fields);
        file(id, categoryId);
    }

    private void migrate(EntityDO row, Long modelId, Long categoryId,
                         List<AlarmConditionCatalog.Published> vocabulary, Long stationId) {
        String publishedCode = "pub-" + row.getCode();
        EntityRespDTO existing = entityRpcFacadeService.getByCode(publishedCode, StrategyRecordFields.DIRECTORY);
        if (existing == null || existing.getId() == null) {
            List<String> models = texts(raw(row, domainCatalog.modelsFieldCode()));
            List<String> selected = new ArrayList<>();
            for (String condition : texts(raw(row, domainCatalog.conditionFieldCode()))) {
                for (String model : models.isEmpty() ? List.of("") : models) {
                    if (AlarmDomainCatalogEnsure.MODELS_ALL.equals(model)) {
                        model = "";
                    }
                    String token = scoped(condition, model);
                    String itemCode = "cond-" + token.replace(':', '-');
                    String name = label(token, vocabulary);
                    Long itemId = ensureCondition(modelId, itemCode, name, token, stationId);
                    selected.add(String.valueOf(itemId));
                }
            }
            Map<String, Object> fields = new LinkedHashMap<>();
            fields.put(code(StrategyRecordFields.KIND), StrategyRecordFields.KIND_STRATEGY);
            fields.put(code(StrategyRecordFields.DOMAIN_CODE), StrategyRecordFields.DOMAIN_ALARM);
            fields.put(code(StrategyRecordFields.MATCH), text(row, domainCatalog.matchFieldCode()));
            fields.put(code(StrategyRecordFields.ACTION_MODE), mode(text(row, domainCatalog.executeFieldCode())));
            fields.put(code(StrategyRecordFields.CONDITION_IDS), selected);
            fields.put(code(StrategyRecordFields.ACTION_IDS), new ArrayList<>(refIds(raw(row, strategyCatalog.fieldCode(AlarmStrategyCatalogEnsure.FIELD_RULES)))));
            create(modelId, categoryId, row.getName(), publishedCode, stationId, fields);
        }
        retire(row.getId());
    }

    private Long ensureCondition(Long modelId, String itemCode, String name, String token, Long facilityId) {
        EntityRespDTO existing = entityRpcFacadeService.getByCode(itemCode, StrategyRecordFields.DIRECTORY);
        if (existing != null && existing.getId() != null) {
            return existing.getId();
        }
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(code(StrategyRecordFields.KIND), StrategyRecordFields.KIND_CONDITION);
        fields.put(code(StrategyRecordFields.DOMAIN_CODE), StrategyRecordFields.DOMAIN_ALARM);
        fields.put(code(StrategyRecordFields.CONDITION_IDS), token);
        return create(modelId, null, name, itemCode, facilityId, fields);
    }

    private Long create(Long modelId, Long categoryId, String name, String code, Long facilityId, Map<String, Object> fields) {
        EntityCreateReqVO request = new EntityCreateReqVO();
        request.setRegistryCode(StrategyRecordFields.DIRECTORY);
        Map<String, Object> baseFields = new LinkedHashMap<>();
        baseFields.put("entityTypeCode", StrategyRecordFields.DIRECTORY);
        baseFields.put("modelId", modelId);
        baseFields.put("name", name);
        baseFields.put("code", code);
        baseFields.put("status", 1);
        if (facilityId != null) {
            baseFields.put(FacilityOwningFieldCodes.FIELD_CODE, FacilityOwningFieldCodes.toApiRef(facilityId));
        }
        request.setBaseFields(baseFields);
        request.setCustomFields(fields);
        Long id = entityService.create(request);
        file(id, categoryId);
        return id;
    }

    private void retire(Long entityId) {
        EntityRespVO old = entityService.get(entityId, StrategyRecordFields.DIRECTORY);
        if (old == null) {
            return;
        }
        Map<String, Object> baseFields = old.getBaseFields() == null
                ? new LinkedHashMap<>() : new LinkedHashMap<>(old.getBaseFields());
        baseFields.put("entityTypeCode", StrategyRecordFields.DIRECTORY);
        baseFields.put("status", 0);
        EntityUpdateReqVO update = new EntityUpdateReqVO();
        update.setId(entityId);
        update.setRegistryCode(StrategyRecordFields.DIRECTORY);
        update.setBaseFields(baseFields);
        update.setCustomFields(old.getCustomFields());
        entityService.update(update);
        for (Long category : entityCategoryRelationService.listCategoryIdsByEntityId(entityId, StrategyRecordFields.DIRECTORY)) {
            entityCategoryRelationService.disassociate(entityId, category, StrategyRecordFields.DIRECTORY);
        }
    }

    private void file(Long entityId, Long categoryId) {
        if (entityId == null || categoryId == null) {
            return;
        }
        if (!entityCategoryRelationService.existsRelation(entityId, categoryId, StrategyRecordFields.DIRECTORY)) {
            entityCategoryRelationService.associate(entityId, categoryId, StrategyRecordFields.DIRECTORY);
        }
    }

    private String code(String name) {
        return publicationReader.fieldCode(name);
    }

    private Long categoryId(String code) {
        for (CategoryDO row : categoryMapper.selectByCategoryTypeCode(StrategyRecordFields.DIRECTORY)) {
            if (code.equals(row.getCode()) && !Boolean.TRUE.equals(row.getDeleted())) {
                return row.getId();
            }
        }
        return null;
    }

    private String label(String token, List<AlarmConditionCatalog.Published> vocabulary) {
        if (token.startsWith("open:")) {
            String[] parts = token.split(":");
            long ruleId = Long.parseLong(parts[parts.length - 1]);
            EntityDO rule = entityRepository.findById(ruleId, AlarmRuleCatalogEnsure.DIRECTORY);
            String text = (rule == null || rule.getName() == null ? "规则" + ruleId : rule.getName()) + "还开着";
            if (parts.length == 3) {
                return modelName(parts[1]) + " · " + text;
            }
            return text;
        }
        String bare = token;
        String prefix = "";
        if (token.startsWith("bit:")) {
            String[] parts = token.split(":", 4);
            if (parts.length == 4) {
                bare = "bit:" + parts[2] + ":" + parts[3];
                prefix = modelName(parts[1]) + " · ";
            }
        }
        for (AlarmConditionCatalog.Published item : vocabulary) {
            if (token.equals(item.code()) || bare.equals(item.code())) {
                return prefix + item.label();
            }
        }
        return token;
    }

    private String modelName(String id) {
        ModelDO model = modelMapper.selectById(Long.parseLong(id));
        return model == null || model.getName() == null ? id : model.getName();
    }

    private static String scoped(String condition, String model) {
        if (model == null || model.isBlank() || condition.contains(":" + model + ":")) {
            return condition;
        }
        if (condition.startsWith("open:")) {
            return "open:" + model + ":" + condition.substring("open:".length());
        }
        if (condition.startsWith("bit:")) {
            String[] parts = condition.split(":", 4);
            if (parts.length == 3) {
                return "bit:" + model + ":" + parts[1] + ":" + parts[2];
            }
        }
        return condition;
    }

    private static String mode(String raw) {
        if ("RUN".equals(raw)) {
            return StrategyRecordFields.ACTION_RUN;
        }
        return StrategyRecordFields.ACTION_SKIP;
    }

    private static String text(EntityDO row, String fieldCode) {
        Object value = raw(row, fieldCode);
        return value == null ? "" : String.valueOf(value).trim();
    }

    private static Object raw(EntityDO row, String fieldCode) {
        if (row.getCustomFields() == null || fieldCode == null) {
            return null;
        }
        return row.getCustomFields().get(fieldCode);
    }

    private static List<String> texts(Object raw) {
        if (raw instanceof Collection<?> collection) {
            List<String> values = new ArrayList<>();
            for (Object item : collection) {
                if (item != null && !String.valueOf(item).isBlank()) {
                    values.add(String.valueOf(item).trim());
                }
            }
            return values;
        }
        if (raw == null || String.valueOf(raw).isBlank()) {
            return List.of();
        }
        return List.of(String.valueOf(raw).trim());
    }

    private static Set<String> refIds(Object raw) {
        Set<String> ids = new LinkedHashSet<>();
        if (!(raw instanceof Collection<?> collection)) {
            return ids;
        }
        for (Object item : collection) {
            if (item instanceof Map<?, ?> map && map.get("id") != null) {
                ids.add(String.valueOf(map.get("id")));
            } else if (item != null && !String.valueOf(item).isBlank()) {
                ids.add(String.valueOf(item).trim());
            }
        }
        return ids;
    }
}
