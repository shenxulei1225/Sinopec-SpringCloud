package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames;
import cn.cheers.x.module.dynamicbusiness.service.strategy.ConditionStrategyService;
import cn.cheers.x.module.dynamicbusiness.service.strategy.PointProcessingRule;
import cn.cheers.x.module.dynamicbusiness.service.strategy.StrategyRuleVersion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 告警判断读告警策略目录里的记录。
 * 条件策略里剩下的是刷新设备状态，不带设备名单。
 * 设备型号从设备账上读，用来对照策略上的设备类型。
 * 不处理这批读数。
 */
@Component
public class StrategyPointRuleSource implements PointRuleSource {

    private static final String EQUIPMENT = "equipment";

    private final StrategyRuleVersion version;
    private final ConditionStrategyService strategies;
    private final AlarmStrategyRuleSource alarmStrategies;
    private final JdbcTemplate jdbcTemplate;

    public StrategyPointRuleSource(StrategyRuleVersion version, ConditionStrategyService strategies,
                                   AlarmStrategyRuleSource alarmStrategies, JdbcTemplate jdbcTemplate) {
        this.version = version;
        this.strategies = strategies;
        this.alarmStrategies = alarmStrategies;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long version() {
        return version.current();
    }

    @Override
    public List<PointProcessingRule> load() {
        List<PointProcessingRule> rules = new ArrayList<>();
        for (PointProcessingRule rule : strategies.listPointRules()) {
            if (PointProcessingRule.THRESHOLD.equals(rule.getSignalKind())
                    || PointProcessingRule.ALARM_FLAG.equals(rule.getSignalKind())) {
                continue;
            }
            rules.add(rule);
        }
        rules.addAll(alarmStrategies.load());
        return rules;
    }

    @Override
    public Map<Long, Set<Long>> boundDevices() {
        return alarmStrategies.boundDevices();
    }

    /**
     * 当前租户设备账上的型号。策略按设备类型判断时用这份对照。
     * 设备表还没有时返回空，判断侧看不到型号就不会套告警策略。
     */
    @Override
    public Map<Long, Long> deviceModels() {
        String table = TenantPhysicalTableNames.entityPhysicalTable(EQUIPMENT);
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'dynamicbusiness' AND table_name = ?",
                Integer.class, table);
        if (count == null || count == 0) {
            return Map.of();
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, model_id FROM dynamicbusiness." + table
                        + " WHERE deleted = false AND model_id IS NOT NULL");
        Map<Long, Long> models = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Object id = row.get("id");
            Object modelId = row.get("model_id");
            if (id instanceof Number && modelId instanceof Number) {
                models.put(((Number) id).longValue(), ((Number) modelId).longValue());
            }
        }
        return models;
    }
}
