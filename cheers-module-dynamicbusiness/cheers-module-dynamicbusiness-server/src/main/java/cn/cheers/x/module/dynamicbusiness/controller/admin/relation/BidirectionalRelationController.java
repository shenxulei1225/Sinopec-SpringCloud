package cn.cheers.x.module.dynamicbusiness.controller.admin.relation;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.bidirectional.*;
import cn.cheers.x.module.dynamicbusiness.service.relation.BidirectionalRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 双向关联查询 Controller
 * 
 * <p>提供实体的双向关联查询功能,支持：</p>
 * <ul>
 *   <li>反向关联查询：获取引用当前实体的其他实体</li>
 *   <li>关联统计：按关联字段分组统计</li>
 *   <li>关联发现：自动发现所有关联关系</li>
 * </ul>
 * 
 * <h3>需求</h3>
 * <ul>
 *   <li>FR-BDA-090: 系统必须自动发现所有关联关系</li>
 *   <li>FR-BDA-091: 系统必须提供反向查询 API</li>
 *   <li>FR-BDA-092: 系统必须提供关联统计 API</li>
 *   <li>FR-BDA-093: 双向关联查询无需用户额外配置</li>
 * </ul>
 * 
 * @author yudao
 */
@Tag(name = "管理后台 - 双向关联查询")
@RestController
@RequestMapping("/system")
@Validated
public class BidirectionalRelationController {

    @Resource
    private BidirectionalRelationService bidirectionalRelationService;

    // ==================== 反向关联查询 API ====================

    @GetMapping("/entity/reverse-relations")
    @Operation(
        summary = "获取实体的反向关联",
        description = "获取引用当前实体的所有其他实体,按 Model 分组返回。系统自动发现所有关联关系,无需用户额外配置。"
    )
    @Parameter(name = "id", description = "实体 ID", required = true, example = "123")
    @Parameter(name = "entityTypeCode", description = "业务类型编码", required = false, example = "tunnel")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<RelatedEntityVO>> getReverseRelations(
            @RequestParam("id") Long id,
            @RequestParam(value = "entityTypeCode", required = false) String entityTypeCode) {
        return success(bidirectionalRelationService.getReverseRelations(id, entityTypeCode));
    }

    @GetMapping("/entity/reverse-relations-by-model")
    @Operation(
        summary = "获取实体的反向关联(按 Model 过滤)",
        description = "获取指定 Model 中引用当前实体的所有记录,支持分页。"
    )
    @Parameter(name = "id", description = "实体 ID", required = true, example = "123")
    @Parameter(name = "modelCode", description = "Model 编码", required = true, example = "production_task")
    @Parameter(name = "entityTypeCode", description = "业务类型编码", required = false, example = "tunnel")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<PageResult<EntitySimpleVO>> getReverseRelationsByModel(
            @RequestParam("id") Long id,
            @RequestParam("modelCode") String modelCode,
            @RequestParam(value = "entityTypeCode", required = false) String entityTypeCode,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        return success(bidirectionalRelationService.getReverseRelationsByModel(id, entityTypeCode, modelCode, pageParam));
    }

    @GetMapping("/entity/forward-relations")
    @Operation(
        summary = "获取实体的正向关联",
        description = "获取当前实体引用的所有其他实体。"
    )
    @Parameter(name = "id", description = "实体 ID", required = true, example = "123")
    @Parameter(name = "entityTypeCode", description = "业务类型编码", required = false, example = "tunnel")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<RelatedEntityVO>> getForwardRelations(
            @RequestParam("id") Long id,
            @RequestParam(value = "entityTypeCode", required = false) String entityTypeCode) {
        return success(bidirectionalRelationService.getForwardRelations(id, entityTypeCode));
    }

    @GetMapping("/entity/relation-info")
    @Operation(
        summary = "获取实体的完整关联信息",
        description = "一次性获取实体的所有关联信息,包括正向关联、反向关联和统计信息。"
    )
    @Parameter(name = "id", description = "实体 ID", required = true, example = "123")
    @Parameter(name = "entityTypeCode", description = "业务类型编码", required = false, example = "tunnel")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<EntityRelationInfoVO> getEntityRelationInfo(
            @RequestParam("id") Long id,
            @RequestParam(value = "entityTypeCode", required = false) String entityTypeCode) {
        return success(bidirectionalRelationService.getEntityRelationInfo(id, entityTypeCode));
    }

    // ==================== 关联统计 API ====================

    @GetMapping("/entity/relation-statistics")
    @Operation(
        summary = "获取实体的关联统计",
        description = "统计引用当前实体的记录数量，按 Model 分组。"
    )
    @Parameter(name = "id", description = "实体 ID", required = true, example = "123")
    @Parameter(name = "entityTypeCode", description = "业务类型编码", required = false, example = "tunnel")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<RelationStatisticsVO> getRelationStatistics(
            @RequestParam("id") Long id,
            @RequestParam(value = "entityTypeCode", required = false) String entityTypeCode) {
        return success(bidirectionalRelationService.getRelationStatistics(id, entityTypeCode));
    }

    @PostMapping("/entity/aggregate")
    @Operation(
        summary = "按关联字段分组统计",
        description = "对指定 Model 的数据按关联字段进行聚合统计。支持的聚合类型：COUNT、SUM、AVG、MAX、MIN"
    )
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<AggregateResultVO>> aggregateByRelation(
            @Valid @RequestBody AggregateQueryReqVO reqVO) {
        return success(bidirectionalRelationService.aggregateByRelation(reqVO));
    }

    @PostMapping("/entity/batch-reverse-relation-counts")
    @Operation(
        summary = "批量获取实体的反向关联数量",
        description = "批量查询多个实体的反向关联数量,用于列表展示。"
    )
    @Parameter(name = "entityTypeCode", description = "业务类型编码", required = false, example = "tunnel")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<Map<Long, Long>> batchGetReverseRelationCounts(
            @RequestParam(value = "entityTypeCode") String entityTypeCode,
            @RequestBody List<Long> entityIds) {
        return success(bidirectionalRelationService.batchGetReverseRelationCounts(entityTypeCode, entityIds));
    }

    // ==================== 关联发现 API ====================

    @GetMapping("/relation/discover")
    @Operation(
        summary = "发现 Model 的所有关联关系",
        description = "扫描所有 Model 的 ENTITY_REF 类型字段,找出引用指定 Model 的字段。系统自动发现关联关系,无需用户额外配置。"
    )
    @Parameter(name = "modelCode", description = "Model 编码", required = true, example = "production_plan")
    @Parameter(name = "useCache", description = "是否使用缓存", example = "true")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<RelationDiscoveryVO>> discoverRelations(
            @RequestParam("modelCode") String modelCode,
            @RequestParam(value = "useCache", defaultValue = "true") Boolean useCache) {
        return success(bidirectionalRelationService.discoverRelations(modelCode, useCache));
    }

    @DeleteMapping("/relation/discover/cache")
    @Operation(
        summary = "清除关联发现缓存",
        description = "当字段定义变更时调用,清除相关缓存。如果不指定 modelCode,则清除所有缓存。"
    )
    @Parameter(name = "modelCode", description = "Model 编码(可选,不指定则清除所有)", example = "production_plan")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Boolean> clearDiscoveryCache(
            @RequestParam(value = "modelCode", required = false) String modelCode) {
        bidirectionalRelationService.clearDiscoveryCache(modelCode);
        return success(true);
    }
}
