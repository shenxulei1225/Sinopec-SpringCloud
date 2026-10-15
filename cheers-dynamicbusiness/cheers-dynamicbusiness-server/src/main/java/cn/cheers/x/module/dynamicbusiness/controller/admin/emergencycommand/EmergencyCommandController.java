package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandAssessReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandCompleteStepReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandDetailRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandEventRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandFromAlertReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandStartReqVO;
import cn.cheers.x.module.dynamicbusiness.service.emergencycommand.EmergencyCommandFields;
import cn.cheers.x.module.dynamicbusiness.service.emergencycommand.EmergencyCommandService;
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

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 应急指挥入口。
 *
 * 管什么：告警转入、研判、启动响应、查看并完成响应步骤。
 * 不管什么：预案怎么编写。预案在标准规范的应急预案目录里维护。
 * 禁止：改走旧应急服务的确认和复制预警步骤。
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

    @PostMapping("/from-alert")
    @Operation(summary = "告警转入应急事件")
    @PreAuthorize("@catalogAccess.allow('" + EmergencyCommandFields.EVENT_CATALOG + "', 'create')")
    public CommonResult<String> openFromAlert(@Valid @RequestBody EmergencyCommandFromAlertReqVO reqVO) {
        return success(emergencyCommandService.openFromAlert(reqVO));
    }

    @PostMapping("/assess")
    @Operation(summary = "研判应急事件")
    @PreAuthorize("@catalogAccess.allow('" + EmergencyCommandFields.EVENT_CATALOG + "', 'update')")
    public CommonResult<Boolean> assess(@Valid @RequestBody EmergencyCommandAssessReqVO reqVO) {
        emergencyCommandService.assess(reqVO);
        return success(true);
    }

    @PostMapping("/start")
    @Operation(summary = "按研判结果启动响应")
    @PreAuthorize("@catalogAccess.allow('" + EmergencyCommandFields.RESPONSE_CATALOG + "', 'create')")
    public CommonResult<String> start(@Valid @RequestBody EmergencyCommandStartReqVO reqVO) {
        return success(emergencyCommandService.start(reqVO));
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
}
