package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt;

import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmFiveWOrchestrationBundleRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmFiveWOrchestrationBundleSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.service.datamgmt.DmFiveWOrchestrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.UPDATE;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 五维编排")
@RestController
@RequestMapping("/dynamicbusiness/data-mgmt/five-w-orchestration")
@Validated
public class DmFiveWOrchestrationController {

    @Resource
    private DmFiveWOrchestrationService dmFiveWOrchestrationService;

    @GetMapping("/{registryCode}")
    @Operation(summary = "读取五维编排 bundle")
    @Parameter(name = "registryCode", description = "注册编码", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<DmFiveWOrchestrationBundleRespVO> get(
            @PathVariable("registryCode") String registryCode) {
        return success(dmFiveWOrchestrationService.getBundle(registryCode));
    }

    @PutMapping("/{registryCode}")
    @Operation(summary = "保存五维编排 bundle")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Boolean> save(
            @PathVariable("registryCode") String registryCode,
            @Valid @RequestBody DmFiveWOrchestrationBundleSaveReqVO reqVO) {
        reqVO.setRegistryCode(registryCode);
        dmFiveWOrchestrationService.saveBundle(reqVO);
        return success(true);
    }
}
