package cn.cheers.x.module.dynamicbusiness.dataprocess;

import java.util.List;

/**
 * 数据处理读取告警策略。
 * 告警策略子类型里，条件成立且选了不执行时，选中的规则不新开。
 * 运行状态和按设计抑制是已停用的旧型号。没有配置时列表为空，判断仍按告警规则。
 */
public interface AlarmBehaviorLookup {

    List<AlarmStateSetting> states();

    List<AlarmSuppression> suppressions();

    /** 告警策略子类型里选了不执行的记录。没有时返回空，规则照常判断。 */
    default List<AlarmPolicy> policies() {
        return List.of();
    }
}
