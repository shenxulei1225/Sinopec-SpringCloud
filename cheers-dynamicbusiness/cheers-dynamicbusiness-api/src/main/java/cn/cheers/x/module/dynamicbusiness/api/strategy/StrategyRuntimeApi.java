package cn.cheers.x.module.dynamicbusiness.api.strategy;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyHandleRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyItemDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;
import cn.cheers.x.module.dynamicbusiness.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 条件策略运行时：按已发布策略匹配事件并调用已登记动作。
 * <p>不负责拆报文、不负责实现各业务内部算法。
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 条件策略运行时")
public interface StrategyRuntimeApi {

    String PREFIX = ApiConstants.DYNAMICBUSINESS_PREFIX + "/strategy-runtime";

    @PostMapping(PREFIX + "/handle")
    @Operation(summary = "按策略处理一次事件")
    CommonResult<StrategyHandleRespDTO> handle(@Valid @RequestBody StrategyTriggerEventDTO event);

    @GetMapping(PREFIX + "/list")
    @Operation(summary = "列出当前可见策略")
    CommonResult<List<StrategyItemDTO>> listPublished();
}
