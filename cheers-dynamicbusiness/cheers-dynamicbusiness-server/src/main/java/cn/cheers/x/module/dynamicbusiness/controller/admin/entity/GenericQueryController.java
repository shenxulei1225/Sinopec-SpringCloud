package cn.cheers.x.module.dynamicbusiness.controller.admin.entity;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.query.*;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.GenericEntityQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 通用 Entity 查询 Controller
 * 
 * <p>提供元数据驱动的通用查询 API，支持通过 modelCode 动态指定查询的 Model，
 * 实现零代码查询能力。</p>
 * 
 * <h3>核心功能</h3>
 * <ul>
 *   <li>通用条件查询：POST /api/entity/query</li>
 *   <li>通用聚合查询：POST /api/entity/aggregate</li>
 *   <li>获取可查询字段：GET /api/entity/searchable-fields</li>
 * </ul>
 * 
 * <h3>架构模式</h3>
 * <p>本 Controller 实现了架构模式 C（Category-Model-Entity）的核心查询能力：</p>
 * <ul>
 *   <li>前端通过 modelCode 指定要查询的 Model</li>
 *   <li>前端通过 getSearchableFields 获取可查询字段列表，动态生成查询表单</li>
 *   <li>前端提交查询条件，后端执行查询并返回结果</li>
 * </ul>
 * 
 * <h3>需求引用</h3>
 * <ul>
 *   <li>FR-051: 系统必须提供元数据驱动的通用查询服务，无需为每个 Model 编写代码</li>
 *   <li>FR-052: 系统必须支持通过 modelCode 参数动态指定查询的 Model</li>
 *   <li>FR-053: 系统必须提供获取 Model 可查询字段列表的 API，供前端动态生成查询表单</li>
 *   <li>FR-058: 模式C（Category-Model-Entity）：系统必须提供按 Model 查询 Entity 的通用 API</li>
 * </ul>
 * 
 * @author 扩展字段查询服务
 */
@Tag(name = "管理后台 - 通用 Entity 查询", description = "提供元数据驱动的通用查询能力，支持通过 modelCode 动态指定查询的 Model，实现零代码查询")
@RestController
@RequestMapping("/dynamicbusiness/entity/generic")
@Validated
public class GenericQueryController {

    @Resource
    private GenericEntityQueryService genericEntityQueryService;

    @PostMapping("/query")
    @Operation(
        summary = "通用条件查询",
        description = "根据 modelCode 和查询条件查询 Entity 列表。\n\n" +
            "**使用流程**：\n" +
            "1. 调用 GET /searchable-fields 获取可查询字段列表\n" +
            "2. 前端根据字段列表动态生成查询表单\n" +
            "3. 用户填写查询条件后，调用此接口执行查询\n\n" +
            "**查询条件**：\n" +
            "- 最多支持 10 个条件\n" +
            "- 支持 AND/OR 逻辑组合\n" +
            "- 支持多种操作符：EQ、NE、GT、GE、LT、LE、BETWEEN、LIKE、IN、NOT_IN、IS_NULL、IS_NOT_NULL\n\n" +
            "**分页**：\n" +
            "- 默认返回第一页，每页 10 条\n" +
            "- 单次查询最多返回 1000 条记录"
    )
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<PageResult<EntityRespVO>> query(@Valid @RequestBody GenericQueryRequest request) {
        return success(genericEntityQueryService.query(request));
    }

    @PostMapping("/aggregate")
    @Operation(
        summary = "通用聚合查询",
        description = "根据 modelCode 和聚合参数进行统计聚合。\n\n" +
            "**支持的聚合类型**：\n" +
            "- COUNT: 计数统计\n" +
            "- SUM: 求和统计（需指定数值字段）\n" +
            "- AVG: 平均值统计（需指定数值字段）\n" +
            "- MAX: 最大值统计（需指定数值字段）\n" +
            "- MIN: 最小值统计（需指定数值字段）\n\n" +
            "**分组统计**：\n" +
            "- 可选指定 groupByFieldCode 进行分组统计\n" +
            "- 例如：按品牌分组统计设备数量\n\n" +
            "**过滤条件**：\n" +
            "- 可选指定 conditions 在聚合前过滤数据"
    )
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<AggregateResultVO> aggregate(@Valid @RequestBody GenericAggregateRequest request) {
        return success(genericEntityQueryService.aggregate(request));
    }

    @PostMapping("/count")
    @Operation(
        summary = "通用计数查询",
        description = "统计满足条件的 Entity 数量。\n\n" +
            "**使用场景**：\n" +
            "- 快速获取满足条件的记录数\n" +
            "- 用于分页查询前获取总数\n" +
            "- 用于统计报表"
    )
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<Long> count(@Valid @RequestBody GenericQueryRequest request) {
        return success(genericEntityQueryService.count(request));
    }

    @GetMapping("/searchable-fields")
    @Operation(
        summary = "获取可查询字段列表",
        description = "返回指定 Model 下所有可查询的字段信息，供前端动态生成查询表单。\n\n" +
            "**返回信息**：\n" +
            "- 字段编码、名称、类型\n" +
            "- 支持的操作符列表\n" +
            "- 是否可排序\n" +
            "- 选项列表（SELECT 类型）\n" +
            "- 引用 Model 列表（ENTITY_REF 类型）\n\n" +
            "**前端使用**：\n" +
            "- 根据字段类型选择合适的输入组件\n" +
            "- 根据支持的操作符生成操作符下拉选项\n" +
            "- 根据选项列表生成下拉选择框"
    )
    @Parameter(name = "modelCode", description = "Model 编码", required = true, example = "air-conditioner")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<SearchableFieldRespVO>> getSearchableFields(@RequestParam("modelCode") String modelCode) {
        return success(genericEntityQueryService.getSearchableFields(modelCode));
    }
}
