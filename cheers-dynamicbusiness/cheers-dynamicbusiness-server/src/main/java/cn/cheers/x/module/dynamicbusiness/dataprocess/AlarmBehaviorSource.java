package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.service.strategy.StrategyPublicationReader;
import cn.cheers.x.strategy.PublishedStrategy;
import cn.cheers.x.strategy.StrategyMatch;
import cn.cheers.x.strategy.StrategyPublication;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 读运行状态、按设计抑制，以及已经发布的告警域。
 * 告警域里选了不执行的策略，条件成立后不新开对应的告警规则。
 * 不管什么：不比较读数，也不写告警。点位和「还开着」仍由调用方判断。
 */
@Service
public class AlarmBehaviorSource implements AlarmBehaviorLookup {

    private static final Logger log = LoggerFactory.getLogger(AlarmBehaviorSource.class);

    @Resource
    private EntityRepository entityRepository;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private AlarmStrategyCatalogEnsure catalog;
    @Resource
    private AlarmBehaviorCatalogEnsure behaviorCatalog;
    @Resource
    private AlarmDomainCatalogEnsure domainCatalog;
    @Resource
    private StrategyPublicationReader publicationReader;

    @Override
    public List<AlarmStateSetting> states() {
        try {
            List<AlarmStateSetting> settings = new ArrayList<>();
        for (EntityDO row : rows(AlarmBehaviorCatalogEnsure.MODEL_STATE)) {
            try {
                AlarmStateSetting setting = toState(row);
                if (setting != null) {
                    settings.add(setting);
                }
            } catch (RuntimeException ex) {
                log.warn("运行状态 {} 读不出来：{}", row.getId(), ex.getMessage());
            }
        }
            return settings;
        } catch (ServiceException ex) {
            log.warn("运行状态还没准备好：{}", ex.getMessage());
            return List.of();
        }
    }

    @Override
    public List<AlarmSuppression> suppressions() {
        List<AlarmSuppression> settings = new ArrayList<>();
        for (EntityDO row : rows(AlarmBehaviorCatalogEnsure.MODEL_SUPPRESS)) {
            AlarmSuppression setting = toSuppression(row);
            if (setting != null) {
                settings.add(setting);
            }
        }
        return settings;
    }

    @Override
    public List<AlarmPolicy> policies() {
        List<AlarmPolicy> policies = new ArrayList<>();
        try {
            StrategyPublication publication = publicationReader.load(AlarmDomainCatalogEnsure.DOMAIN);
            List<AlarmConditionCatalog.PointSentence> points = domainCatalog.loadPointSentences();
            for (PublishedStrategy strategy : publication.strategies()) {
                if (!StrategyRecordFields.ACTION_SKIP.equals(strategy.actionMode())) {
                    continue;
                }
                List<String> tokens = new ArrayList<>();
                Set<String> models = new HashSet<>();
                boolean unscoped = false;
                for (String conditionId : strategy.conditionIds()) {
                    String token = publicationReader.conditionToken(conditionId);
                    if (token == null || token.isBlank()) {
                        continue;
                    }
                    tokens.add(token);
                    String model = scopedModel(token);
                    if (model == null) {
                        unscoped = true;
                    } else {
                        models.add(model);
                    }
                }
                if (tokens.isEmpty()) {
                    log.warn("告警策略 {} 没有条件，不参加", strategy.id());
                    continue;
                }
                Set<Long> ruleIds = new HashSet<>();
                for (String actionId : strategy.actionIds()) {
                    try {
                        ruleIds.add(Long.parseLong(actionId));
                    } catch (NumberFormatException ex) {
                        log.warn("告警策略 {} 的动作 {} 不是告警规则编号", strategy.id(), actionId);
                    }
                }
                String relation = strategy.match() == StrategyMatch.ALL
                        ? AlarmDomainCatalogEnsure.MATCH_ALL : AlarmDomainCatalogEnsure.MATCH_ANY;
                try {
                    AlarmPolicy policy = toPolicy(tokens, relation,
                            unscoped ? List.of() : new ArrayList<>(models), ruleIds, points);
                    if (policy == null) {
                        log.warn("告警策略 {} 的条件对不上，不参加", strategy.id());
                        continue;
                    }
                    policies.add(policy);
                } catch (RuntimeException ex) {
                    log.warn("告警策略 {} 读不出来：{}", strategy.id(), ex.getMessage());
                }
            }
        } catch (ServiceException ex) {
            log.warn("告警域还没有发布：{}", ex.getMessage());
        }
        return policies;
    }

