package cn.cheers.x.module.dynamicbusiness.controller.admin.strategy;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo.StrategyDecideReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo.StrategyDecideRespVO;
import cn.cheers.x.module.dynamicbusiness.service.strategy.StrategyDecisionAppService;
import cn.cheers.x.module.dynamicbusiness.service.strategy.StrategyVocabularyQuery;
import cn.cheers.x.strategy.StrategyDecision;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 策略判定入口。
 *
 * 管什么：收下域和这次的事实，返回成立的策略和勾选的动作。
 * 不管什么：不保存策略，不执行动作。
 */
@Tag(name = "管理后台 - 策略判定")
@RestController
@RequestMapping("/dynamicbusiness/strategy-decision")
@Validated
public class StrategyDecisionController {

    @Resource
    private StrategyDecisionAppService strategyDecisionAppService;
    @Resource
    private StrategyVocabularyQuery strategyVocabularyQuery;

    @GetMapping("/vocabulary")
    @Operation(summary = "列出这个域已经公布、可以勾选的条件和动作")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<StrategyVocabularyQuery.Vocabulary> vocabulary(@RequestParam("entityId") Long entityId) {
        return success(strategyVocabularyQuery.load(entityId));
    }

    @PostMapping("/decide")
    @Operation(summary = "按已发布的策略做一次判定")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<StrategyDecideRespVO> decide(@Valid @RequestBody StrategyDecideReqVO request) {
        StrategyDecision decision = strategyDecisionAppService.decide(
                request.getDomainCode(), request.getPresentConditionIds());
        return success(toResponse(decision));
    }

    private static StrategyDecideRespVO toResponse(StrategyDecision decision) {
        StrategyDecideRespVO response = new StrategyDecideRespVO();
        List<StrategyDecideRespVO.Matched> matched = new ArrayList<>();
        for (StrategyDecision.MatchedStrategy item : decision.matched()) {
            StrategyDecideRespVO.Matched row = new StrategyDecideRespVO.Matched();
            row.setId(item.id());
            row.setName(item.name());
            row.setActionMode(item.actionMode());
            row.setActionIds(item.actionIds());
            matched.add(row);
        }
        List<StrategyDecideRespVO.Skipped> skipped = new ArrayList<>();
        for (StrategyDecision.SkippedStrategy item : decision.skipped()) {
            StrategyDecideRespVO.Skipped row = new StrategyDecideRespVO.Skipped();
            row.setId(item.id());
            row.setName(item.name());
            row.setReason(item.reason());
            skipped.add(row);
        }
        response.setMatched(matched);
        response.setSkipped(skipped);
        return response;
    }
}
