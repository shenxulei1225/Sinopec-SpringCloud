package cn.cheers.x.module.dynamicbusiness.service.strategy;

import cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo.ConditionStrategyRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo.ConditionStrategySaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo.StrategyDeviceOptionVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.strategy.ConditionStrategyDO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 条件策略的查看与增改。
 * <p>不负责执行动作；运行时只读已启用行。
 */
public interface ConditionStrategyService {

    List<ConditionStrategyRespVO> listVisible();

    List<ConditionStrategyDO> listVisibleEnabled(String eventType);

    /** 已启用、并且能用在点位读数上的规则。巡检执行账那几条不在这里。 */
    List<PointProcessingRule> listPointRules();

    /**
     * 策略编号 → 已经挂上的设备编号。
     * 没有这一条，或集合是空的，表示这台设备不套用。不会当成全部设备。
     */
    Map<Long, Set<Long>> boundDeviceIds();

    /** 策略页勾选用。设备台账里的编号和名称。 */
    List<StrategyDeviceOptionVO> listEquipmentOptions();

    Long save(ConditionStrategySaveReqVO req);

    void delete(Long id);

    Map<String, String> actionCatalog();
}
