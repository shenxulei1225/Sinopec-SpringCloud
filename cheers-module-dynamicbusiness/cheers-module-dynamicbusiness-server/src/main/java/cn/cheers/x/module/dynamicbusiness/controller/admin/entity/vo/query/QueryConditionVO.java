package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.query;

import cn.cheers.x.module.dynamicbusiness.service.entity.query.enums.Operator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 查询条件 VO
 * 
 * <p>封装单个字段的查询条件，用于 REST API 请求。</p>
 * 
 * @author 扩展字段查询服务
 */
@Schema(description = "管理后台 - 查询条件")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryConditionVO {

    /**
     * 字段编码（必填）
     * 
     * <p>对应 FieldDefinition 中的 code。
     * 系统会验证字段是否存在且可查询（is_searchable=true）。</p>
     */
    @Schema(description = "字段编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "power")
    @NotBlank(message = "字段编码不能为空")
    private String fieldCode;

    /**
     * 操作符（必填）
     * 
     * <p>支持的操作符：</p>
     * <ul>
     *   <li>EQ: 等于</li>
     *   <li>NE: 不等于</li>
     *   <li>GT: 大于</li>
     *   <li>GE: 大于等于</li>
     *   <li>LT: 小于</li>
     *   <li>LE: 小于等于</li>
     *   <li>BETWEEN: 区间</li>
     *   <li>LIKE: 模糊匹配</li>
     *   <li>IN: 包含</li>
     *   <li>NOT_IN: 不包含</li>
     *   <li>IS_NULL: 为空</li>
     *   <li>IS_NOT_NULL: 不为空</li>
     * </ul>
     */
    @Schema(description = "操作符", requiredMode = Schema.RequiredMode.REQUIRED, example = "EQ")
    @NotNull(message = "操作符不能为空")
    private Operator operator;

    /**
     * 查询值
     * 
     * <p>根据操作符类型：</p>
     * <ul>
     *   <li>EQ、NE、GT、GE、LT、LE、LIKE：单个值</li>
     *   <li>IN、NOT_IN：值列表（JSON 数组格式）</li>
     *   <li>BETWEEN：第一个边界值</li>
     *   <li>IS_NULL、IS_NOT_NULL：不需要值</li>
     * </ul>
     */
    @Schema(description = "查询值", example = "50")
    private Object value;

    /**
     * 第二个查询值
     * 
     * <p>仅用于 BETWEEN 操作符，表示区间的第二个边界值。</p>
     */
    @Schema(description = "第二个查询值（用于 BETWEEN）", example = "100")
    private Object value2;

    // ==================== 便捷构造方法 ====================

    /**
     * 创建等值查询条件
     */
    public static QueryConditionVO eq(String fieldCode, Object value) {
        return QueryConditionVO.builder()
                .fieldCode(fieldCode)
                .operator(Operator.EQ)
                .value(value)
                .build();
    }

    /**
     * 创建大于查询条件
     */
    public static QueryConditionVO gt(String fieldCode, Object value) {
        return QueryConditionVO.builder()
                .fieldCode(fieldCode)
                .operator(Operator.GT)
                .value(value)
                .build();
    }

    /**
     * 创建区间查询条件
     */
    public static QueryConditionVO between(String fieldCode, Object value1, Object value2) {
        return QueryConditionVO.builder()
                .fieldCode(fieldCode)
                .operator(Operator.BETWEEN)
                .value(value1)
                .value2(value2)
                .build();
    }

    /**
     * 创建模糊查询条件
     */
    public static QueryConditionVO like(String fieldCode, String value) {
        return QueryConditionVO.builder()
                .fieldCode(fieldCode)
                .operator(Operator.LIKE)
                .value(value)
                .build();
    }
}
