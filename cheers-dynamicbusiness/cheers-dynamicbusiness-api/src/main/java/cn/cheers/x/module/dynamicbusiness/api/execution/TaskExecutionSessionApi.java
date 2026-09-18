package cn.cheers.x.module.dynamicbusiness.api.execution;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;
import cn.cheers.x.module.dynamicbusiness.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    @PostMapping(PREFIX + "/writeback")
    @Operation(summary = "更新这次执行的状态或某一步的状态")
    CommonResult<Boolean> writeback(@Valid @RequestBody TaskExecutionWritebackReqDTO req);

    @PostMapping(PREFIX + "/append-process")
    @Operation(summary = "往这次执行的账里记一条过程")
    CommonResult<TaskExecutionAppendProcessRespDTO> appendProcess(
            @Valid @RequestBody TaskExecutionAppendProcessReqDTO req);
}
