package cn.cheers.x.module.platform.runtime.controller.admin;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotReleaseReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotStatusUpdateReqDTO;
import cn.cheers.x.module.platform.runtime.service.RuntimeSlotWriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 平台 L4 计划点回写")
@RestController
@RequestMapping("/platform/runtime/slots")
@Validated
public class RuntimeSlotWriteController {

    @Resource
    private RuntimeSlotWriteService runtimeSlotWriteService;

    @PostMapping("/update-status")
    @Operation(summary = "回写计划点状态并追加过程时间线")
    public CommonResult<Boolean> updateSlotStatus(@Valid @RequestBody RuntimeSlotStatusUpdateReqDTO request) {
        runtimeSlotWriteService.updateSlotStatus(request);
        return success(true);
    }

    @PostMapping("/release-unfinished")
    @Operation(summary = "按运行作业释放未执行计划点占用（让路/挂起/中止）")
    public CommonResult<Boolean> releaseUnfinished(@Valid @RequestBody RuntimeSlotReleaseReqDTO request) {
        runtimeSlotWriteService.releaseUnfinished(request);
        return success(true);
    }
}
