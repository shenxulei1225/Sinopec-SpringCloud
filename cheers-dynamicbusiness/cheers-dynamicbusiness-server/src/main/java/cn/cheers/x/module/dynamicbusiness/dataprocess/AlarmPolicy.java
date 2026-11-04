package cn.cheers.x.module.dynamicbusiness.dataprocess;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 设备告警策略里的一条。
 * 适用型号空着表示全部型号。指定了型号，只对这些型号生效。
 * 条件可以有多句。全部满足是这几句同时成立。任一满足是有一句成立即可。
 * 型号上没有这句对应的点时，这条策略对这台设备不用。没读到点时也不当成成立。
 * 没选规则时，这台设备上的规则都不新开。选了几条就只不新开这几条。
 * 不执行只挡住新开。已经开着的仍可关掉。
 */
public record AlarmPolicy(
        List<Clause> clauses,
        boolean allMustMatch,
        Set<Long> modelIds,
        Set<Long> ruleIds,
        boolean allRules) {

    /** 某一型号上要看的点和期望的工程值。布尔量的工程值是 0 或 1。 */
    public record Expect(String pointCode, double expected) {
    }

    /**
     * STATE 的 byModel 写每个型号对应的点。型号不在里面，表示这个型号没有这句。
     * OPEN 看同一台设备上某条告警还开着。
     */
    public record Clause(String kind, Map<Long, Expect> byModel, Long leadRuleId) {
    }

    /**
     * 一条状态条件。编号形如 state:型号:点位:0。
     * 规则集合为空表示这台设备上的规则都不新开。
     */
    public static AlarmPolicy parse(String code, Set<Long> ruleIds) {
        if (code == null) {
            return null;
        }
        boolean all = ruleIds == null || ruleIds.isEmpty();
        Set<Long> ids = all ? Set.of() : Set.copyOf(ruleIds);
        if (code.startsWith("state:")) {
            Map<Long, Expect> byModel = AlarmConditionCatalog.resolve(code, List.of());
            if (byModel.isEmpty()) {
                return null;
            }
            return new AlarmPolicy(List.of(new Clause("STATE", byModel, null)), false, Set.of(), ids, all);
        }
        if (code.startsWith("open:")) {
            long lead = Long.parseLong(code.substring("open:".length()));
            return new AlarmPolicy(List.of(new Clause("OPEN", Map.of(), lead)), false, Set.of(), ids, all);
        }
        return null;
    }
}
