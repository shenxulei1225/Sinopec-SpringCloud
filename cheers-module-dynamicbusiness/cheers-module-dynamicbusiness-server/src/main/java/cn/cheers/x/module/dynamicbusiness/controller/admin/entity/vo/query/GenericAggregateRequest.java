package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.query;

import cn.cheers.x.module.dynamicbusiness.service.entity.query.enums.AggregateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用聚合请求 VO
 * 
 * <p>用于 REST API 的通用 Entity 聚合请求，支持通过 modelCode 指定聚合的 Model，
 * 实现零代码统计能力。</p>
 * 
 * <h3>使用场景</h3>
 * <ul>
 *   <li>统计某 Model 下的 Entity 数量</li>
 *   <li>计算数值字段的求和、平均值、最大值、最小值</li>
 *   <li>按字段分组统计</li>
 * </ul>
 * 
 * <h3>需求引用</h3>
 * <ul>
 *   <li>FR-013: 系统必须支持扩展字段的计数统计（count）</li>
 *   <li>FR-014: 系统必须支持扩展字段的求和统计（sum）</li>
 *   <li>FR-015: 系统必须支持扩展字段的平均值统计（avg）</li>
 *   <li>FR-016: 系统必须支持扩展字段的最大值统计（max）</li>
 *   <li>FR-017: 系统必须支持扩展字段的最小值统计（min）</li>
 *   <li>FR-018: 系统必须支持按扩展字段分组统计</li>
 * </ul>
 * 
 * @author 扩展字段查询服务
 */
@Schema(description = "管理后台 - 通用 Entity 聚合请求")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericAggregateRequest {

    /**
     * Model 编码（必填）
     * 
     * <p>指定要聚合的 Model，系统会根据 modelCode 获取 Model 的元数据信息。</p>
     */
    @Schema(description = "Model 编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "air-conditioner")
    @NotBlank(message = "Model 编码不能为空")
    private String modelCode;

    /**
     * 查询条件列表（可选）
     * 
     * <p>用于在聚合前过滤数据。最多支持 10 个条件。</p>
     */
    @Schema(description = "查询条件列表（用于过滤）")
    @Valid
    @Size(max = 10, message = "查询条件最多支持10个")
    @Builder.Default
    private List<QueryConditionVO> conditions = new ArrayList<>();

    /**
     * 聚合类型（必填）
     * 
     * <p>支持的聚合类型：</p>
     * <ul>
     *   <li>COUNT: 计数统计</li>
     *   <li>SUM: 求和统计</li>
     *   <li>AVG: 平均值统计</li>
     *   <li>MAX: 最大值统计</li>
     *   <li>MIN: 最小值统计</li>
     * </ul>
     */
    @Schema(description = "聚合类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "COUNT")
    @NotNull(message = "聚合类型不能为空")
    private AggregateType aggregateType;

    /**
     * 聚合字段编码
     * 
     * <p>对于 COUNT：可选，不指定则统计总数。
     * 对于 SUM、AVG、MAX、MIN：必填，必须是数值类型字段。</p>
     */
    @Schema(description = "聚合字段编码", example = "power")
    private String fieldCode;

    /**
     * 分组字段编码（可选）
     * 
     * <p>如果指定，则按此字段分组后再聚合。
     * 例如：按品牌分组统计设备数量。</p>
     */
    @Schema(description = "分组字段编码", example = "brand")
    private String groupByFieldCode;

    // ==================== 便捷方法 ====================

    /**
     * 添加查询条件
     */
    public GenericAggregateRequest addCondition(QueryConditionVO condition) {
        if (this.conditions == null) {
            this.conditions = new ArrayList<>();
        }
        this.conditions.add(condition);
        return this;
    }

    /**
     * 判断是否有查询条件
     */
    public boolean hasConditions() {
        return conditions != null && !conditions.isEmpty();
    }

    /**
     * 判断是否有分组
     */
    public boolean hasGroupBy() {
        return groupByFieldCode != null && !groupByFieldCode.isEmpty();
    }

    // ==================== 静态构建方法 ====================

    /**
     * 创建计数聚合请求
     */
    public static GenericAggregateRequest count(String modelCode) {
        return GenericAggregateRequest.builder()
                .modelCode(modelCode)
                .aggregateType(AggregateType.COUNT)
                .build();
    }

    /**
     * 创建求和聚合请求
     */
    public static GenericAggregateRequest sum(String modelCode, String fieldCode) {
        return GenericAggregateRequest.builder()
                .modelCode(modelCode)
                .aggregateType(AggregateType.SUM)
                .fieldCode(fieldCode)
                .build();
    }

    /**
     * 创建平均值聚合请求
     */
    public static GenericAggregateRequest avg(String modelCode, String fieldCode) {
        return GenericAggregateRequest.builder()
                .modelCode(modelCode)
                .aggregateType(AggregateType.AVG)
                .fieldCode(fieldCode)
                .build();
    }

    /**
     * 设置分组字段
     */
    public GenericAggregateRequest groupBy(String groupByFieldCode) {
        this.groupByFieldCode = groupByFieldCode;
        return this;
    }
}
