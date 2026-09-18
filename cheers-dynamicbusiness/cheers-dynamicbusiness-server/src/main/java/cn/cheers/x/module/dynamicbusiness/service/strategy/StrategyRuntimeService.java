package cn.cheers.x.module.dynamicbusiness.service.strategy;

import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyHandleRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyItemDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;

import java.util.List;

/**
 * 条件策略运行时：匹配可见策略并调用已登记动作。
 * <p>不负责：拆报文、新建执行账；告警只调用已登记入口，不猜设备。
 */
public interface StrategyRuntimeService {

    StrategyHandleRespDTO handle(StrategyTriggerEventDTO event);

    List<StrategyItemDTO> listPublished();
}
