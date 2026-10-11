package cn.cheers.x.inspection.task.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 动作库或检查项保存之后，给用到这次改动的已生成任务记「需要更新」。
 * 列表只读任务上的标记，不在打开列表时重算。
 */
@FeignClient(name = TaskUpdateHintRpcApi.SERVICE_NAME)
@Tag(name = "RPC 服务 - 已生成任务更新标记")
public interface TaskUpdateHintRpcApi {

    String SERVICE_NAME = "inspection-task-server";

    String PREFIX = "/rpc-api/inspection-task/update-hint";

    @PostMapping(PREFIX + "/mark")
    @Operation(summary = "按这次保存的动作或检查项，给用到它的已生成任务记需要更新")
    CommonResult<Boolean> mark(@RequestBody TaskUpdateHintMarkReqDTO req);
}
