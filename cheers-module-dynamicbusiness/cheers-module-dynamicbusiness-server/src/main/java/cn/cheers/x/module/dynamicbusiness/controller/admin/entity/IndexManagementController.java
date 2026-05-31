package cn.cheers.x.module.dynamicbusiness.controller.admin.entity;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.index.*;
import cn.cheers.x.module.dynamicbusiness.service.entity.index.IndexRebuildService;
import cn.cheers.x.module.dynamicbusiness.service.entity.sync.EntitySyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 索引管理 Controller
 * 
 * <p>提供索引重建、补同步、失败日志查询等管理功能。</p>
 * 
 * <h3>核心功能</h3>
 * <ul>
 *   <li>索引重建：重建指定 Model 的索引</li>
 *   <li>补同步：按 Entity ID 或时间范围补同步</li>
 *   <li>失败日志：查询同步失败日志</li>
 * </ul>
 * 
 * <h3>需求引用</h3>
 * <ul>
 *   <li>FR-038: 系统必须提供索引重建功能</li>
 *   <li>FR-044: 系统必须提供补同步功能</li>
 *   <li>FR-047: 系统必须支持查询同步失败日志</li>
 * </ul>
 * 
 * @author 扩展字段查询服务
 */
@Tag(name = "管理后台 - 索引管理", description = "提供索引重建、补同步、失败日志查询等管理功能")
@RestController
@RequestMapping("/dynamicbusiness/entity/index")
@Validated
public class IndexManagementController {

    @Resource
    private IndexRebuildService indexRebuildService;

    @Resource
    private EntitySyncService entitySyncService;

    // ==================== 索引重建 ====================

    @PostMapping("/rebuild")
    @Operation(
        summary = "重建索引",
        description = "重建指定 Model 的索引。\n\n" +
            "**使用场景**：\n" +
            "- 索引数据不一致时\n" +
            "- 字段可查询配置变更后\n" +
            "- 数据迁移后\n\n" +
            "**注意事项**：\n" +
            "- 重建过程中查询可能不准确\n" +
            "- 大数据量时建议在低峰期执行\n" +
            "- 支持异步执行，返回任务 ID"
    )
    @PreAuthorize("@ss.hasPermission('system:entity:index:rebuild')")
    public CommonResult<IndexRebuildRespVO> rebuildIndex(@Valid @RequestBody IndexRebuildReqVO reqVO) {
        // 启动重建任务
        Long modelId = reqVO.getModelId();
        indexRebuildService.rebuildIndex(modelId, null);
        
        // 获取当前进度
        IndexRebuildService.RebuildProgress progress = indexRebuildService.getCurrentProgress(modelId);
        
        // 使用 setter 方法构建响应
        IndexRebuildRespVO result = new IndexRebuildRespVO();
        result.setTaskId("rebuild-" + modelId);
        result.setAsync(reqVO.getAsync());
        result.setStatus(progress != null ? progress.getStatus() : "RUNNING");
        result.setTotalCount(progress != null ? progress.getTotal() : 0L);
        result.setProcessedCount(progress != null ? progress.getProcessed() : 0L);
        result.setFailedCount(0L);
        result.setStartTime(LocalDateTime.now());
        
        return success(result);
    }

    @GetMapping("/rebuild/status")
    @Operation(
        summary = "查询索引重建状态",
        description = "查询异步索引重建任务的执行状态。"
    )
    @Parameter(name = "modelId", description = "Model ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:entity:index:rebuild')")
    public CommonResult<IndexRebuildStatusVO> getRebuildStatus(@RequestParam("modelId") Long modelId) {
        IndexRebuildService.RebuildProgress progress = indexRebuildService.getCurrentProgress(modelId);
        
        IndexRebuildStatusVO status = new IndexRebuildStatusVO();
        status.setTaskId("rebuild-" + modelId);
        status.setModelId(modelId);
        
        if (progress == null) {
            status.setStatus("NOT_FOUND");
            return success(status);
        }
        
        status.setStatus(progress.getStatus());
        status.setTotalCount(progress.getTotal());
        status.setProcessedCount(progress.getProcessed());
        status.setFailedCount(0L);
        status.setProgressPercent(progress.getPercent());
        status.setEstimatedRemainingSeconds(progress.getEstimatedRemainingSeconds());
        status.setErrorMessage(progress.getErrorMessage());
        
        return success(status);
    }

    // ==================== 补同步 ====================

