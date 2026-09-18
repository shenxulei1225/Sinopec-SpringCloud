package cn.cheers.x.module.dynamicbusiness.controller.admin.strategy;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo.ConditionStrategyRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo.ConditionStrategySaveReqVO;
import cn.cheers.x.module.dynamicbusiness.service.strategy.ConditionStrategyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 给人看、可添加的条件策略。
 * <p>不负责拆报文，不执行策略。
 */
@Tag(name = "管理后台 - 条件策略")
@RestController
@RequestMapping("/dynamicbusiness/condition-strategy")
@Validated
public class ConditionStrategyController {

    @Resource
    private ConditionStrategyService conditionStrategyService;

    @GetMapping("/list")
    @Operation(summary = "列出当前能看见的策略")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<ConditionStrategyRespVO>> list() {
        return success(conditionStrategyService.listVisible());
    }

    @GetMapping("/actions")
    @Operation(summary = "已登记、可勾选的动作")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<Map<String, String>> actions() {
        return success(conditionStrategyService.actionCatalog());
    }

    @PostMapping("/create")
    @Operation(summary = "新增一条策略")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Long> create(@Valid @RequestBody ConditionStrategySaveReqVO req) {
        req.setId(null);
        return success(conditionStrategyService.save(req));
    }

    @PutMapping("/update")
    @Operation(summary = "改一条自己建的策略")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody ConditionStrategySaveReqVO req) {
        conditionStrategyService.save(req);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删一条自己建的策略")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        conditionStrategyService.delete(id);
        return success(true);
    }
}
