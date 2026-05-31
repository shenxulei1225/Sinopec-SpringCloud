package cn.cheers.x.module.dynamicbusiness.service.field.vo;

import cn.cheers.x.module.dynamicbusiness.service.field.SmartSearchableService.IndexStrategy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 智能默认配置 VO
 * 
 * <p>描述某个字段类型的智能默认设置，包括：</p>
 * <ul>
 *   <li>是否默认可查询</li>
 *   <li>是否默认可排序</li>
 *   <li>推荐的索引策略</li>
 *   <li>是否支持手动开启可查询</li>
 * </ul>
 * 
 * @author yudao
 * @since 2026-01-07
 */
@Schema(description = "智能默认配置 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartDefaultConfigVO {

    /**
     * 字段类型编码
     */
    @Schema(description = "字段类型编码", example = "TEXT")
    private String fieldType;

    /**
     * 字段类型名称
     */
    @Schema(description = "字段类型名称", example = "文本")
    private String fieldTypeName;

    /**
     * 是否默认可查询
     */
    @Schema(description = "是否默认可查询", example = "true")
    private Boolean defaultSearchable;

    /**
     * 是否默认可排序
     */
    @Schema(description = "是否默认可排序", example = "true")
    private Boolean defaultSortable;

    /**
     * 推荐的索引策略
     */
    @Schema(description = "推荐的索引策略", example = "GIN")
    private IndexStrategy indexStrategy;

    /**
     * 索引策略编码（用于前端展示）
     */
    @Schema(description = "索引策略编码", example = "GIN")
    private String indexStrategyCode;

    /**
     * 索引策略名称（用于前端展示）
     */
    @Schema(description = "索引策略名称", example = "GIN 索引")
    private String indexStrategyName;

    /**
     * 是否支持手动开启可查询
     * 
     * <p>某些类型（如 LONG_TEXT）虽然默认不可查询，但支持用户手动开启。</p>
     * <p>某些类型（如 FILE、IMAGE）不支持手动开启可查询。</p>
     */
    @Schema(description = "是否支持手动开启可查询", example = "true")
    private Boolean canManuallyEnableSearchable;

    /**
     * 配置说明
     */
    @Schema(description = "配置说明", example = "文本字段，使用 GIN 索引支持等值和模糊查询")
    private String description;

    /**
     * 创建智能默认配置
     * 
     * @param fieldType 字段类型编码
     * @param fieldTypeName 字段类型名称
     * @param defaultSearchable 是否默认可查询
     * @param defaultSortable 是否默认可排序
     * @param indexStrategy 索引策略
     * @param canManuallyEnableSearchable 是否支持手动开启可查询
     * @param description 配置说明
     * @return SmartDefaultConfigVO
     */
    public static SmartDefaultConfigVO of(String fieldType, String fieldTypeName,
                                          boolean defaultSearchable, boolean defaultSortable,
                                          IndexStrategy indexStrategy,
                                          boolean canManuallyEnableSearchable,
                                          String description) {
        return SmartDefaultConfigVO.builder()
                .fieldType(fieldType)
                .fieldTypeName(fieldTypeName)
                .defaultSearchable(defaultSearchable)
                .defaultSortable(defaultSortable)
                .indexStrategy(indexStrategy)
                .indexStrategyCode(indexStrategy.getCode())
                .indexStrategyName(indexStrategy.getName())
                .canManuallyEnableSearchable(canManuallyEnableSearchable)
                .description(description)
                .build();
    }
}
