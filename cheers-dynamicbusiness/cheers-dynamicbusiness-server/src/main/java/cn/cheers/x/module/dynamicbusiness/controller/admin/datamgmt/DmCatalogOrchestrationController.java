package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt;

import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmCatalogOrchestrationBundleRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmCatalogOrchestrationBundleSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.service.datamgmt.DmCatalogOrchestrationService;
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

@Tag(name = "管理后台 - 数据目录编排")
@RestController
@RequestMapping("/dynamicbusiness/data-mgmt/catalog-orchestration")
@Validated
public class DmCatalogOrchestrationController {

    @Resource
    private DmCatalogOrchestrationService catalogOrchestrationService;

    @GetMapping("/{registryCode}")
    @Operation(summary = "读取数据目录编排头")
    @Parameter(name = "registryCode", description = "注册编码", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<DmCatalogOrchestrationBundleRespVO> get(
            @PathVariable("registryCode") String registryCode) {
        return success(catalogOrchestrationService.getBundle(registryCode));
    }

    @PutMapping("/{registryCode}")
    @Operation(summary = "保存数据目录编排头")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Boolean> save(
            @PathVariable("registryCode") String registryCode,
            @Valid @RequestBody DmCatalogOrchestrationBundleSaveReqVO reqVO) {
        reqVO.setRegistryCode(registryCode);
        catalogOrchestrationService.saveBundle(reqVO);
        return success(true);
    }
}
