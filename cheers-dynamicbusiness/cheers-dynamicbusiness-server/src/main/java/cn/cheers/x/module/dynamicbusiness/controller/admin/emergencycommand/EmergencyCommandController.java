package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandAssessReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandChangeLevelReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandCloseReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandCompleteStepReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandDetailRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandEventRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandFromAlertReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandIntakeReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandJudgementReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandPlanRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandStartReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandSubmitReportReqVO;
import cn.cheers.x.module.dynamicbusiness.service.emergencycommand.EmergencyCommandFields;
import cn.cheers.x.module.dynamicbusiness.service.emergencycommand.EmergencyCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 应急指挥入口。
 *
 * 管什么：研判选定预案、按事件上的预案启动、完成步骤、上报、改变级别、结案。
 * 不管什么：预案怎么编写。预案在应急预案目录里维护。
 * 禁止：启动时复制步骤，或按事件分类另找一份预案。
 */
@Tag(name = "管理后台 - 应急指挥")
@RestController
@RequestMapping("/dynamicbusiness/emergency-command")
@Validated
public class EmergencyCommandController {

    @Resource
    private EmergencyCommandService emergencyCommandService;

    @GetMapping("/events")
    @Operation(summary = "应急事件列表")
    @PreAuthorize("@catalogAccess.allow('" + EmergencyCommandFields.EVENT_CATALOG + "', 'query')")
    public CommonResult<PageResult<EmergencyCommandEventRespVO>> pageEvents(
            @RequestParam(value = "eventStatus", required = false) String eventStatus,
            @RequestParam(value = "eventType", required = false) String eventType,
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return success(emergencyCommandService.pageEvents(eventStatus, eventType, pageNo, pageSize));
    }

    @GetMapping("/event")
    @Operation(summary = "应急事件详情")
    @PreAuthorize("@catalogAccess.allow('" + EmergencyCommandFields.EVENT_CATALOG + "', 'query')")
    public CommonResult<EmergencyCommandEventRespVO> getEvent(@RequestParam("eventId") String eventId) {
        return success(emergencyCommandService.getEvent(eventId));
    }

    @PostMapping("/intake")
    @Operation(summary = "人工接报，新建待研判事件")
    @PreAuthorize("@catalogAccess.allow('" + EmergencyCommandFields.EVENT_CATALOG + "', 'create')")
    public CommonResult<String> intake(@Valid @RequestBody EmergencyCommandIntakeReqVO reqVO) {
        return success(emergencyCommandService.intake(reqVO));
    }

    @PostMapping("/from-alert")
    @Operation(summary = "告警转入应急事件")
    @PreAuthorize("@catalogAccess.allow('" + EmergencyCommandFields.EVENT_CATALOG + "', 'create')")
    public CommonResult<String> openFromAlert(@Valid @RequestBody EmergencyCommandFromAlertReqVO reqVO) {
        return success(emergencyCommandService.openFromAlert(reqVO));
    }

    @PostMapping("/confirm-judgement")
    @Operation(summary = "确认研判。待研判时写入预案和级别；已确认的须在弹框里再次确认才改")
    @PreAuthorize("@catalogAccess.allow('" + EmergencyCommandFields.EVENT_CATALOG + "', 'update')")
    public CommonResult<Boolean> confirmJudgement(@Valid @RequestBody EmergencyCommandJudgementReqVO reqVO) {
        emergencyCommandService.confirmJudgement(reqVO);
        return success(true);
    }

    @PostMapping("/assess")
    @Operation(summary = "研判应急事件")
    @PreAuthorize("@catalogAccess.allow('" + EmergencyCommandFields.EVENT_CATALOG + "', 'update')")
    public CommonResult<Boolean> assess(@Valid @RequestBody EmergencyCommandAssessReqVO reqVO) {
        emergencyCommandService.assess(reqVO);
        return success(true);
    }

    @GetMapping("/plans")
    @Operation(summary = "研判可选的生效预案")
    @PreAuthorize("@catalogAccess.allow('" + EmergencyCommandFields.EVENT_CATALOG + "', 'query')")
    public CommonResult<List<EmergencyCommandPlanRespVO>> plans() {
        return success(emergencyCommandService.listPlans());
    }

    @PostMapping("/start")
    @Operation(summary = "按事件上已选定的预案启动响应")
    @PreAuthorize("@catalogAccess.allow('" + EmergencyCommandFields.RESPONSE_CATALOG + "', 'create')")
    public CommonResult<String> start(@Valid @RequestBody EmergencyCommandStartReqVO reqVO) {
        return success(emergencyCommandService.start(reqVO));
    }

    @PostMapping("/change-level")
    @Operation(summary = "改变当前响应级别，原级别的完成和上报仍保留")
    @PreAuthorize("@catalogAccess.allow('" + EmergencyCommandFields.RESPONSE_CATALOG + "', 'update')")
    public CommonResult<Boolean> changeLevel(@Valid @RequestBody EmergencyCommandChangeLevelReqVO reqVO) {
        emergencyCommandService.changeLevel(reqVO);
        return success(true);
    }

    @PostMapping("/submit-report")
    @Operation(summary = "记下某一步的信息上报，级别可以是已经离开的级别")
    @PreAuthorize("@catalogAccess.allow('" + EmergencyCommandFields.RESPONSE_CATALOG + "', 'update')")
    public CommonResult<Boolean> submitReport(@Valid @RequestBody EmergencyCommandSubmitReportReqVO reqVO) {
        emergencyCommandService.submitReport(reqVO);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "指挥页：事件和已启动的响应步骤")
    @PreAuthorize("@catalogAccess.allow('" + EmergencyCommandFields.RESPONSE_CATALOG + "', 'query')")
    public CommonResult<EmergencyCommandDetailRespVO> detail(
            @RequestParam("eventId") String eventId,
            @RequestParam("facilityId") String facilityId) {
        return success(emergencyCommandService.detail(eventId, facilityId));
    }

    @PostMapping("/start-step")
    @Operation(summary = "开始执行响应上的一步")
    @PreAuthorize("@catalogAccess.allow('" + EmergencyCommandFields.RESPONSE_CATALOG + "', 'update')")
    public CommonResult<Boolean> startStep(@Valid @RequestBody EmergencyCommandCompleteStepReqVO reqVO) {
        emergencyCommandService.startStep(reqVO);
        return success(true);
    }

    @PostMapping("/complete-step")
    @Operation(summary = "完成响应上的一步")
    @PreAuthorize("@catalogAccess.allow('" + EmergencyCommandFields.RESPONSE_CATALOG + "', 'update')")
    public CommonResult<Boolean> completeStep(@Valid @RequestBody EmergencyCommandCompleteStepReqVO reqVO) {
        emergencyCommandService.completeStep(reqVO);
        return success(true);
    }

    @PostMapping("/close")
    @Operation(summary = "结案。后期处置之后把这起事件标成已结案")
    @PreAuthorize("@catalogAccess.allow('" + EmergencyCommandFields.EVENT_CATALOG + "', 'update')")
    public CommonResult<Boolean> close(@Valid @RequestBody EmergencyCommandCloseReqVO reqVO) {
        emergencyCommandService.closeCase(reqVO);
        return success(true);
    }
}
