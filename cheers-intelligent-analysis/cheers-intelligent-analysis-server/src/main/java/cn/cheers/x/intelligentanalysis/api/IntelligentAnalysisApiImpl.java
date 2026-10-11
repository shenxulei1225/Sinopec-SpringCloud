package cn.cheers.x.intelligentanalysis.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.intelligentanalysis.api.dto.AnalysisJobRespDTO;
import cn.cheers.x.intelligentanalysis.api.dto.AnalysisJobSubmitReqDTO;
import cn.cheers.x.intelligentanalysis.service.IntelligentAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 智能分析标准服务入口。
 *
 * <p>调用方只能提交平台能力 id、媒体引用和参数。厂商选择由智能分析模块完成。</p>
 */
@RestController
@Validated
@RequiredArgsConstructor
public class IntelligentAnalysisApiImpl implements IntelligentAnalysisApi {

    private final IntelligentAnalysisService intelligentAnalysisService;

    @Override
    public CommonResult<Boolean> requireAvailable(Long capabilityId) {
        intelligentAnalysisService.assertCapabilityAvailable(capabilityId);
        return success(true);
    }

    @Override
    public CommonResult<AnalysisJobRespDTO> submitJob(AnalysisJobSubmitReqDTO request) {
        return success(intelligentAnalysisService.submitJob(request));
    }

    @Override
    public CommonResult<AnalysisJobRespDTO> getJob(String jobNo) {
        return success(intelligentAnalysisService.getJob(jobNo));
    }
}