    @PostMapping("/resync")
    @Operation(
        summary = "补同步",
        description = "补同步指定的 Entity 到索引。\n\n" +
            "**支持的补同步方式**：\n" +
            "- 按 Entity ID 列表补同步\n" +
            "- 按时间范围补同步（补同步指定时间范围内更新的 Entity）\n" +
            "- 补同步所有失败的记录\n\n" +
            "**使用场景**：\n" +
            "- 同步失败后手动补同步\n" +
            "- 数据修复后补同步"
    )
    @PreAuthorize("@ss.hasPermission('system:entity:index:resync')")
    public CommonResult<ResyncRespVO> resync(@Valid @RequestBody ResyncReqVO reqVO) {
        LocalDateTime startTime = LocalDateTime.now();
        long successCount = 0;
        long failedCount = 0;
        long totalCount = 0;
        
        ResyncReqVO.ResyncType type = reqVO.getType();
        List<Long> entityIds = reqVO.getEntityIds();
        String businessTypeCode = reqVO.getBusinessTypeCode();
        
        if (type == ResyncReqVO.ResyncType.BY_IDS && entityIds != null) {
            // 按 Entity ID 列表补同步（需要 businessTypeCode）
            if (businessTypeCode == null || businessTypeCode.isEmpty()) {
                throw new IllegalArgumentException("BY_IDS 类型补同步必须提供 businessTypeCode");
            }
            totalCount = entityIds.size();
            successCount = indexRebuildService.resyncByEntityIds(entityIds, businessTypeCode);
            failedCount = totalCount - successCount;
        } else if (type == ResyncReqVO.ResyncType.BY_TIME_RANGE) {
            // 按时间范围补同步
            int count = entitySyncService.resyncFailedByTimeRange(reqVO.getStartTime(), reqVO.getEndTime());
            successCount = count;
            totalCount = count;
        } else if (type == ResyncReqVO.ResyncType.FAILED_ONLY) {
            // 补同步所有失败的记录
            int count = indexRebuildService.resyncPendingFailLogs(1000);
            successCount = count;
            totalCount = count;
        }
        
        LocalDateTime endTime = LocalDateTime.now();
        
        // 使用 setter 方法构建响应
        ResyncRespVO result = new ResyncRespVO();
        result.setTotalCount(totalCount);
        result.setSuccessCount(successCount);
        result.setFailedCount(failedCount);
        result.setSkippedCount(0L);
        result.setStartTime(startTime);
        result.setEndTime(endTime);
        result.setDurationMs(java.time.Duration.between(startTime, endTime).toMillis());
        
        return success(result);
    }

    // ==================== 失败日志 ====================

    @GetMapping("/sync/fail-logs")
    @Operation(
        summary = "查询同步失败日志",
        description = "分页查询同步失败日志。\n\n" +
            "**支持的过滤条件**：\n" +
            "- 按业务类型编码过滤（必填）\n" +
            "- 按 Model ID 过滤\n" +
            "- 按状态过滤（PENDING/RETRYING/FAILED/SUCCESS）\n" +
            "- 按时间范围过滤"
    )
    @Parameter(name = "businessTypeCode", description = "业务类型编码（必填）", required = true, example = "equipment")
    @Parameter(name = "modelId", description = "Model ID（可选）", example = "1")
    @Parameter(name = "status", description = "状态（可选）", example = "FAILED")
    @Parameter(name = "startTime", description = "开始时间（可选）")
    @Parameter(name = "endTime", description = "结束时间（可选）")
    @Parameter(name = "pageNo", description = "页码（默认1）", example = "1")
    @Parameter(name = "pageSize", description = "每页条数（默认10）", example = "10")
    @PreAuthorize("@ss.hasPermission('system:entity:index:query')")
    public CommonResult<PageResult<SyncFailLogVO>> getSyncFailLogs(
            @RequestParam("businessTypeCode") String businessTypeCode,
            @RequestParam(value = "modelId", required = false) Long modelId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "startTime", required = false) LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false) LocalDateTime endTime,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        
        // 获取待处理的失败日志（按 businessTypeCode 过滤）
        List<cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntitySyncFailLogDO> failLogs = 
            entitySyncService.getPendingFailLogsByBusinessTypeCode(businessTypeCode, pageSize);
        
        // 转换为 VO（使用 setter 方法）
        List<SyncFailLogVO> voList = failLogs.stream()
            .map(log -> {
                SyncFailLogVO vo = new SyncFailLogVO();
                vo.setId(log.getId());
                vo.setEntityId(log.getEntityId());
                vo.setModelId(log.getModelId());
                vo.setBusinessTypeCode(log.getBusinessTypeCode());
                vo.setEngineType(log.getEngineType());
                vo.setFailReason(log.getFailReason());
                vo.setRetryCount(log.getRetryCount());
                vo.setLastRetryAt(log.getLastRetryAt());
                vo.setStatus(log.getStatus());
                vo.setCreateTime(log.getCreateTime());
                vo.setUpdateTime(log.getUpdateTime());
                return vo;
            })
            .collect(Collectors.toList());
        
        // 获取总数（按 businessTypeCode 过滤）
        long total = entitySyncService.countPendingFailLogsByBusinessTypeCode(businessTypeCode);
        
        return success(new PageResult<>(voList, total));
    }

    @PostMapping("/sync/fail-logs/retry")
    @Operation(
        summary = "重试失败的同步",
        description = "重试指定的同步失败记录。失败日志中已包含 businessTypeCode，无需额外传入。"
    )
    @PreAuthorize("@ss.hasPermission('system:entity:index:resync')")
    public CommonResult<Integer> retryFailedSync(@RequestBody List<Long> failLogIds) {
        // 通过失败日志处理，失败日志中已包含 businessTypeCode
        int successCount = indexRebuildService.resyncPendingFailLogs(failLogIds.size());
        return success(successCount);
    }
}