    /** open:型号:规则 和 bit:型号:点:值 把型号写进条件编号。没有型号段就返回空。 */
    private static String scopedModel(String token) {
        if (token.startsWith("open:")) {
            String[] parts = token.split(":");
            return parts.length == 3 ? parts[1] : null;
        }
        if (token.startsWith("bit:")) {
            String[] parts = token.split(":", 4);
            return parts.length == 4 ? parts[1] : null;
        }
        return null;
    }

    private AlarmPolicy toPolicy(List<String> codes, String relation, List<String> modelValues,
                                 Set<Long> ruleIds, List<AlarmConditionCatalog.PointSentence> points) {
        List<AlarmPolicy.Clause> clauses = new ArrayList<>();
        for (String code : codes) {
            if (code.startsWith("open:")) {
                String[] parts = code.split(":");
                clauses.add(new AlarmPolicy.Clause("OPEN", Map.of(), Long.parseLong(parts[parts.length - 1])));
                continue;
            }
            Map<Long, AlarmPolicy.Expect> byModel = AlarmConditionCatalog.resolve(code, points);
            if (byModel.isEmpty()) {
                log.warn("告警策略的条件 {} 对不上，这一句不用", code);
                continue;
            }
            clauses.add(new AlarmPolicy.Clause("STATE", byModel, null));
        }
        if (clauses.isEmpty()) {
            return null;
        }
        Set<Long> modelIds = new HashSet<>();
        for (String value : modelValues) {
            if (!StringUtils.hasText(value) || AlarmDomainCatalogEnsure.MODELS_ALL.equals(value)) {
                modelIds.clear();
                break;
            }
            modelIds.add(Long.parseLong(value.trim()));
        }
        boolean allRules = ruleIds.isEmpty();
        return new AlarmPolicy(clauses, AlarmDomainCatalogEnsure.MATCH_ALL.equals(relation),
                modelIds, allRules ? Set.of() : ruleIds, allRules);
    }

    private List<EntityDO> rows(String modelName) {
        ModelDO model = modelMapper.selectByNameAndEntityTypeCode(modelName, AlarmStrategyCatalogEnsure.DIRECTORY);
        if (model == null || model.getId() == null) {
            return List.of();
        }
        List<EntityDO> listed = entityRepository.findByModelId(model.getId(), AlarmStrategyCatalogEnsure.DIRECTORY);
        List<Long> ids = new ArrayList<>();
        for (EntityDO row : listed) {
            if (row.getId() != null && Integer.valueOf(1).equals(row.getStatus())) {
                ids.add(row.getId());
            }
        }
        if (ids.isEmpty()) {
            return List.of();
        }
        return entityRepository.findByIdsWithDedicatedBaseFields(ids, AlarmStrategyCatalogEnsure.DIRECTORY);
    }

