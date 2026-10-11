package cn.cheers.x.module.dynamicbusiness.controller.admin.analysis;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.analysis.dto.AnalysisJobRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.analysis.dto.AnalysisJobSubmitReqDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.analysis.vo.AnalysisCapabilityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.analysis.vo.AnalysisCapabilitySaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.analysis.vo.AnalysisProviderRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.analysis.vo.AnalysisProviderSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.service.analysis.IntelligentAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 智能分析管理入口。
 *
 * <p>负责分析能力、服务接入和任务查看。不负责动作 CRUD，也不维护设备协议支持关系。</p>
 */
@Tag(name = "管理后台 - 智能分析")
@RestController
@RequestMapping("/intelligent-analysis")
@Validated
@RequiredArgsConstructor
public class IntelligentAnalysisController {

    private final IntelligentAnalysisService intelligentAnalysisService;

    @GetMapping("/capabilities")
    @Operation(summary = "列出分析能力")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<AnalysisCapabilityRespVO>> capabilities(
            @RequestParam(value = "onlyAvailable", defaultValue = "false") boolean onlyAvailable) {
        return success(intelligentAnalysisService.listCapabilities(onlyAvailable));
    }

    @PostMapping("/capabilities")
    @Operation(summary = "新增分析能力")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Long> createCapability(
            @Valid @RequestBody AnalysisCapabilitySaveReqVO request) {
        request.setId(null);
        return success(intelligentAnalysisService.saveCapability(request));
    }

    @PutMapping("/capabilities")
    @Operation(summary = "更新分析能力")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Long> updateCapability(
            @Valid @RequestBody AnalysisCapabilitySaveReqVO request) {
        return success(intelligentAnalysisService.saveCapability(request));
    }

    @GetMapping("/providers")
    @Operation(summary = "列出服务接入")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<AnalysisProviderRespVO>> providers() {
        return success(intelligentAnalysisService.listProviders());
    }

    @PostMapping("/providers")
    @Operation(summary = "新增服务接入")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Long> createProvider(
            @Valid @RequestBody AnalysisProviderSaveReqVO request) {
        request.setId(null);
        return success(intelligentAnalysisService.saveProvider(request));
    }

    @PutMapping("/providers")
    @Operation(summary = "更新服务接入")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Long> updateProvider(
            @Valid @RequestBody AnalysisProviderSaveReqVO request) {
        return success(intelligentAnalysisService.saveProvider(request));
    }

    @GetMapping("/jobs")
    @Operation(summary = "列出最近分析任务")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<AnalysisJobRespDTO>> jobs(
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        return success(intelligentAnalysisService.listRecentJobs(limit));
    }

    @PostMapping("/jobs")
    @Operation(summary = "提交一次分析任务")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<AnalysisJobRespDTO> submitJob(
            @Valid @RequestBody AnalysisJobSubmitReqDTO request) {
        return success(intelligentAnalysisService.submitJob(request));
    }

    @PostMapping("/callbacks/legacy-detection")
    @PermitAll
    @Operation(summary = "接收 detection 服务回调")
    public Map<String, Object> legacyDetectionCallback(@RequestBody String body) {
        intelligentAnalysisService.applyLegacyCallback(body);
        return Map.of("code", 0);
    }
}
