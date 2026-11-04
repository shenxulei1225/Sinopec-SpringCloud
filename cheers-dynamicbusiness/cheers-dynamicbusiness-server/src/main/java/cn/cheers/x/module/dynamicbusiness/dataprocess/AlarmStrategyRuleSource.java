package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.service.strategy.PointProcessingRule;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 从告警规则目录里读阈值告警模型上已启用的规则。
 * 判断按规则上的型号。同一种型号的设备共用这些规则。
 * 策略不再决定用哪几条规则。运行状态和抑制另读。
 * 缺限值、型号对不上的规则不参加判断。
 * 不写规则，也不比较读数。
 */
@Service
public class AlarmStrategyRuleSource {

    private static final Logger log = LoggerFactory.getLogger(AlarmStrategyRuleSource.class);

    @Resource
    private EntityRepository entityRepository;
    @Resource
    private EntityRelationMapper entityRelationMapper;
    @Resource
    private AlarmStrategyCatalogEnsure catalog;
    @Resource
    private AlarmRuleCatalogEnsure ruleCatalog;
    @Resource
    private AlarmCatalogEnsure alarmCatalog;
    @Resource
    private EntityCategoryRelationService entityCategoryRelationService;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private FieldMapper fieldMapper;

    public List<PointProcessingRule> load() {
        ModelDO threshold = modelMapper.selectByNameAndEntityTypeCode(
                AlarmRuleCatalogEnsure.MODEL_THRESHOLD, AlarmRuleCatalogEnsure.DIRECTORY);
        if (threshold == null || threshold.getId() == null) {
            return List.of();
        }
        List<Long> ids = new ArrayList<>();
        for (EntityDO listed : entityRepository.findByModelId(threshold.getId(), AlarmRuleCatalogEnsure.DIRECTORY)) {
            if (listed.getId() != null && Integer.valueOf(1).equals(listed.getStatus())) {
                ids.add(listed.getId());
            }
        }
        if (ids.isEmpty()) {
            return List.of();
        }
        String pointCode = ruleCatalog.fieldCode(AlarmRuleCatalogEnsure.FIELD_POINT);
        String opCode = ruleCatalog.fieldCode(AlarmRuleCatalogEnsure.FIELD_OP);
        String valueCode = ruleCatalog.fieldCode(AlarmRuleCatalogEnsure.FIELD_VALUE);
        String typeCode = ruleCatalog.fieldCode(AlarmRuleCatalogEnsure.FIELD_TYPE);
        String levelCode = ruleCatalog.fieldCode(AlarmTypeCatalog.FIELD_LEVEL);
        String ruleDeviceType = ruleCatalog.fieldCode(AlarmRuleCatalogEnsure.FIELD_DEVICE_TYPE);
        String highCode = ruleCatalog.fieldCode(AlarmRuleCatalogEnsure.FIELD_HIGH);
        String lowCode = ruleCatalog.fieldCode(AlarmRuleCatalogEnsure.FIELD_LOW);
        String highRecoverCode = ruleCatalog.fieldCode(AlarmRuleCatalogEnsure.FIELD_HIGH_RECOVER);
        String lowRecoverCode = ruleCatalog.fieldCode(AlarmRuleCatalogEnsure.FIELD_LOW_RECOVER);
        List<PointProcessingRule> rules = new ArrayList<>();
        for (EntityDO rule : entityRepository.findByIdsWithDedicatedBaseFields(ids, AlarmRuleCatalogEnsure.DIRECTORY)) {
            if (rule.getId() == null || !Integer.valueOf(1).equals(rule.getStatus())) {
                continue;
            }
            if (!AlarmRuleCatalogEnsure.MODEL_THRESHOLD.equals(modelName(rule.getModelId()))) {
                continue;
            }
            rules.addAll(toRules(rule, pointCode, opCode, valueCode, typeCode, levelCode, ruleDeviceType,
                    highCode, lowCode, highRecoverCode, lowRecoverCode));
        }
        return rules;
    }

    private String modelName(Long modelId) {
        if (modelId == null) {
            return "";
        }
        ModelDO model = modelMapper.selectById(modelId);
        return model == null || model.getName() == null ? "" : model.getName();
    }

