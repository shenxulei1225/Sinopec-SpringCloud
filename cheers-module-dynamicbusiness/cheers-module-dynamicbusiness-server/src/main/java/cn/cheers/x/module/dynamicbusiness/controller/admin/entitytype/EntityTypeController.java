package cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.*;
import cn.cheers.x.module.dynamicbusiness.enums.businesstype.StorageTypeEnum;
import cn.cheers.x.module.dynamicbusiness.service.businesstype.BusinessTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 业务类型 Controller
 *
 * 职责:
 * - 业务类型(BusinessType)的元模型入口(What 维度)。
 * - 提供业务能力发现接口:子业务导航树、配置子业务清单。
 * - 关联存储策略配置(实现细节)。
 *
 * 建模指引:
 * - 优先复用已有 BusinessType,通过 Model/Category/View 扩展视角。
 * - 复杂业务(如巡检)应通过关联多个业务元素(配置子业务/资源业务)实现。
 */
@Tag(name = "管理后台 - 业务类型", description = "业务类型的元模型入口与能力发现,用于定义业务本体(What)及其关联的业务元素")
@RestController
@RequestMapping("/dynamicbusiness/business-type")
@Validated
public class BusinessTypeController {

    @Resource
    private BusinessTypeService businessTypeService;

    // ========== 业务类型本体 (BusinessType) CRUD ==========

