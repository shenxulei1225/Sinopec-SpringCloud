package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.query;

import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.enums.LogicType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用查询请求 VO
 * 
 * <p>用于 REST API 的通用 Entity 查询请求，支持通过 modelCode 指定查询的 Model，
 * 实现零代码查询能力。</p>
 * 
 * <h3>使用场景</h3>
 * <ul>
 *   <li>前端动态生成查询表单后提交查询请求</li>
 *   <li>通过 modelCode 参数动态指定查询的 Model</li>
 *   <li>支持扩展字段的条件查询、排序、分页</li>
 * </ul>
 * 
 * <h3>需求引用</h3>
 * <ul>
 *   <li>FR-051: 系统必须提供元数据驱动的通用查询服务，无需为每个 Model 编写代码</li>
 *   <li>FR-052: 系统必须支持通过 modelCode 参数动态指定查询的 Model</li>
 *   <li>FR-058: 模式C（Category-Model-Entity）：系统必须提供按 Model 查询 Entity 的通用 API</li>
 * </ul>
 * 
 * @author 扩展字段查询服务
 */
@Schema(description = "管理后台 - 通用 Entity 查询请求")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericQueryRequest {

    /**
     * Model 编码（必填）
     * 
     * <p>指定要查询的 Model，系统会根据 modelCode 获取 Model 的元数据信息，
     * 包括可查询字段列表、字段类型等。</p>
     */
    @Schema(description = "Model 编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "air-conditioner")
    @NotBlank(message = "Model 编码不能为空")
    private String modelCode;

    /**
     * 查询条件列表
     * 
     * <p>最多支持 10 个条件。每个条件包含字段编码、操作符和值。
     * 系统会验证字段是否可查询（is_searchable=true）。</p>
     */
    @Schema(description = "查询条件列表")
    @Valid
    @Size(max = 10, message = "查询条件最多支持10个")
    @Builder.Default
    private List<QueryConditionVO> conditions = new ArrayList<>();

    /**
     * 条件组合逻辑
     * 
     * <p>默认为 AND（所有条件都必须满足）。
     * 可选值：AND、OR。</p>
     */
    @Schema(description = "条件组合逻辑", example = "AND")
    @Builder.Default
    private LogicType logic = LogicType.AND;

    /**
     * 排序条件列表
     * 
     * <p>支持多字段排序，按列表顺序优先级排序。
     * 系统会验证排序字段是否可查询。</p>
     */
    @Schema(description = "排序条件列表")
    @Valid
    @Builder.Default
    private List<QuerySortVO> sorts = new ArrayList<>();

    /**
     * 分页参数
     * 
     * <p>如果不指定，默认返回第一页，每页 10 条。
     * 单次查询最多返回 1000 条记录。</p>
     */
    @Schema(description = "分页参数")
    @Valid
    private PageParam pageParam;

    // ==================== 便捷方法 ====================

    /**
     * 添加查询条件
     */
    public GenericQueryRequest addCondition(QueryConditionVO condition) {
        if (this.conditions == null) {
            this.conditions = new ArrayList<>();
        }
        this.conditions.add(condition);
        return this;
    }

    /**
     * 添加排序条件
     */
    public GenericQueryRequest addSort(QuerySortVO sort) {
        if (this.sorts == null) {
            this.sorts = new ArrayList<>();
        }
        this.sorts.add(sort);
        return this;
    }

    /**
     * 判断是否有查询条件
     */
    public boolean hasConditions() {
        return conditions != null && !conditions.isEmpty();
    }

    /**
     * 判断是否有排序条件
     */
    public boolean hasSorts() {
        return sorts != null && !sorts.isEmpty();
    }

    /**
     * 获取分页参数，如果未设置则返回默认值
     */
    public PageParam getPageParamOrDefault() {
        if (pageParam == null) {
            pageParam = new PageParam();
        }
        return pageParam;
    }
}
