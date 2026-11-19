package cn.cheers.x.module.dynamicbusiness.controller.admin.ledgerstate;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.ledgerstate.vo.LedgerStateExecuteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.ledgerstate.vo.LedgerTransitionRespVO;
import cn.cheers.x.module.dynamicbusiness.service.ledgerstate.LedgerStateMachineService;
import cn.cheers.x.module.dynamicbusiness.service.ledgerstate.LedgerTransitionView;
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
 * 台账办事状态的查询与转移。
 *
 * 管什么：按已发布定义列出当前页按钮，执行一跳，读已写下的脉络。
 * 不管什么：角色勾选本身、作业标准步骤、审批设计器。
 * 禁止：在这里写死谁能受理；页面权限仍只认角色授权。
 * 权限：列可做的转移要这一页的查看勾选。执行和补「已上报」由服务按转移配置的页面核对维护勾选。
 * 脉络在我的报修、报修受理、故障台账、工单中心、我的工单都显示，任一页有查看勾选即可读。
 * 工单开工、完工、关闭报修只走报修接口（RepairRecordController），这里不再提供第二条写入。
 */
@Tag(name = "管理后台 - 办事状态")
@RestController
@RequestMapping("/dynamicbusiness/ledger-state")
@Validated
public class LedgerStateMachineController {

    @Resource
    private LedgerStateMachineService ledgerStateMachineService;

    @GetMapping("/available")
    @Operation(summary = "当前页在这张单上能做的转移")
    @PreAuthorize("@pageAccess.allow(#pageCode, 'view')")
    public CommonResult<List<LedgerTransitionRespVO>> available(
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("entityId") Long entityId,
            @RequestParam("pageCode") String pageCode) {
        List<LedgerTransitionRespVO> list = new ArrayList<>();
        for (LedgerTransitionView view : ledgerStateMachineService.listAvailable(entityTypeCode, entityId, pageCode)) {
            LedgerTransitionRespVO item = new LedgerTransitionRespVO();
            item.setToState(view.toState());
            item.setToStateName(view.toStateName());
            item.setRequiredFields(view.requiredFields());
            item.setTraceTemplate(view.traceTemplate());
            list.add(item);
        }
        return success(list);
    }

    @PostMapping("/execute")
    @Operation(summary = "执行一条页面上的转移")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<Boolean> execute(@Valid @RequestBody LedgerStateExecuteReqVO reqVO) {
        ledgerStateMachineService.execute(reqVO.getEntityTypeCode(), reqVO.getEntityId(),
                reqVO.getToState(), reqVO.getPageCode(), reqVO.getPayload());
        return success(true);
    }

    @PostMapping("/record-submitted")
    @Operation(summary = "新建后补「已上报」脉络")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<Boolean> recordSubmitted(@Valid @RequestBody LedgerStateExecuteReqVO reqVO) {
        ledgerStateMachineService.recordSubmitted(reqVO.getEntityTypeCode(), reqVO.getEntityId());
        return success(true);
    }

    @GetMapping("/trace")
    @Operation(summary = "已发生的脉络行")
    @PreAuthorize("@pageAccess.allowAny('view', 'repair-request', 'repair-accept', 'fault-ledger', "
            + "'work-order-center', 'work-order-execute')")
    public CommonResult<List<String>> trace(
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("entityId") Long entityId) {
        return success(ledgerStateMachineService.listTraceLines(entityTypeCode, entityId));
    }
}
