package cn.cheers.x.module.dynamicbusiness.service.strategy;

import cn.cheers.x.strategy.StrategyDecision;
import cn.cheers.x.strategy.StrategyDecider;
import cn.cheers.x.strategy.StrategyFact;
import cn.cheers.x.strategy.StrategyPublication;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 给调用方做一次策略判定。
 *
 * 管什么：读动态业务里已经发布的域和策略，交给判定模块，记下这次的事实和结果。
 * 不管什么：不执行动作，不改策略记录。
 * 禁止：在这里按业务解释动作做法。
 */
@Service
@Slf4j
public class StrategyDecisionAppService {

    @Resource
    private StrategyPublicationReader publicationReader;

    public StrategyDecision decide(String domainCode, List<String> presentConditionIds) {
        StrategyPublication publication = publicationReader.load(domainCode);
        Set<String> present = new LinkedHashSet<>();
        if (presentConditionIds != null) {
            for (String id : presentConditionIds) {
                if (id != null && !id.isBlank()) {
                    present.add(id.trim());
                }
            }
        }
        StrategyDecision decision = StrategyDecider.decide(publication, new StrategyFact(present));
        log.info("策略判定 domain={} present={} matched={} skipped={}",
                domainCode, present.size(), decision.matched().size(), decision.skipped().size());
        return decision;
    }
}
