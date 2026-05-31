package cn.cheers.x.module.dynamicbusiness.controller.admin.computed;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.computed.vo.ComputedFieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.computed.vo.ComputedFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.computed.vo.ComputedFieldUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.service.computed.ComputedFieldService;
import cn.cheers.x.module.dynamicbusiness.service.computed.precompute.PrecomputeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 计算字段 Controller
 * 
 * 提供计算字段的管理接口，支持：
 * - 计算字段的 CRUD 操作
 * - 字段值计算（实时计算）
 * - 缓存管理
 * 
 * 需求：FR-BDA-050~055
 * 
 * @author yudao
 */
@Tag(name = "管理后台 - 计算字段")
@RestController
@RequestMapping("/dynamicbusiness/computed-field")
@Validated
public class ComputedFieldController {

    @Resource
    private ComputedFieldService computedFieldService;

    @Resource
    private PrecomputeService precomputeService;

    // ========== CRUD 接口 ==========

    @PostMapping("/create")
    @Operation(summary = "创建计算字段")
    @PreAuthorize("@ss.hasPermission('system:computed-field:create')")
    public CommonResult<Long> createComputedField(@Valid @RequestBody ComputedFieldCreateReqVO reqVO) {
        return success(computedFieldService.createComputedField(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新计算字段")
    @PreAuthorize("@ss.hasPermission('system:computed-field:update')")
    public CommonResult<Boolean> updateComputedField(@Valid @RequestBody ComputedFieldUpdateReqVO reqVO) {
        computedFieldService.updateComputedField(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除计算字段")
    @Parameter(name = "id", description = "计算字段ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:computed-field:delete')")
    public CommonResult<Boolean> deleteComputedField(@RequestParam("id") Long id) {
        computedFieldService.deleteComputedField(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取计算字段详情")
    @Parameter(name = "id", description = "计算字段ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:computed-field:query')")
    public CommonResult<ComputedFieldRespVO> getComputedField(@RequestParam("id") Long id) {
        return success(computedFieldService.getComputedField(id));
    }

    @GetMapping("/list")
    @Operation(summary = "获取 Model 的计算字段列表")
    @Parameter(name = "modelId", description = "Model ID", required = true, example = "100")
    @PreAuthorize("@ss.hasPermission('system:computed-field:query')")
    public CommonResult<List<ComputedFieldRespVO>> getComputedFieldList(
            @RequestParam("modelId") Long modelId) {
        return success(computedFieldService.getComputedFieldsByModelId(modelId));
    }

    // ========== 字段值计算接口 ==========

    @GetMapping("/compute")
    @Operation(summary = "计算字段值（实时）")
    @Parameters({
            @Parameter(name = "entityId", description = "实体ID", required = true, example = "1"),
            @Parameter(name = "fieldCodes", description = "字段编码列表，逗号分隔", required = true, 
                    example = "fault_device_count,fault_rate")
    })
    @PreAuthorize("@ss.hasPermission('system:computed-field:query')")
    public CommonResult<Map<String, Object>> computeFieldValues(
            @RequestParam("entityId") Long entityId,
            @RequestParam("fieldCodes") List<String> fieldCodes) {
        return success(computedFieldService.computeFieldValues(entityId, fieldCodes));
    }

    @GetMapping("/compute-single")
    @Operation(summary = "计算单个字段值")
    @Parameters({
            @Parameter(name = "entityId", description = "实体ID", required = true, example = "1"),
            @Parameter(name = "fieldCode", description = "字段编码", required = true, example = "fault_device_count")
    })
    @PreAuthorize("@ss.hasPermission('system:computed-field:query')")
    public CommonResult<Object> computeSingleFieldValue(
            @RequestParam("entityId") Long entityId,
            @RequestParam("fieldCode") String fieldCode) {
        return success(computedFieldService.computeFieldValue(entityId, fieldCode));
    }

    @GetMapping("/compute-all")
    @Operation(summary = "计算 Model 的所有计算字段值")
    @Parameters({
            @Parameter(name = "entityId", description = "实体ID", required = true, example = "1"),
            @Parameter(name = "modelId", description = "Model ID", required = true, example = "100")
    })
    @PreAuthorize("@ss.hasPermission('system:computed-field:query')")
    public CommonResult<Map<String, Object>> computeAllFieldValues(
            @RequestParam("entityId") Long entityId,
            @RequestParam("modelId") Long modelId) {
        return success(computedFieldService.computeAllFieldValues(entityId, modelId));
    }

    // ========== 缓存管理接口 ==========

    @PostMapping("/refresh-cache")
    @Operation(summary = "刷新指定字段的缓存")
    @Parameters({
            @Parameter(name = "modelId", description = "Model ID", required = true, example = "100"),
            @Parameter(name = "fieldCode", description = "字段编码", required = true, example = "fault_device_count")
    })
    @PreAuthorize("@ss.hasPermission('system:computed-field:update')")
    public CommonResult<Boolean> refreshCache(
            @RequestParam("modelId") Long modelId,
            @RequestParam("fieldCode") String fieldCode) {
        computedFieldService.refreshCache(modelId, fieldCode);
        return success(true);
    }

    @PostMapping("/refresh-all-cache")
    @Operation(summary = "刷新 Model 的所有缓存字段")
    @Parameter(name = "modelId", description = "Model ID", required = true, example = "100")
    @PreAuthorize("@ss.hasPermission('system:computed-field:update')")
    public CommonResult<Boolean> refreshAllCache(@RequestParam("modelId") Long modelId) {
        computedFieldService.refreshAllCache(modelId);
        return success(true);
    }

    @PostMapping("/trigger-precompute")
    @Operation(summary = "触发预计算")
    @Parameter(name = "entityId", description = "实体ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:computed-field:update')")
    public CommonResult<Boolean> triggerPrecompute(@RequestParam("entityId") Long entityId) {
        computedFieldService.triggerPrecompute(entityId);
        return success(true);
    }

    @PostMapping("/clear-entity-cache")
    @Operation(summary = "清除指定实体的所有缓存")
    @Parameter(name = "entityId", description = "实体ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:computed-field:update')")
    public CommonResult<Boolean> clearEntityCache(@RequestParam("entityId") Long entityId) {
        computedFieldService.clearEntityCache(entityId);
        return success(true);
    }

    @PostMapping("/batch-refresh-entity-cache")
    @Operation(summary = "批量刷新实体缓存")
    @Parameters({
            @Parameter(name = "modelId", description = "Model ID", required = true, example = "100"),
            @Parameter(name = "entityIds", description = "实体ID列表，逗号分隔", required = true, example = "1,2,3")
    })
    @PreAuthorize("@ss.hasPermission('system:computed-field:update')")
    public CommonResult<Integer> batchRefreshEntityCache(
            @RequestParam("modelId") Long modelId,
            @RequestParam("entityIds") List<Long> entityIds) {
        return success(computedFieldService.batchRefreshEntityCache(modelId, entityIds));
    }

    @PostMapping("/warmup-field-cache")
    @Operation(summary = "预热指定字段的缓存")
    @Parameters({
            @Parameter(name = "modelId", description = "Model ID", required = true, example = "100"),
            @Parameter(name = "fieldCode", description = "字段编码", required = true, example = "fault_device_count"),
            @Parameter(name = "entityIds", description = "实体ID列表，逗号分隔", required = true, example = "1,2,3")
    })
    @PreAuthorize("@ss.hasPermission('system:computed-field:update')")
    public CommonResult<Integer> warmupFieldCache(
            @RequestParam("modelId") Long modelId,
            @RequestParam("fieldCode") String fieldCode,
            @RequestParam("entityIds") List<Long> entityIds) {
        return success(computedFieldService.warmupFieldCache(modelId, fieldCode, entityIds));
    }

    @PostMapping("/warmup-model-cache")
    @Operation(summary = "预热指定 Model 的所有缓存字段")
    @Parameters({
            @Parameter(name = "modelId", description = "Model ID", required = true, example = "100"),
            @Parameter(name = "entityIds", description = "实体ID列表，逗号分隔", required = true, example = "1,2,3")
    })
    @PreAuthorize("@ss.hasPermission('system:computed-field:update')")
    public CommonResult<Integer> warmupModelCache(
            @RequestParam("modelId") Long modelId,
            @RequestParam("entityIds") List<Long> entityIds) {
        return success(computedFieldService.warmupModelCache(modelId, entityIds));
    }

    @GetMapping("/cache-statistics")
    @Operation(summary = "获取缓存统计信息")
    @Parameter(name = "modelId", description = "Model ID（可选，为空时返回全局统计）", example = "100")
    @PreAuthorize("@ss.hasPermission('system:computed-field:query')")
    public CommonResult<ComputedFieldService.CacheStatisticsVO> getCacheStatistics(
            @RequestParam(value = "modelId", required = false) Long modelId) {
        return success(computedFieldService.getCacheStatistics(modelId));
    }

    @GetMapping("/is-cached")
    @Operation(summary = "检查缓存是否存在")
    @Parameters({
            @Parameter(name = "modelId", description = "Model ID", required = true, example = "100"),
            @Parameter(name = "fieldCode", description = "字段编码", required = true, example = "fault_device_count"),
            @Parameter(name = "entityId", description = "实体ID", required = true, example = "1")
    })
    @PreAuthorize("@ss.hasPermission('system:computed-field:query')")
    public CommonResult<Boolean> isCached(
            @RequestParam("modelId") Long modelId,
            @RequestParam("fieldCode") String fieldCode,
            @RequestParam("entityId") Long entityId) {
        return success(computedFieldService.isCached(modelId, fieldCode, entityId));
    }

    @GetMapping("/cache-ttl")
    @Operation(summary = "获取缓存剩余过期时间（秒）")
    @Parameters({
            @Parameter(name = "modelId", description = "Model ID", required = true, example = "100"),
            @Parameter(name = "fieldCode", description = "字段编码", required = true, example = "fault_device_count"),
            @Parameter(name = "entityId", description = "实体ID", required = true, example = "1")
    })
    @PreAuthorize("@ss.hasPermission('system:computed-field:query')")
    public CommonResult<Long> getCacheTtl(
            @RequestParam("modelId") Long modelId,
            @RequestParam("fieldCode") String fieldCode,
            @RequestParam("entityId") Long entityId) {
        return success(computedFieldService.getCacheTtl(modelId, fieldCode, entityId));
    }

    @PostMapping("/clear-all-cache")
    @Operation(summary = "清除所有计算字段缓存")
    @PreAuthorize("@ss.hasPermission('system:computed-field:update')")
    public CommonResult<Integer> clearAllCache() {
        return success(computedFieldService.clearAllCache());
    }

    // ========== 预计算管理接口 ==========

    @PostMapping("/trigger-precompute-async")
    @Operation(summary = "异步触发预计算")
    @Parameter(name = "entityId", description = "实体ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:computed-field:update')")
    public CommonResult<Boolean> triggerPrecomputeAsync(@RequestParam("entityId") Long entityId) {
        precomputeService.triggerPrecomputeAsync(entityId);
        return success(true);
    }

    @PostMapping("/trigger-field-precompute-async")
    @Operation(summary = "异步触发指定字段的预计算")
    @Parameters({
            @Parameter(name = "modelId", description = "Model ID", required = true, example = "100"),
            @Parameter(name = "fieldCode", description = "字段编码", required = true, example = "fault_device_count"),
            @Parameter(name = "entityId", description = "实体ID", required = true, example = "1")
    })
    @PreAuthorize("@ss.hasPermission('system:computed-field:update')")
    public CommonResult<Boolean> triggerFieldPrecomputeAsync(
            @RequestParam("modelId") Long modelId,
            @RequestParam("fieldCode") String fieldCode,
            @RequestParam("entityId") Long entityId) {
        precomputeService.triggerFieldPrecomputeAsync(modelId, fieldCode, entityId);
        return success(true);
    }

    @PostMapping("/trigger-batch-precompute-async")
    @Operation(summary = "异步批量触发预计算")
    @Parameters({
            @Parameter(name = "modelId", description = "Model ID", required = true, example = "100"),
            @Parameter(name = "entityIds", description = "实体ID列表，逗号分隔", required = true, example = "1,2,3")
    })
    @PreAuthorize("@ss.hasPermission('system:computed-field:update')")
    public CommonResult<Boolean> triggerBatchPrecomputeAsync(
            @RequestParam("modelId") Long modelId,
            @RequestParam("entityIds") List<Long> entityIds) {
        precomputeService.triggerBatchPrecomputeAsync(modelId, entityIds);
        return success(true);
    }

    @PostMapping("/recompute-all")
    @Operation(summary = "重新计算所有预计算字段")
    @Parameter(name = "modelId", description = "Model ID（可选，为空时重算所有）", example = "100")
    @PreAuthorize("@ss.hasPermission('system:computed-field:update')")
    public CommonResult<Integer> recomputeAll(
            @RequestParam(value = "modelId", required = false) Long modelId) {
        return success(precomputeService.recomputeAll(modelId));
    }

    @GetMapping("/precompute-statistics")
    @Operation(summary = "获取预计算统计信息")
    @PreAuthorize("@ss.hasPermission('system:computed-field:query')")
    public CommonResult<PrecomputeService.PrecomputeStatistics> getPrecomputeStatistics() {
        return success(precomputeService.getStatistics());
    }

    @GetMapping("/pending-task-count")
    @Operation(summary = "获取待处理的预计算任务数量")
    @PreAuthorize("@ss.hasPermission('system:computed-field:query')")
    public CommonResult<Integer> getPendingTaskCount() {
        return success(precomputeService.getPendingTaskCount());
    }

    @PostMapping("/clear-pending-tasks")
    @Operation(summary = "清空所有待处理的预计算任务")
    @PreAuthorize("@ss.hasPermission('system:computed-field:update')")
    public CommonResult<Integer> clearPendingTasks() {
        return success(precomputeService.clearPendingTasks());
    }

    // ========== 验证接口 ==========

    @PostMapping("/validate-formula")
    @Operation(summary = "验证公式表达式语法")
    @PreAuthorize("@ss.hasPermission('system:computed-field:query')")
    public CommonResult<FormulaValidationResultVO> validateFormula(
            @RequestBody FormulaValidationReqVO reqVO) {
        String error = computedFieldService.validateFormulaExpression(reqVO.formulaExpression());
        return success(new FormulaValidationResultVO(error == null, error));
    }

    @PostMapping("/detect-circular-dependency")
    @Operation(summary = "检测循环依赖")
    @PreAuthorize("@ss.hasPermission('system:computed-field:query')")
    public CommonResult<CircularDependencyResultVO> detectCircularDependency(
            @RequestBody CircularDependencyCheckReqVO reqVO) {
        String cyclePath = computedFieldService.detectCircularDependency(
                reqVO.modelId(), reqVO.fieldCode(), reqVO.formulaFields());
        return success(new CircularDependencyResultVO(cyclePath != null, cyclePath));
    }

    @GetMapping("/dependency-order")
    @Operation(summary = "获取字段的依赖顺序")
    @Parameters({
            @Parameter(name = "modelId", description = "Model ID", required = true, example = "100"),
            @Parameter(name = "fieldCodes", description = "字段编码列表，逗号分隔", required = true, 
                    example = "fault_rate,fault_device_count")
    })
    @PreAuthorize("@ss.hasPermission('system:computed-field:query')")
    public CommonResult<List<String>> getDependencyOrder(
            @RequestParam("modelId") Long modelId,
            @RequestParam("fieldCodes") List<String> fieldCodes) {
        return success(computedFieldService.getDependencyOrder(modelId, fieldCodes));
    }

    // ========== 内部 VO 类 ==========

    /**
     * 公式验证请求 VO
     */
    public record FormulaValidationReqVO(
            String formulaExpression
    ) {}

    /**
     * 公式验证结果 VO
     */
    public record FormulaValidationResultVO(
            boolean valid,
            String errorMessage
    ) {}

    /**
     * 循环依赖检测请求 VO
     */
    public record CircularDependencyCheckReqVO(
            Long modelId,
            String fieldCode,
            List<String> formulaFields
    ) {}

    /**
     * 循环依赖检测结果 VO
     */
    public record CircularDependencyResultVO(
            boolean hasCircularDependency,
            String cyclePath
    ) {}
}
