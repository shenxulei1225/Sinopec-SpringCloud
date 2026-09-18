package cn.cheers.x.module.dynamicbusiness.controller.admin.orchestration;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.orchestration.vo.BusinessOrchestrationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.orchestration.vo.BusinessOrchestrationUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.service.orchestration.BusinessOrchestrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 业务编排（5W）API。
 *
 * <p>负责：按 businessCode 读/写编排权威与画布坐标。</p>
 * <p>不负责：功能点内部细配；不与门户业务 CRUD 混用。</p>
 */
@Tag(name = "管理后台 - 业务编排（5W）")
@RestController
@RequestMapping("/dynamicbusiness/business-orchestrations")
@Validated
public class BusinessOrchestrationController {

    @Resource
    private BusinessOrchestrationService businessOrchestrationService;

    @GetMapping("/get-by-code")
    @Operation(summary = "按业务编码获取编排；不存在返回 data=null")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<BusinessOrchestrationRespVO> getByCode(
            @RequestParam("businessCode") @NotBlank String businessCode) {
        return success(businessOrchestrationService.getByBusinessCode(businessCode));
    }

    @PutMapping("/upsert")
    @Operation(summary = "按业务编码 upsert 编排权威与画布")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Long> upsert(@Valid @RequestBody BusinessOrchestrationUpsertReqVO reqVO) {
        return success(businessOrchestrationService.upsert(reqVO));
    }
}
