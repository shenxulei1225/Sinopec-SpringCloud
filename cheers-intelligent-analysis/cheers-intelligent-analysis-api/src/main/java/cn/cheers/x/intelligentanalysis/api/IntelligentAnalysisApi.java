package cn.cheers.x.intelligentanalysis.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.intelligentanalysis.api.dto.AnalysisJobRespDTO;
import cn.cheers.x.intelligentanalysis.api.dto.AnalysisJobSubmitReqDTO;
import cn.cheers.x.intelligentanalysis.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 智能分析标准服务。
 *
 * <p>负责按平台能力确认可用性、提交和查询分析任务。不暴露厂商算法编号，也不根据动作名称选择算法。</p>
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 智能分析")
public interface IntelligentAnalysisApi {

    String PREFIX = ApiConstants.PREFIX;

    @GetMapping(PREFIX + "/capabilities/require-available")
    @Operation(summary = "确认分析能力当前可以受理任务")
    CommonResult<Boolean> requireAvailable(@RequestParam("capabilityId") Long capabilityId);

    @PostMapping(PREFIX + "/jobs")
    @Operation(summary = "提交图片或录像分析任务")
    CommonResult<AnalysisJobRespDTO> submitJob(
            @Valid @RequestBody AnalysisJobSubmitReqDTO request);

    @GetMapping(PREFIX + "/jobs/get")
    @Operation(summary = "按任务编号查询分析结果")
    CommonResult<AnalysisJobRespDTO> getJob(@RequestParam("jobNo") String jobNo);
}
