package cn.cheers.x.module.dynamicbusiness.controller.admin.entity;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.bidirectional.RelatedEntityVO;
import cn.cheers.x.module.dynamicbusiness.service.relation.BidirectionalRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 关联 Entity 查询 Controller
 * 
 * <p>提供反向查询能力，查询所有关联到指定 Entity 的其他 Entity。</p>
 * 
 * <h3>业务场景</h3>
 * <p>例如：查询某个"计划"被哪些"任务"关联</p>
 * <ul>
 *   <li>计划 Entity ID = 123</li>
 *   <li>任务 Model 有 plan_id 字段关联到计划</li>
 *   <li>反向查询返回所有 plan_id = 123 的任务</li>
 * </ul>
 * 
 * <h3>需求引用</h3>
 * <ul>
 *   <li>FR-078: 系统必须支持反向查询（查询关联到指定 Entity 的所有 Entity）</li>
 *   <li>FR-086: 系统必须提供反向查询 API（GET /api/entity/{id}/related）</li>
 *   <li>BR-REL-005: 反向查询通过 GET /api/entity/{id}/related API 提供</li>
 * </ul>
 * 
 * @author yudao
 */
@Tag(name = "管理后台 - 关联 Entity 查询", description = "提供反向查询能力，查询所有关联到指定 Entity 的其他 Entity")
@RestController
@RequestMapping("/dynamicbusiness/entity")
@Validated
public class RelatedEntityController {

    @Resource
    private BidirectionalRelationService bidirectionalRelationService;

    @GetMapping("/{id}/related")
    @Operation(
        summary = "反向查询关联 Entity",
        description = "查询所有关联到指定 Entity 的其他 Entity。\n\n" +
            "**使用场景**：\n" +
            "- 查询某个\"计划\"被哪些\"任务\"关联\n" +
            "- 查询某个\"设备\"被哪些\"工单\"引用\n\n" +
            "**实现原理**：\n" +
            "1. 获取目标 Entity 的 Model 信息\n" +
            "2. 查询所有以该 Model 为目标的关联关系\n" +
            "3. 对于每个关联关系，查询源 Model 下关联字段值等于目标 Entity ID 的 Entity\n\n" +
            "**过滤条件**：\n" +
            "- 可选指定 modelCode 只返回指定 Model 的关联 Entity\n" +
            "- entityTypeCode 是必填参数，用于路由到正确的存储策略"
    )
    @Parameter(name = "id", description = "目标 Entity ID", required = true, example = "123")
    @Parameter(name = "modelCode", description = "过滤条件：只返回指定 Model 的关联 Entity（可选）", example = "task")
    @Parameter(name = "entityTypeCode", description = "目标 Entity 的业务类型编码（必填，用于路由到正确的存储策略）", required = true, example = "personnel")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<RelatedEntityVO>> getRelatedEntities(
            @PathVariable("id") Long id,
            @RequestParam(value = "modelCode", required = false) String modelCode,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        if (modelCode != null && !modelCode.isBlank()) {
            // 仅返回指定 model 的反向关联统计，保持接口语义兼容
            List<RelatedEntityVO> all = bidirectionalRelationService.getReverseRelations(id, entityTypeCode);
            return success(all.stream().filter(item -> modelCode.equals(item.getModelCode())).toList());
        }
        return success(bidirectionalRelationService.getReverseRelations(id, entityTypeCode));
    }

    @GetMapping("/{id}/related/count")
    @Operation(
        summary = "统计关联 Entity 数量",
        description = "统计所有关联到指定 Entity 的 Entity 数量。\n\n" +
            "**使用场景**：\n" +
            "- 在删除 Entity 前检查是否有关联\n" +
            "- 显示关联数量统计信息\n\n" +
            "**重要**：entityTypeCode 是必填参数，用于路由到正确的存储策略"
    )
    @Parameter(name = "id", description = "目标 Entity ID", required = true, example = "123")
    @Parameter(name = "modelCode", description = "过滤条件：只统计指定 Model 的关联 Entity（可选）", example = "task")
    @Parameter(name = "entityTypeCode", description = "目标 Entity 的业务类型编码（必填，用于路由到正确的存储策略）", required = true, example = "personnel")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<Long> countRelatedEntities(
            @PathVariable("id") Long id,
            @RequestParam(value = "modelCode", required = false) String modelCode,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        if (modelCode != null && !modelCode.isBlank()) {
            List<RelatedEntityVO> all = bidirectionalRelationService.getReverseRelations(id, entityTypeCode);
            long count = all.stream()
                    .filter(item -> modelCode.equals(item.getModelCode()))
                    .mapToLong(item -> item.getCount() == null ? 0L : item.getCount())
                    .sum();
            return success(count);
        }
        return success(bidirectionalRelationService.getRelationStatistics(id, entityTypeCode).getReverseRelationCount());
    }
}