    private AlarmStateSetting toState(EntityDO row) {
        String deviceType = text(row, catalog.fieldCode(AlarmStrategyCatalogEnsure.FIELD_DEVICE_TYPE));
        String point = text(row, behaviorCatalogField(AlarmBehaviorCatalogEnsure.FIELD_STATE_POINT));
        String op = text(row, catalog.fieldCode(AlarmStrategyCatalogEnsure.FIELD_OP));
        String value = text(row, catalog.fieldCode(AlarmStrategyCatalogEnsure.FIELD_VALUE));
        String action = text(row, behaviorCatalogField(AlarmBehaviorCatalogEnsure.FIELD_ACTION));
        Set<Long> rules = refIds(raw(row, catalog.fieldCode(AlarmStrategyCatalogEnsure.FIELD_RULES)));
        if (!StringUtils.hasText(deviceType) || !StringUtils.hasText(point) || !StringUtils.hasText(op)
                || !StringUtils.hasText(value) || !StringUtils.hasText(action) || rules.isEmpty()) {
            log.warn("运行状态 {} 缺型号、状态点、比较或规则，不参加", row.getId());
            return null;
        }
        Double number = parse(value);
        if (number == null) {
            log.warn("运行状态 {} 的比较值不是数字，不参加", row.getId());
            return null;
        }
        ModelDO equipment = modelMapper.selectByNameAndEntityTypeCode(deviceType, "equipment");
        if (equipment == null || equipment.getId() == null) {
            log.warn("运行状态 {} 的型号「{}」对不上，不参加", row.getId(), deviceType);
            return null;
        }
        return new AlarmStateSetting(equipment.getId(), point, op.trim(), number, action.trim(), rules,
                text(row, catalog.fieldCode(AlarmStrategyCatalogEnsure.FIELD_HIGH)),
                text(row, catalog.fieldCode(AlarmStrategyCatalogEnsure.FIELD_LOW)),
                text(row, catalog.fieldCode(AlarmStrategyCatalogEnsure.FIELD_HIGH_RECOVER)),
                text(row, catalog.fieldCode(AlarmStrategyCatalogEnsure.FIELD_LOW_RECOVER)),
                text(row, catalog.fieldCode(AlarmTypeCatalog.FIELD_LEVEL)));
    }

    private AlarmSuppression toSuppression(EntityDO row) {
        Set<Long> lead = refIds(raw(row, behaviorCatalogField(AlarmBehaviorCatalogEnsure.FIELD_LEAD)));
        Set<Long> held = refIds(raw(row, behaviorCatalogField(AlarmBehaviorCatalogEnsure.FIELD_HELD)));
        if (lead.size() != 1 || held.isEmpty()) {
            log.warn("按设计抑制 {} 没有正好一条先成立的规则，或没有要压住的规则，不参加", row.getId());
            return null;
        }
        return new AlarmSuppression(lead.iterator().next(), held);
    }

    private String behaviorCatalogField(String name) {
        return behaviorCatalog.fieldCode(name);
    }

    private static Object raw(EntityDO row, String fieldCode) {
        Map<String, Object> values = row.getDedicatedBaseFieldValues();
        Object value = values == null ? null : values.get(fieldCode);
        if (value == null && row.getCustomFields() != null) {
            value = row.getCustomFields().get(fieldCode);
        }
        return value;
    }

    private static List<String> texts(Object raw) {
        List<String> values = new ArrayList<>();
        collectText(raw, values);
        return values;
    }

    private static void collectText(Object raw, List<String> values) {
        if (raw == null) {
            return;
        }
        if (raw instanceof String text) {
            String trimmed = text.trim();
            if (trimmed.startsWith("[")) {
                collectText(JSON.parseArray(trimmed), values);
                return;
            }
            if (!trimmed.isBlank()) {
                values.add(trimmed);
            }
            return;
        }
        if (raw instanceof List<?> list) {
            for (Object item : list) {
                collectText(item, values);
            }
            return;
        }
        if (raw instanceof Map<?, ?> map && map.get("id") != null) {
            return;
        }
        String text = String.valueOf(raw).trim();
        if (!text.isBlank() && !"null".equals(text)) {
            values.add(text);
        }
    }

    private static String text(EntityDO row, String fieldCode) {
        Object value = raw(row, fieldCode);
        return value == null ? "" : String.valueOf(value).trim();
    }

    private static Set<Long> refIds(Object raw) {
        Set<Long> ids = new HashSet<>();
        collect(raw, ids);
        return ids;
    }

    private static void collect(Object raw, Set<Long> ids) {
        if (raw instanceof Number number) {
            ids.add(number.longValue());
            return;
        }
        if (raw instanceof Map<?, ?> map && map.get("id") != null) {
            ids.add(Long.parseLong(String.valueOf(map.get("id"))));
            return;
        }
        if (raw instanceof List<?> list) {
            for (Object item : list) {
                collect(item, ids);
            }
        }
    }

    private static Double parse(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
