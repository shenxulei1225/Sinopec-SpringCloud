package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityDeleteReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 管廊三条设备告警策略在启动时用实体创建写好。
 * 运行状态、按设计抑制不在启动时创建。已经停用的这两条型号保持停用。
 * 启用和停用由超级管理员在型号上改状态。
 * 检修和启动没有对应的点，不建。搁置由人在告警记录上填时间。
 * 不判断读数，也不写告警记录。
 */
@Service
public class AlarmBehaviorCatalogEnsure {

    public static final String MODEL_STATE = "运行状态";
    public static final String MODEL_SUPPRESS = "按设计抑制";
    public static final String FIELD_STATE_POINT = "状态点位";
    public static final String FIELD_ACTION = "这一状态下";
    public static final String FIELD_LEAD = "先成立的规则";
    public static final String FIELD_HELD = "被压住的规则";

    @Resource
    private ModelMapper modelMapper;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private EntityService entityService;
    @Resource
    private EntityRpcFacadeService entityRpcFacadeService;
    @Resource
    private EntityCategoryRelationService entityCategoryRelationService;
    @Resource
    private AlarmRuleCatalogEnsure alarmRuleCatalogEnsure;
    @Resource
    private AlarmDomainCatalogEnsure alarmDomainCatalogEnsure;

    /**
     * 不再创建运行状态和按设计抑制。
     * 超级管理员可以在型号上改启用和停用。启动时不改这两条已经停用的型号。
     */
    public void ensure() {
    }

    private String codeOf(String name) {
        FieldDO field = findField(name);
        if (field == null || field.getCode() == null || field.getCode().isBlank()) {
            throw new ServiceException(500, "策略目录缺少字段「" + name + "」");
        }
        return field.getCode();
    }

    public String fieldCode(String name) {
        FieldDO field = findField(name);
        if (field == null || field.getCode() == null || field.getCode().isBlank()) {
            throw new ServiceException(500, "策略目录缺少字段「" + name + "」");
        }
        return field.getCode();
    }

    private FieldDO findField(String name) {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        List<FieldDO> rows = fieldMapper.selectList(new LambdaQueryWrapperX<FieldDO>()
                .eq(FieldDO::getName, name)
                .eq(FieldDO::getTenantId, tenantId)
                .eq(FieldDO::getDeleted, false));
        if (rows.size() > 1) {
            throw new ServiceException(500, "字段「" + name + "」有多条，对不上");
        }
        return rows.isEmpty() ? null : rows.get(0);
    }

    /**
     * 管廊里点表已经写明的场景，建成告警策略，挂在「告警」下。
     * 水泵没有运行时，不执行水位低。
     * 水泵故障还开着时，不执行三条水位。
     * 液压井盖急停还开着时，不执行故障。
     * 还停在旧型号上的同编号记录删掉后按新字段重建。
     */
    public void seedGallery(Long categoryId, Long deviceCategoryId, Long stationId) {
        if (categoryId == null || deviceCategoryId == null || stationId == null) {
            throw new ServiceException(500, "智慧管廊的告警策略还不能建");
        }
        Long modelId = alarmDomainCatalogEnsure.modelId();
        String models = alarmDomainCatalogEnsure.modelsFieldCode();
        String condition = alarmDomainCatalogEnsure.conditionFieldCode();
        String match = alarmDomainCatalogEnsure.matchFieldCode();
        String execute = alarmDomainCatalogEnsure.executeFieldCode();
        String rules = codeOf(AlarmStrategyCatalogEnsure.FIELD_RULES);
        String pumpModel = String.valueOf(alarmDomainCatalogEnsure.equipmentModelId("水泵"));
        String hatchModel = String.valueOf(alarmDomainCatalogEnsure.equipmentModelId("液压井盖"));

        Map<String, Object> stopped = new LinkedHashMap<>();
        stopped.put(models, List.of(pumpModel));
        stopped.put(condition, List.of(alarmDomainCatalogEnsure.publishedCondition("水泵", "运行", "0")));
        stopped.put(match, AlarmDomainCatalogEnsure.MATCH_ANY);
        stopped.put(execute, "SKIP");
        stopped.put(rules, refs("rule-pump-level-low"));
        file("st-pump-stopped", "水泵没有运行时不执行水位低", modelId, categoryId, deviceCategoryId, stationId, stopped);

        Map<String, Object> pumpHold = new LinkedHashMap<>();
        pumpHold.put(models, List.of(pumpModel));
        pumpHold.put(condition, List.of(openCondition("rule-pump-fault")));
        pumpHold.put(match, AlarmDomainCatalogEnsure.MATCH_ANY);
        pumpHold.put(execute, "SKIP");
        pumpHold.put(rules, refs("rule-pump-level-low", "rule-pump-level-high", "rule-pump-level-hh"));
        file("st-pump-fault-hold", "水泵故障还开着时不执行水位", modelId, categoryId, deviceCategoryId, stationId, pumpHold);

        Map<String, Object> hatchHold = new LinkedHashMap<>();
        hatchHold.put(models, List.of(hatchModel));
        hatchHold.put(condition, List.of(openCondition("rule-hatch-estop")));
        hatchHold.put(match, AlarmDomainCatalogEnsure.MATCH_ANY);
        hatchHold.put(execute, "SKIP");
        hatchHold.put(rules, refs("rule-hatch-fault"));
        file("st-hatch-estop-hold", "液压井盖急停还开着时不执行故障", modelId, categoryId, deviceCategoryId, stationId, hatchHold);
    }

