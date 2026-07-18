package cn.cheers.x.alarm.controller.admin;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.alarm.controller.admin.vo.linkage.*;
import cn.cheers.x.alarm.convert.LinkageRuleConvert;
import cn.cheers.x.alarm.dal.dataobject.LinkageExecutionDO;
import cn.cheers.x.alarm.service.linkage.LinkageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 联动执行记录 Controller
 * 
 * <p>提供联动执行记录的查询和手动执行联动功能。</p>
 * 
 * <p>核心功能：
 * <ul>
 *   <li>联动执行记录查询（FR-011）</li>
 *   <li>手动执行联动（FR-011）</li>
 *   <li>重试失败的联动执行（FR-011）</li>
 *   <li>查询需要人工介入的执行记录（FR-011）</li>
 * </ul>
 * </p>
 * 
 * <p>业务规则：
 * <ul>
 *   <li>BR-BIZ-005：联动动作执行失败时，系统应自动重试3次，每次间隔5秒</li>
 *   <li>BR-BIZ-006：联动重试3次后仍失败，系统必须发送通知给值班员，要求人工介入处理</li>
 *   <li>BR-BIZ-007：所有联动动作必须记录执行状态、执行时间、执行结果</li>
 *   <li>BR-PRM-001：联动动作执行需要操作员权限</li>
 * </ul>
 * </p>
 *
 * @author 告警管理模块
 */
@Tag(name = "管理后台 - 联动执行记录")
@RestController
@RequestMapping("/alarm/linkage-executions")
@Validated
public class LinkageExecutionController {

    @Resource
    private LinkageService linkageService;

    // ========== 联动执行记录查询 ==========

    @GetMapping("/get")
    @Operation(summary = "获取联动执行记录详情")
    @Parameter(name = "id", description = "执行记录ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:linkage-execution:query')")
    public CommonResult<LinkageExecutionRespVO> getLinkageExecution(@RequestParam("id") Long id) {
        LinkageExecutionDO execution = linkageService.getLinkageExecution(id);
        return success(LinkageRuleConvert.INSTANCE.convertExecution(execution));
    }

    @GetMapping("/page")
    @Operation(summary = "获取联动执行记录分页列表", 
               description = "支持按告警ID、联动规则ID、动作类型、执行状态、是否需要人工介入等条件筛选")
    @PreAuthorize("@ss.hasPermission('alarm:linkage-execution:query')")
    public CommonResult<PageResult<LinkageExecutionRespVO>> getLinkageExecutionPage(
            @Valid LinkageExecutionPageReqVO pageReqVO) {
        PageResult<LinkageExecutionDO> pageResult = linkageService.getLinkageExecutionPage(pageReqVO);
        return success(LinkageRuleConvert.INSTANCE.convertExecutionPage(pageResult));
    }

    @GetMapping("/list-by-alarm")
    @Operation(summary = "根据告警ID获取联动执行记录列表", description = "获取指定告警的所有联动执行记录")
    @Parameter(name = "alarmId", description = "告警ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:linkage-execution:query')")
    public CommonResult<List<LinkageExecutionRespVO>> getLinkageExecutionsByAlarmId(
            @RequestParam("alarmId") Long alarmId) {
        List<LinkageExecutionDO> list = linkageService.getLinkageExecutionsByAlarmId(alarmId);
        return success(LinkageRuleConvert.INSTANCE.convertExecutionList(list));
    }

    @GetMapping("/manual-intervention")
    @Operation(summary = "获取需要人工介入的联动执行记录", 
               description = "获取所有重试3次后仍失败、需要人工介入处理的联动执行记录")
    @PreAuthorize("@ss.hasPermission('alarm:linkage-execution:query')")
    public CommonResult<List<LinkageExecutionRespVO>> getManualInterventionExecutions() {
        List<LinkageExecutionDO> list = linkageService.getManualInterventionExecutions();
        return success(LinkageRuleConvert.INSTANCE.convertExecutionList(list));
    }

    // ========== 手动执行联动 ==========

    @PostMapping("/manual-execute")
    @Operation(summary = "手动执行联动动作", 
               description = "值班员手动触发联动控制，用于人工介入处理或补充执行联动动作")
    @PreAuthorize("@ss.hasPermission('alarm:linkage-execution:execute')")
    public CommonResult<LinkageExecutionRespVO> manualExecuteLinkage(
            @Valid @RequestBody LinkageManualExecuteReqVO reqVO) {
        LinkageExecutionDO execution = linkageService.manualExecuteLinkage(
                reqVO.getAlarmId(), reqVO.getActionType(), reqVO.getActionConfig());
        return success(LinkageRuleConvert.INSTANCE.convertExecution(execution));
    }

    @PostMapping("/execute-rule")
    @Operation(summary = "执行指定联动规则", 
               description = "针对指定告警执行指定的联动规则，用于手动触发联动")
    @PreAuthorize("@ss.hasPermission('alarm:linkage-execution:execute')")
    public CommonResult<List<LinkageExecutionRespVO>> executeLinkageRule(
            @RequestParam("alarmId") Long alarmId,
            @RequestParam("linkageRuleId") Long linkageRuleId) {
        List<LinkageExecutionDO> executions = linkageService.executeLinkageRule(alarmId, linkageRuleId);
        return success(LinkageRuleConvert.INSTANCE.convertExecutionList(executions));
    }

    @PostMapping("/retry")
    @Operation(summary = "重试失败的联动执行", 
               description = "重试执行失败的联动动作，最多重试3次，3次后仍失败将标记需要人工介入")
    @Parameter(name = "id", description = "执行记录ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:linkage-execution:execute')")
    public CommonResult<LinkageExecutionRespVO> retryLinkageExecution(@RequestParam("id") Long id) {
        LinkageExecutionDO execution = linkageService.retryLinkageExecution(id);
        return success(LinkageRuleConvert.INSTANCE.convertExecution(execution));
    }

}
