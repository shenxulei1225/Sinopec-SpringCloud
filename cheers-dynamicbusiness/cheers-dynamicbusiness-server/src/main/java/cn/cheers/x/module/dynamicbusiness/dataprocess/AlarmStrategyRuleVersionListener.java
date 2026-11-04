package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.module.dynamicbusiness.service.computed.precompute.EntityChangeEvent;
import cn.cheers.x.module.dynamicbusiness.service.strategy.StrategyRuleVersion;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 告警策略记录保存并提交之后，让数据处理重新读这本目录。
 * 不写策略，也不在保存事务里读规则。
 */
@Component
public class AlarmStrategyRuleVersionListener {

    @Resource
    private StrategyRuleVersion strategyRuleVersion;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterStrategySaved(EntityChangeEvent event) {
        if (event != null && (AlarmStrategyCatalogEnsure.DIRECTORY.equals(event.getEntityTypeCode())
                || AlarmRuleCatalogEnsure.DIRECTORY.equals(event.getEntityTypeCode()))) {
            strategyRuleVersion.bump();
        }
    }
}
