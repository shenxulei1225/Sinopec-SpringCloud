package cn.cheers.x.module.dynamicbusiness.service.strategy;

import cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo.ConditionStrategyRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo.ConditionStrategySaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.strategy.ConditionStrategyDO;

import java.util.List;
import java.util.Map;

/**
 * 条件策略的查看与增改。
 * <p>不负责执行动作；运行时只读已启用行。
 */
public interface ConditionStrategyService {

    List<ConditionStrategyRespVO> listVisible();

    List<ConditionStrategyDO> listVisibleEnabled(String eventType);

    Long save(ConditionStrategySaveReqVO req);

    void delete(Long id);

    Map<String, String> actionCatalog();
}