    public Map<Long, Set<Long>> boundDevices() {
        List<EntityDO> rows = loadRows();
        if (rows.isEmpty()) {
            return Map.of();
        }
        String deviceField = catalog.fieldCode(AlarmStrategyCatalogEnsure.FIELD_DEVICES);
        List<Long> ids = rows.stream().map(EntityDO::getId).filter(id -> id != null).toList();
        List<EntityRelationDO> links = entityRelationMapper.selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                .in(EntityRelationDO::getSourceEntityId, ids)
                .eq(EntityRelationDO::getFieldCode, deviceField)
                .eq(EntityRelationDO::getDeleted, false));
        Map<Long, Set<Long>> bound = new HashMap<>();
        for (Long id : ids) {
            bound.put(id, new HashSet<>());
        }
        for (EntityRelationDO link : links) {
            if (link.getSourceEntityId() == null || link.getTargetEntityId() == null) {
                continue;
            }
            bound.computeIfAbsent(link.getSourceEntityId(), key -> new HashSet<>()).add(link.getTargetEntityId());
        }
        return bound;
    }

    private List<EntityDO> loadRows() {
        Long categoryId = catalog.alarmCategoryId();
        if (categoryId == null) {
            return List.of();
        }
        List<Long> ids = entityCategoryRelationService.listEntityIdsByCategoryIdOnly(
                categoryId, AlarmStrategyCatalogEnsure.DIRECTORY);
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<EntityDO> rows = entityRepository.findByIdsWithDedicatedBaseFields(
                ids, AlarmStrategyCatalogEnsure.DIRECTORY);
        List<EntityDO> enabled = new ArrayList<>();
        for (EntityDO row : rows) {
            if (row.getId() != null && Integer.valueOf(1).equals(row.getStatus())) {
                enabled.add(row);
            }
        }
        return enabled;
    }

    /**
     * 写了高于或低于时，各出一条数值规则，并带上对应的恢复线。
     * 没写这两列时，仍用比较方式和比较值。开关量等于 1 仍是报警标识。
     */
    private List<PointProcessingRule> toRules(EntityDO row, String pointCode, String opCode, String valueCode,
                                              String typeCode, String levelCode, String deviceTypeCode,
                                              String highCode, String lowCode, String highRecoverCode,
                                              String lowRecoverCode) {
        String point = text(row, pointCode);
        String type = text(row, typeCode);
        String level = text(row, levelCode);
        String deviceType = text(row, deviceTypeCode);
        if (!StringUtils.hasText(point) || !StringUtils.hasText(type) || !StringUtils.hasText(level)
                || !StringUtils.hasText(deviceType)) {
            log.warn("告警策略 {} 缺了点位、类型、级别或型号，不参加判断", row.getId());
            return List.of();
        }
        ModelDO model = modelMapper.selectByNameAndEntityTypeCode(deviceType, "equipment");
        if (model == null || model.getId() == null) {
            log.warn("告警策略 {} 的型号「{}」对不上，不参加判断", row.getId(), deviceType);
            return List.of();
        }
        Long alarmTypeId;
        try {
            alarmTypeId = alarmCatalog.categoryId(type.trim());
        } catch (RuntimeException ex) {
            log.warn("告警策略 {} 的告警类型对不上告警目录：{}", row.getId(), ex.getMessage());
            return List.of();
        }
        String high = text(row, highCode);
        String low = text(row, lowCode);
        if (StringUtils.hasText(high) || StringUtils.hasText(low)) {
            List<PointProcessingRule> rules = new ArrayList<>();
            addBand(rules, row, point.trim(), alarmTypeId, level.trim(), model.getId(),
                    "GT", high, text(row, highRecoverCode), row.getId());
            addBand(rules, row, point.trim(), alarmTypeId, level.trim(), model.getId(),
                    "LT", low, text(row, lowRecoverCode), -row.getId());
            return rules;
        }
        String op = text(row, opCode);
        String value = text(row, valueCode);
        if (!StringUtils.hasText(op) || !StringUtils.hasText(value)) {
            log.warn("告警策略 {} 没有高于、低于，也没有比较，不参加判断", row.getId());
            return List.of();
        }
        Double threshold = parse(value);
        if (threshold == null) {
            log.warn("告警策略 {} 的比较值不是数字，不参加判断", row.getId());
            return List.of();
        }
        String kind = "EQ".equals(op.trim()) && threshold == 1d
                ? PointProcessingRule.ALARM_FLAG
                : PointProcessingRule.THRESHOLD;
        return List.of(new PointProcessingRule(row.getId(), point.trim(), kind, op.trim(), threshold,
                alarmTypeId, level.trim(), null, null, model.getId(), null, row.getId(),
                row.getName(), fieldName(point.trim())));
    }

    private void addBand(List<PointProcessingRule> rules, EntityDO row, String point, Long alarmTypeId, String level,
                         Long modelId, String op, String value, String recoveryText, long signalKey) {
        if (!StringUtils.hasText(value)) {
            return;
        }
        Double threshold = parse(value);
        if (threshold == null) {
            log.warn("告警策略 {} 的{}不是数字，不参加判断", row.getId(), "GT".equals(op) ? "高于" : "低于");
            return;
        }
        Double recovery = StringUtils.hasText(recoveryText) ? parse(recoveryText) : null;
        if (StringUtils.hasText(recoveryText) && recovery == null) {
            log.warn("告警策略 {} 的恢复线不是数字，这一侧不自动消警", row.getId());
        }
        rules.add(new PointProcessingRule(row.getId(), point, PointProcessingRule.THRESHOLD, op, threshold,
                alarmTypeId, level, null, null, modelId, recovery, signalKey,
                row.getName(), fieldName(point)));
    }

    private String fieldName(String code) {
        if (!StringUtils.hasText(code)) {
            return "";
        }
        FieldDO field = fieldMapper.selectByCode(code);
        return field == null || field.getName() == null ? "" : field.getName().trim();
    }

    private static String text(EntityDO row, String fieldCode) {
        Map<String, Object> values = row.getDedicatedBaseFieldValues();
        Object raw = values == null ? null : values.get(fieldCode);
        if (raw == null && row.getCustomFields() != null) {
            raw = row.getCustomFields().get(fieldCode);
        }
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    private static Double parse(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
