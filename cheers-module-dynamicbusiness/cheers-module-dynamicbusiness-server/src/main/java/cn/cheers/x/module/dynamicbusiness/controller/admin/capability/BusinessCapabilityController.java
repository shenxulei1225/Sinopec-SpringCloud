package cn.cheers.x.module.dynamicbusiness.controller.admin.capability;

import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.BusinessCapabilityFullRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.BusinessCapabilitySummaryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.CapabilityComponentProjectionRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.ModelCrudFormDefinitionRespVO;
import cn.cheers.x.module.dynamicbusiness.service.capability.BusinessCapabilityService;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.UPDATE;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 业务能力控制器。
 *
 * <p>接口边界（无兼容模式）：</p>
 * <ul>
 *   <li>能力列表按 businessTypeCode 返回；</li>
 *   <li>投影读取按 businessTypeCode + componentCode 返回；</li>
 *   <li>模型表单读取按 businessTypeCode + modelId 返回；</li>
 *   <li>不接受 dataSourceKey，也不提供旧 /instances/* 端点。</li>
 * </ul>
 */
@Tag(name = "管理后台 - 业务能力管理", description = "业务能力列表、能力全集、组件投影、模型 CRUD 表单定义读取与重建")
@RestController
@RequestMapping("/dynamicbusiness/capability")
@Validated
public class BusinessCapabilityController {

    @Resource
    private BusinessCapabilityService businessCapabilityService;

    @GetMapping("/list")
    @Operation(summary = "获取业务能力列表", description = "供前端业务数据来源下拉使用；可按 businessCategory 过滤。")
    @Parameter(name = "businessCategory", description = "业务分类：dynamic / system；不传则返回全部", example = "dynamic")
    @PreAuthorize("@ss.hasPermission('system:business-type:query')")
    public CommonResult<List<BusinessCapabilitySummaryRespVO>> list(
            @RequestParam(value = "businessCategory", required = false) String businessCategory) {
        return success(businessCapabilityService.listCapabilitySummaries(businessCategory));
    }

    @GetMapping("/full")
    @Operation(summary = "读取能力全集")
    @Parameter(name = "businessTypeCode", description = "业务类型编码", required = true, example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:business-type:query')")
    public CommonResult<BusinessCapabilityFullRespVO> getFull(@RequestParam("businessTypeCode") String businessTypeCode) {
        return success(businessCapabilityService.getCapabilityFull(businessTypeCode));
    }

    @GetMapping("/projection")
    @Operation(summary = "读取组件能力投影")
    @Parameter(name = "businessTypeCode", description = "业务类型编码", required = true, example = "equipment")
    @Parameter(name = "componentCode", description = "组件编码（list/tree/table/card）", required = true, example = "list")
    @Parameter(name = "dataKind", description = "数据种类：model / entity；system 固定 entity", example = "entity")
    @PreAuthorize("@ss.hasPermission('system:business-type:query')")
    public CommonResult<CapabilityComponentProjectionRespVO> getProjection(
            @RequestParam("businessTypeCode") String businessTypeCode,
            @RequestParam("componentCode") String componentCode,
            @RequestParam(value = "dataKind", required = false, defaultValue = "entity") String dataKind) {
        return success(businessCapabilityService.getProjection(businessTypeCode, componentCode, dataKind));
    }

    @GetMapping("/model-crud-form")
    @Operation(summary = "读取模型 CRUD 表单定义")
    @Parameter(name = "businessTypeCode", description = "业务类型编码", required = true, example = "equipment")
    @Parameter(name = "modelId", description = "模型编号", required = true, example = "1001")
    @PreAuthorize("@ss.hasPermission('system:business-type:query')")
    public CommonResult<ModelCrudFormDefinitionRespVO> getModelCrudForm(
            @RequestParam("businessTypeCode") String businessTypeCode,
            @RequestParam("modelId") Long modelId) {
        return success(businessCapabilityService.getModelCrudFormDefinition(businessTypeCode, modelId));
    }

    @PostMapping("/internal/rebuild/system-all")
    @Operation(summary = "重建全部系统业务能力")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:business-type:update')")
    public CommonResult<Boolean> rebuildAllSystemCapabilities() {
        businessCapabilityService.rebuildAllSystemCapabilities();
        return success(true);
    }

    @PostMapping("/internal/rebuild/business-type")
    @Operation(summary = "按业务类型重建能力")
    @Parameter(name = "businessTypeCode", description = "业务类型编码", required = true, example = "equipment")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:business-type:update')")
    public CommonResult<Boolean> rebuildByBusinessTypeCode(@RequestParam("businessTypeCode") String businessTypeCode) {
        businessCapabilityService.rebuildByBusinessTypeCode(businessTypeCode);
        return success(true);
    }

    @PostMapping("/internal/rebuild/model")
    @Operation(summary = "按模型触发能力重建")
    @Parameter(name = "modelId", description = "模型编号", required = true, example = "1001")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:business-type:update')")
    public CommonResult<Boolean> rebuildByModelId(@RequestParam("modelId") Long modelId) {
        businessCapabilityService.rebuildByModelId(modelId);
        return success(true);
    }
}