    private void file(String code, String name, Long modelId, Long categoryId, Long deviceCategoryId,
                      Long stationId, Map<String, Object> fields) {
        EntityRespDTO existing = entityRpcFacadeService.getByCode(code, AlarmStrategyCatalogEnsure.DIRECTORY);
        if (existing != null && existing.getId() != null) {
            Object savedModels = existing.getCustomFields() == null
                    ? null : existing.getCustomFields().get(alarmDomainCatalogEnsure.modelsFieldCode());
            Object savedCondition = existing.getCustomFields() == null
                    ? null : existing.getCustomFields().get(alarmDomainCatalogEnsure.conditionFieldCode());
            if (savedModels instanceof List<?> && sameValues(savedCondition, fields.get(alarmDomainCatalogEnsure.conditionFieldCode()))) {
                entityCategoryRelationService.associate(existing.getId(), categoryId, AlarmStrategyCatalogEnsure.DIRECTORY);
                entityCategoryRelationService.associate(existing.getId(), deviceCategoryId, AlarmStrategyCatalogEnsure.DIRECTORY);
                return;
            }
            EntityDeleteReqVO delete = new EntityDeleteReqVO();
            delete.setId(existing.getId());
            delete.setEntityTypeCode(AlarmStrategyCatalogEnsure.DIRECTORY);
            delete.setRegistryCode(AlarmStrategyCatalogEnsure.DIRECTORY);
            delete.setForceDelete(true);
            entityService.delete(delete);
        }
        EntityCreateReqVO request = new EntityCreateReqVO();
        request.setRegistryCode(AlarmStrategyCatalogEnsure.DIRECTORY);
        Map<String, Object> baseFields = new LinkedHashMap<>();
        baseFields.put("entityTypeCode", AlarmStrategyCatalogEnsure.DIRECTORY);
        baseFields.put("modelId", modelId);
        baseFields.put("name", name);
        baseFields.put("code", code);
        baseFields.put("status", 1);
        baseFields.put(FacilityOwningFieldCodes.FIELD_CODE, FacilityOwningFieldCodes.toApiRef(stationId));
        request.setBaseFields(baseFields);
        request.setCustomFields(fields);
        Long entityId = entityService.create(request);
        entityCategoryRelationService.associate(entityId, categoryId, AlarmStrategyCatalogEnsure.DIRECTORY);
        entityCategoryRelationService.associate(entityId, deviceCategoryId, AlarmStrategyCatalogEnsure.DIRECTORY);
    }

    private static boolean sameValues(Object saved, Object wanted) {
        return textValues(saved).equals(textValues(wanted));
    }

    private static List<String> textValues(Object raw) {
        if (!(raw instanceof List<?> list)) {
            return List.of();
        }
        List<String> values = new ArrayList<>();
        for (Object item : list) {
            if (item != null) {
                values.add(String.valueOf(item));
            }
        }
        return values;
    }

    private String openCondition(String ruleCode) {
        Long id = alarmRuleCatalogEnsure.entityIdByCode(ruleCode);
        if (id == null) {
            throw new ServiceException(500, "告警规则「" + ruleCode + "」还没有，策略不能用它");
        }
        return "open:" + id;
    }

    private List<Map<String, Object>> refs(String... ruleCodes) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String ruleCode : ruleCodes) {
            list.add(oneRef(ruleCode));
        }
        return list;
    }

    private Map<String, Object> oneRef(String ruleCode) {
        Long id = alarmRuleCatalogEnsure.entityIdByCode(ruleCode);
        if (id == null) {
            throw new ServiceException(500, "告警规则「" + ruleCode + "」还没有，策略不能用它");
        }
        Map<String, Object> ref = new LinkedHashMap<>();
        ref.put("entityTypeCode", AlarmRuleCatalogEnsure.DIRECTORY);
        ref.put("id", id);
        return ref;
    }

    private Long modelId(String name) {
        ModelDO model = modelMapper.selectByNameAndEntityTypeCode(name, AlarmStrategyCatalogEnsure.DIRECTORY);
        if (model == null || model.getId() == null) {
            throw new ServiceException(500, "策略目录还没有模型「" + name + "」");
        }
        return model.getId();
    }

    private static String escape(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