    @PostMapping("/create")
    @Operation(summary = "创建业务类型(含配置)", description = "创建新的业务本体(如巡检任务、维修工单),初始化存储策略")
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:business-type:create')")
    public CommonResult<Long> create(@Valid @RequestBody BusinessTypeCreateReqVO reqVO) {
        return success(businessTypeService.create(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新业务类型(含配置)", description = "修改业务类型的基本元信息或调整存储策略")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:business-type:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody BusinessTypeUpdateReqVO reqVO) {
        businessTypeService.update(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除业务类型")
    @Parameter(name = "id", description = "业务类型编号", required = true, example = "1024")
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('system:business-type:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        businessTypeService.delete(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取业务类型详情")
    @Parameter(name = "id", description = "业务类型编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:business-type:query')")
    public CommonResult<BusinessTypeRespVO> get(@RequestParam("id") Long id) {
        return success(businessTypeService.get(id));
    }


    // ========== 业务类型发现与组合能力 ==========

    @GetMapping("/get-by-code")
    @Operation(summary = "根据编码获取业务类型", description = "用于跨模块通过 businessTypeCode 快速定位业务元模型")
    @Parameter(name = "businessTypeCode", description = "业务类型编码", required = true, example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:business-type:query')")
    public CommonResult<BusinessTypeRespVO> getByCode(@RequestParam("businessTypeCode") String businessTypeCode) {
        return success(businessTypeService.getByCode(businessTypeCode));
    }

    @GetMapping("/exists")
    @Operation(summary = "检查业务类型是否存在")
    @Parameter(name = "businessTypeCode", description = "业务类型编码", required = true, example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:business-type:query')")
    public CommonResult<Boolean> exists(@RequestParam("businessTypeCode") String businessTypeCode) {
        return success(businessTypeService.checkBusinessTypeExists(businessTypeCode));
    }

    @GetMapping("/list-tree")
    @Operation(summary = "获取全量业务类型树", description = "用于系统级业务树导航或全局层级管理；业务排序调整请在业务管理界面完成")
    @PreAuthorize("@ss.hasPermission('system:business-type:query')")
    public CommonResult<List<BusinessTypeRespVO>> listTree() {
        return success(businessTypeService.listTree());
    }

    @GetMapping("/list-children-tree")
    @Operation(summary = "获取指定业务的子业务树", description = "进入某业务管理页面时,获取其下属的子业务导航树(用于切换具体业务入口)")
    @Parameter(name = "businessTypeCode", description = "根业务类型编码", required = true, example = "INSPECTION_TASK")
    @PreAuthorize("@ss.hasPermission('system:business-type:query')")
    public CommonResult<List<BusinessTypeRespVO>> listChildrenTree(@RequestParam("businessTypeCode") String businessTypeCode) {
        return success(businessTypeService.listChildrenTreeByCode(businessTypeCode));
    }

    @GetMapping("/list-all")
    @Operation(summary = "获取所有业务类型列表(平铺)", description = "用于业务列表展示或下拉选择（不构建树结构，按业务树 DFS 顺序展平）")
    @PreAuthorize("@ss.hasPermission('system:business-type:query')")
    public CommonResult<List<BusinessTypeRespVO>> listAll() {
        return success(businessTypeService.listAcrossBusinessTypes());
    }

    @GetMapping("/list-simple")
    @Operation(summary = "获取业务类型简单列表", description = "用于简单下拉选择（仅返回 code/name/description）")
    @PreAuthorize("@ss.hasPermission('system:business-type:query')")
    public CommonResult<List<BusinessTypeSimpleVO>> listSimple() {
        return success(businessTypeService.listSimple());
    }

    @GetMapping("/list-config-children")
    @Operation(summary = "获取某业务的配置子业务列表", description = "获取主业务(如巡检)所关联的配置/资源类业务元素(如巡检点、检查项),用于 How 维度建模")
    @Parameter(name = "businessTypeCode", description = "主业务类型编码", required = true, example = "INSPECTION_TASK")
    @PreAuthorize("@ss.hasPermission('system:business-type:query')")
    public CommonResult<List<BusinessTypeRespVO>> listConfigChildren(@RequestParam("businessTypeCode") String businessTypeCode) {
        return success(businessTypeService.listConfigChildrenByCode(businessTypeCode));
    }

    // ========== 存储配置 (Config) 细节 API ==========

    @GetMapping("/config/storage-types")
    @Operation(summary = "获取支持的存储类型选项", description = "用于创建业务类型时选择存储策略(GENERIC/DEDICATED等)")
    public CommonResult<List<StorageTypeOption>> getStorageTypes() {
        List<StorageTypeOption> options = Arrays.stream(StorageTypeEnum.values())
                .map(type -> new StorageTypeOption(type.getCode(), type.getName(),
                        type.isDedicated(), false, type.supportsRuleEngine()))
                .toList();
        return success(options);
    }

    @GetMapping("/config/is-dedicated")
    @Operation(summary = "判断是否使用专用存储")
    @Parameter(name = "businessTypeCode", description = "业务类型编码", required = true, example = "equipment")
    public CommonResult<Boolean> isDedicatedStorage(@RequestParam("businessTypeCode") String businessTypeCode) {
        return success(businessTypeService.isDedicatedStorage(businessTypeCode));
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新业务类型状态", description = "用于激活或停用业务类型及其关联配置")
    @PreAuthorize("@ss.hasPermission('system:business-type:update')")
    public CommonResult<Boolean> updateStatus(@RequestParam("id") Long id,
                                              @RequestParam("status") String status) {
        businessTypeService.updateStatus(id, status);
        return success(true);
    }

    @GetMapping("/statistics")
    @Operation(
        summary = "获取业务类型的统计信息",
        description = "获取业务类型的统计信息,包括分类数量、模型数量、实体数量等"
    )
    @Parameter(name = "businessTypeCode", description = "业务类型编码", required = true, example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:business-type:query')")
    public CommonResult<Map<String, Object>> getStatistics(
            @RequestParam("businessTypeCode") String businessTypeCode) {
        return success(businessTypeService.getBusinessTypeStatistics(businessTypeCode));
    }
    /**
     * 存储类型选项
     */
    public record StorageTypeOption(
            String code,
            String name,
            boolean dedicated,
            boolean requiresCodeDevelopment,
            boolean supportsRuleEngine
    ) {}
}
