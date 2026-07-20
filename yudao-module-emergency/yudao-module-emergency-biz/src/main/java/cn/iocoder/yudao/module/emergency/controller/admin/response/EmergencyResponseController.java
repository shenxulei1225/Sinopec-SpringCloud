package cn.iocoder.yudao.module.emergency.controller.admin.response;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.emergency.controller.admin.response.vo.*;
import cn.iocoder.yudao.module.emergency.service.response.EmergencyResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/emergency/responses")
@Tag(name = "管理后台 - 应急响应")
public class EmergencyResponseController {

    @Resource
    private EmergencyResponseService responseService;

    @PostMapping
    @Operation(summary = "启动应急响应", description = "根据事件ID和预案ID启动应急响应")
    public CommonResult<ResponseRespVO> start(@Valid @RequestBody ResponseStartReqVO reqVO) {
        return success(responseService.start(reqVO));
    }

    @PostMapping("/{responseNo}/upgrade")
    @Operation(summary = "升级/调整响应级别", description = "调整响应级别和关联预案")
    @Parameter(name = "responseNo", description = "响应编号", required = true)
    public CommonResult<Boolean> upgrade(@PathVariable("responseNo") String responseNo,
                                         @Valid @RequestBody ResponseUpgradeReqVO reqVO) {
        responseService.upgrade(responseNo, reqVO);
        return success(true);
    }

    @PostMapping("/{responseNo}/cancel")
    @Operation(summary = "取消应急响应", description = "取消响应并终止相关任务和调度")
    @Parameter(name = "responseNo", description = "响应编号", required = true)
    public CommonResult<Boolean> cancel(@PathVariable("responseNo") String responseNo,
                                        @Valid @RequestBody ResponseCancelReqVO reqVO) {
        responseService.cancel(responseNo, reqVO);
        return success(true);
    }

    @GetMapping("/{responseNo}/timeline")
    @Operation(summary = "获取响应时间轴", description = "获取响应相关的所有操作记录时间轴")
    @Parameter(name = "responseNo", description = "响应编号", required = true)
    public CommonResult<PageResult<TimelineItemRespVO>> timeline(@PathVariable("responseNo") String responseNo,
                                                                 @Valid TimelinePageReqVO reqVO) {
        return success(responseService.getTimeline(responseNo, reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得应急响应", description = "根据ID获得应急响应详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ResponseRespVO> getResponse(@RequestParam("id") Long id) {
        return success(responseService.getResponse(id));
    }

    @GetMapping("/get-by-event-id")
    @Operation(summary = "根据事件ID获得应急响应", description = "根据事件ID获得应急响应详情")
    @Parameter(name = "eventId", description = "事件ID", required = true, example = "1024")
    public CommonResult<ResponseRespVO> getResponseByEventId(@RequestParam("eventId") Long eventId) {
        return success(responseService.getResponseByEventId(eventId));
    }

    @GetMapping("/page")
    @Operation(summary = "获得应急响应分页")
    public CommonResult<PageResult<ResponseRespVO>> getResponsePage(@Valid PageParam pageReqVO) {
        return success(responseService.getResponsePage(pageReqVO));
    }
}

