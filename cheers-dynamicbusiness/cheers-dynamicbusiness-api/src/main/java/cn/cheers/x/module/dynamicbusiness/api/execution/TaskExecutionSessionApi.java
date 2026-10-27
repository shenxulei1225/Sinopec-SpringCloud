package cn.cheers.x.module.dynamicbusiness.api.execution;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendPoseReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionDayAccountPageDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionMarkProcessReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionMarkProcessRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartBatchReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartBatchRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStepSnapshotDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionUnfinishedOptionDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;
import cn.cheers.x.module.dynamicbusiness.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 任务模块执行会话 RPC：新建这次执行的账 / 更新状态与步骤 / 往账里记一条过程。
 * <p>不绑定设备或某一业务域。
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 任务执行会话")
public interface TaskExecutionSessionApi {

    String PREFIX = ApiConstants.DYNAMICBUSINESS_PREFIX + "/task-execution-session";

    @PostMapping(PREFIX + "/start")
    @Operation(summary = "创建执行记录并 bootstrap 步骤")
    CommonResult<TaskExecutionStartRespDTO> start(@Valid @RequestBody TaskExecutionStartReqDTO req);

    @PostMapping(PREFIX + "/start-batch")
    @Operation(summary = "同一份步骤清单写入多条待执行账")
    CommonResult<TaskExecutionStartBatchRespDTO> startBatch(
            @Valid @RequestBody TaskExecutionStartBatchReqDTO req);

    @PostMapping(PREFIX + "/writeback")
    @Operation(summary = "更新这次执行的状态或某一步的状态")
    CommonResult<Boolean> writeback(@Valid @RequestBody TaskExecutionWritebackReqDTO req);

    @PostMapping(PREFIX + "/delete-pending")
    @Operation(summary = "删除一条还没开始的执行账及其步骤")
    CommonResult<Boolean> deletePending(@RequestParam("executionRecordId") Long executionRecordId);

    @PostMapping(PREFIX + "/append-process")
    @Operation(summary = "往这次执行的账里记一条过程")
    CommonResult<TaskExecutionAppendProcessRespDTO> appendProcess(
            @Valid @RequestBody TaskExecutionAppendProcessReqDTO req);

    @PostMapping(PREFIX + "/mark-process-event")
    @Operation(summary = "按消息编号把已有下发事件标成设备已接收或设备已响应")
    CommonResult<TaskExecutionMarkProcessRespDTO> markProcessEvent(
            @Valid @RequestBody TaskExecutionMarkProcessReqDTO req);

    @PostMapping(PREFIX + "/append-pose")
    @Operation(summary = "往这次执行另存一个回放位置点")
    CommonResult<Boolean> appendPose(@Valid @RequestBody TaskExecutionAppendPoseReqDTO req);

    @GetMapping(PREFIX + "/steps")
    @Operation(summary = "列出这次执行账上的步骤")
    CommonResult<List<TaskExecutionStepSnapshotDTO>> listSteps(
            @RequestParam("executionRecordId") Long executionRecordId);

    /**
     * 协议监控任务下拉。按执行账快照里的设备编号，列出待执行和正在执行的账。
     * 只返回名称、状态、计划时刻、收流地址和模板编号。
     * 不返回指令包正文。其它业务不要拿它当通用列表。
     */
    @GetMapping(PREFIX + "/unfinished-for-device")
    @Operation(summary = "这台设备还没结束的执行账，只含监控下拉要的几项")
    CommonResult<List<TaskExecutionUnfinishedOptionDTO>> listUnfinishedForDevice(
            @RequestParam("deviceId") String deviceId);

    /**
     * 巡检图层侧栏。按当前站场和所选日期，列出这一天的执行账。
     * 日期格式 yyyy-MM-dd。只返回名称、任务、计划时刻和执行状态。
     * 不作为数据目录的通用列表。
     */
    @GetMapping(PREFIX + "/patrol-layer-day")
    @Operation(summary = "巡检图层侧栏：当前站场、所选日期的执行账")
    CommonResult<TaskExecutionDayAccountPageDTO> listPatrolLayerDay(
            @RequestParam("facilityId") Long facilityId,
            @RequestParam("day") String day,
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("pageSize") Integer pageSize);
}
