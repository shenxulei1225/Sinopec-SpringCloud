package cn.cheers.x.module.dynamicbusiness.service.entity.query.dto;

import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.enums.LogicType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 字段查询请求
 *
 * 封装扩展字段查询的完整请求参数,包含模型ID、查询条件、排序和分页
 *
 * @author 系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldQueryRequest {

    /**
     * 模型ID（必填）
     * 指定要查询的 Model
     */
    @NotNull(message = "模型ID不能为空")
    private Long modelId;

    /**
     * 存储类型编码（必填）：路由到对应 {@code ent_*} 物理表，禁止缺省回落。
     */
    @NotNull(message = "entityTypeCode不能为空")
    private String entityTypeCode;

    /**
     * 查询条件列表
     * 最多支持 10 个条件
     */
    @Valid
    @Size(max = 10, message = "查询条件最多支持10个")
    @Builder.Default
    private List<FieldCondition> conditions = new ArrayList<>();

    /**
     * 条件组合逻辑
     * 默认为 AND（所有条件都必须满足）
     */
    @Builder.Default
    private LogicType logic = LogicType.AND;

    /**
     * 排序条件列表
     * 支持多字段排序,按列表顺序优先级排序
     */
    @Valid
    @Builder.Default
    private List<FieldSort> sorts = new ArrayList<>();

    /**
     * 分页参数
     * 如果不指定,默认返回第一页,每页 10 条
     */
    @Valid
    private PageParam pageParam;

    // ==================== 便捷方法 ====================

    /**
     * 添加查询条件
     *
     * @param condition 查询条件
     * @return 当前请求对象（支持链式调用）
     */
    public FieldQueryRequest addCondition(FieldCondition condition) {
        if (this.conditions == null) {
            this.conditions = new ArrayList<>();
        }
        this.conditions.add(condition);
        return this;
    }

    /**
     * 添加排序条件
     *
     * @param sort 排序条件
     * @return 当前请求对象（支持链式调用）
     */
    public FieldQueryRequest addSort(FieldSort sort) {
        if (this.sorts == null) {
            this.sorts = new ArrayList<>();
        }
        this.sorts.add(sort);
        return this;
    }

    /**
     * 设置为 AND 逻辑
     *
     * @return 当前请求对象（支持链式调用）
     */
    public FieldQueryRequest and() {
        this.logic = LogicType.AND;
        return this;
    }

    /**
     * 设置为 OR 逻辑
     *
     * @return 当前请求对象（支持链式调用）
     */
    public FieldQueryRequest or() {
        this.logic = LogicType.OR;
        return this;
    }

    /**
     * 判断是否有查询条件
     *
     * @return 是否有查询条件
     */
    public boolean hasConditions() {
        return conditions != null && !conditions.isEmpty();
    }

    /**
     * 判断是否有排序条件
     *
     * @return 是否有排序条件
     */
    public boolean hasSorts() {
        return sorts != null && !sorts.isEmpty();
    }

    /**
     * 获取分页参数,如果未设置则返回默认值
     *
     * @return 分页参数
     */
    public PageParam getPageParamOrDefault() {
        if (pageParam == null) {
            pageParam = new PageParam();
        }
        return pageParam;
    }

    // ==================== 静态构建方法 ====================

    /**
     * 创建查询请求构建器
     *
     * @param modelId 模型ID
     * @return 构建器
     */
    public static FieldQueryRequestBuilder forModel(Long modelId) {
        return FieldQueryRequest.builder().modelId(modelId);
    }
}
