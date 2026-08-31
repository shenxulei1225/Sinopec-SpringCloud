package cn.cheers.x.module.dynamicbusiness.controller.admin.sop;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopInstanceBindingRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopInstanceBindingUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopInstanceCreateFromTemplateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopMethodBindingRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopMethodBindingUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.service.sop.SopBindingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 通用 SOP 方法选用 / 实例绑定 API。
 *
 * <p><b>负责</b>：暴露键入参的绑定读写；权威表 V80。</p>
 * <p><b>不负责</b>：检查/应急等业务类型码默认值（由前端/配方传入）。</p>
 * <p><b>禁止</b>：在本 Controller 写死 subject=inspection_item。</p>
 */
@Tag(name = "管理后台 - SOP 通用绑定")
@RestController
@RequestMapping("/dynamicbusiness/sop-bindings")
@Validated
public class SopBindingController {

    @Resource
    private SopBindingService sopBindingService;

    @GetMapping("/methods")
    @Operation(summary = "列出对象下的方法选用行")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<SopMethodBindingRespVO>> listMethods(
            @RequestParam("subjectType") @NotBlank String subjectType,
            @RequestParam("subjectId") @NotNull Long subjectId) {
        return success(sopBindingService.listMethods(subjectType, subjectId));
    }

    @PutMapping("/methods")
    @Operation(summary = "upsert 方法选用：同对象同维度更新模板引用")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Long> upsertMethod(@Valid @RequestBody SopMethodBindingUpsertReqVO reqVO) {
        return success(sopBindingService.upsertMethod(reqVO));
    }

    @GetMapping("/instances")
    @Operation(summary = "列出或按维度查询实例绑定；带 dimensionKey+dimensionValue 时返回 0～1 条列表")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<SopInstanceBindingRespVO>> listOrGetInstances(
            @RequestParam("hostType") @NotBlank String hostType,
            @RequestParam("hostId") @NotNull Long hostId,
            @RequestParam("subjectType") @NotBlank String subjectType,
            @RequestParam("subjectId") @NotNull Long subjectId,
            @RequestParam(value = "dimensionKey", required = false) String dimensionKey,
            @RequestParam(value = "dimensionValue", required = false) String dimensionValue) {
        if (StringUtils.hasText(dimensionKey) && StringUtils.hasText(dimensionValue)) {
            SopInstanceBindingRespVO one = sopBindingService.getInstanceBinding(
                    hostType, hostId, subjectType, subjectId, dimensionKey, dimensionValue);
            return success(one == null ? List.of() : List.of(one));
        }
        return success(sopBindingService.listInstanceBindings(
                hostType, hostId, subjectType, subjectId));
    }

    @PutMapping("/instances")
    @Operation(summary = "upsert 实例绑定；禁止同一实例绑两个宿主")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Boolean> upsertInstance(@Valid @RequestBody SopInstanceBindingUpsertReqVO reqVO) {
        sopBindingService.upsertInstanceBinding(reqVO);
        return success(true);
    }

    @PostMapping("/instances/from-template")
    @Operation(summary = "从 SOP 模板新建独占实例并绑定")
    @PreAuthorize("@ss.hasPermission('system:entity:create')")
    public CommonResult<Map<String, Long>> createInstanceFromTemplate(
            @Valid @RequestBody SopInstanceCreateFromTemplateReqVO reqVO) {
        long sopInstanceId = sopBindingService.createInstanceFromTemplate(reqVO);
        Map<String, Long> body = new LinkedHashMap<>();
        body.put("sopInstanceId", sopInstanceId);
        return success(body);
    }
}
