package cn.cheers.x.module.dynamicbusiness.controller.admin.repair;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.repair.vo.RepairAssigneeReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.repair.vo.RepairDispatchReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.repair.vo.RepairRemarkReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.repair.vo.RepairWorkOrderReqVO;
import cn.cheers.x.module.dynamicbusiness.service.repair.RepairRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 报修受理与派工、以及工单完工回写报修单。
 *
 * 管什么：受理人改报修单，执行人完工后关闭报修单。
 * 不管什么：流程定义、待办箱。本接口不启动流程。
 * 禁止：上报页调用受理和派工。
 */
@Tag(name = "管理后台 - 报修单状态")
@RestController
@RequestMapping("/dynamicbusiness/repair-record")
@Validated
public class RepairRecordController {

    @Resource
    private RepairRecordService repairRecordService;

    @PostMapping("/submitted")
    @Operation(summary = "记下已上报")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<Boolean> submitted(@Valid @RequestBody RepairRemarkReqVO reqVO) {
        repairRecordService.recordSubmitted(reqVO.getId());
        return success(true);
    }

    @PostMapping("/accept")
    @Operation(summary = "受理报修")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<Boolean> accept(@Valid @RequestBody RepairRemarkReqVO reqVO) {
        repairRecordService.accept(reqVO.getId());
        return success(true);
    }

    @PostMapping("/reject")
    @Operation(summary = "驳回报修")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<Boolean> reject(@Valid @RequestBody RepairRemarkReqVO reqVO) {
        repairRecordService.reject(reqVO.getId(), reqVO.getRemark());
        return success(true);
    }

    @PostMapping("/need-info")
    @Operation(summary = "受理回退，原因必填")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<Boolean> needInfo(@Valid @RequestBody RepairRemarkReqVO reqVO) {
        repairRecordService.needInfo(reqVO.getId(), reqVO.getRemark());
        return success(true);
    }

    @PostMapping("/resubmit")
    @Operation(summary = "报修人补完材料后交回")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<Boolean> resubmit(@Valid @RequestBody RepairRemarkReqVO reqVO) {
        repairRecordService.resubmit(reqVO.getId());
        return success(true);
    }

    @PostMapping("/dispatch")
    @Operation(summary = "派工并生成工单")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<Boolean> dispatch(@Valid @RequestBody RepairDispatchReqVO reqVO) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("scope", reqVO.getScope());
        payload.put("title", reqVO.getTitle());
        payload.put("workContent", reqVO.getWorkContent());
        payload.put("standardId", reqVO.getStandardId());
        payload.put("teamId", reqVO.getTeamId());
        payload.put("teamName", reqVO.getTeamName());
        payload.put("assigneeUserId", reqVO.getAssigneeUserId());
        payload.put("assigneeName", reqVO.getAssigneeName());
        payload.put("handlingTime", reqVO.getHandlingTime());
        payload.put("assetId", reqVO.getAssetId());
        repairRecordService.dispatch(reqVO.getId(), payload);
        return success(true);
    }

    @PostMapping("/started")
    @Operation(summary = "工单开工，报修单仍为已派工")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<Boolean> started(@Valid @RequestBody RepairWorkOrderReqVO reqVO) {
        repairRecordService.noteStarted(reqVO.getRepairId(), reqVO.getWorkOrderId());
        return success(true);
    }

    @PostMapping("/assignee")
    @Operation(summary = "领走或指定到人后，给报修补一行")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<Boolean> assignee(@Valid @RequestBody RepairAssigneeReqVO reqVO) {
        repairRecordService.noteAssignee(
                reqVO.getRepairId(), reqVO.getWorkOrderId(), reqVO.getPersonName(), Boolean.TRUE.equals(reqVO.getReassign()));
        return success(true);
    }

    @PostMapping("/finished")
    @Operation(summary = "工单完工并关闭报修单")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<Boolean> finished(@Valid @RequestBody RepairWorkOrderReqVO reqVO) {
        repairRecordService.finishFromWorkOrder(reqVO.getRepairId(), reqVO.getWorkOrderId());
        return success(true);
    }
}
