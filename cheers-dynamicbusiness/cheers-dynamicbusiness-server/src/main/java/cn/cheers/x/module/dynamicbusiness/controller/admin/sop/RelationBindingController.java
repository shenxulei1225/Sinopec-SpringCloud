package cn.cheers.x.module.dynamicbusiness.controller.admin.sop;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.RelationInstanceBindingRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.RelationInstanceBindingUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.RelationMethodBindingRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.RelationMethodBindingUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.service.sop.RelationBindingService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 通用关系绑定 API。
 *
 * <p><b>负责</b>：暴露「subject+dimension -> target」与「host+subject+dimension -> target」读写。</p>
 * <p><b>不负责</b>：解释业务类型码含义（由调用方传入）。</p>
 * <p><b>禁止</b>：写死任何业务预设类型。</p>
 */
@Tag(name = "管理后台 - 通用关系绑定")
@RestController
@RequestMapping("/dynamicbusiness/relation-bindings")
@Validated
public class RelationBindingController {

    @Resource
    private RelationBindingService relationBindingService;

    @GetMapping("/methods")
    @Operation(summary = "列出对象下的方法选用行")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<RelationMethodBindingRespVO>> listMethods(
            @RequestParam("subjectType") @NotBlank String subjectType,
            @RequestParam("subjectId") @NotNull Long subjectId) {
        return success(relationBindingService.listMethods(subjectType, subjectId));
    }

    @PutMapping("/methods")
    @Operation(summary = "upsert 对象维度绑定：同对象同维度更新目标实体引用")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Long> upsertMethod(@Valid @RequestBody RelationMethodBindingUpsertReqVO reqVO) {
        return success(relationBindingService.upsertMethod(reqVO));
    }

    @GetMapping("/instances")
    @Operation(summary = "列出或按维度查询宿主绑定；带 dimensionKey+dimensionValue 时返回 0～1 条列表")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<RelationInstanceBindingRespVO>> listOrGetInstances(
            @RequestParam("hostType") @NotBlank String hostType,
            @RequestParam("hostId") @NotNull Long hostId,
            @RequestParam("subjectType") @NotBlank String subjectType,
            @RequestParam("subjectId") @NotNull Long subjectId,
            @RequestParam(value = "dimensionKey", required = false) String dimensionKey,
            @RequestParam(value = "dimensionValue", required = false) String dimensionValue) {
        if (StringUtils.hasText(dimensionKey) && StringUtils.hasText(dimensionValue)) {
            RelationInstanceBindingRespVO one = relationBindingService.getInstanceBinding(
                    hostType, hostId, subjectType, subjectId, dimensionKey, dimensionValue);
            return success(one == null ? List.of() : List.of(one));
        }
        return success(relationBindingService.listInstanceBindings(
                hostType, hostId, subjectType, subjectId));
    }

    @PutMapping("/instances")
    @Operation(summary = "upsert 宿主绑定；禁止同一目标绑两个宿主")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Boolean> upsertInstance(@Valid @RequestBody RelationInstanceBindingUpsertReqVO reqVO) {
        relationBindingService.upsertInstanceBinding(reqVO);
        return success(true);
    }

}
