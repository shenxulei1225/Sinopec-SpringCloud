package cn.cheers.x.module.dynamicbusiness.api.strategy;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyHandleRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyItemDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;
import cn.cheers.x.module.dynamicbusiness.service.strategy.StrategyRuntimeService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 条件策略运行时对外入口。
 * <p>不负责拆报文、不负责巡检步骤完成判定。
 */
@RestController
@Validated
public class StrategyRuntimeApiImpl implements StrategyRuntimeApi {

    @Resource
    private StrategyRuntimeService strategyRuntimeService;

    @Override
    public CommonResult<StrategyHandleRespDTO> handle(StrategyTriggerEventDTO event) {
        return success(strategyRuntimeService.handle(event));
    }

    @Override
    public CommonResult<List<StrategyItemDTO>> listPublished() {
        return success(strategyRuntimeService.listPublished());
    }
}
