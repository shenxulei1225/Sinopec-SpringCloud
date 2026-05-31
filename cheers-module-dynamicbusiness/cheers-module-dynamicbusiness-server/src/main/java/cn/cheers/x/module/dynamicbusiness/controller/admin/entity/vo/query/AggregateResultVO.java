package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 聚合结果响应 VO
 * 
 * <p>用于返回聚合查询的结果。</p>
 * 
 * @author 扩展字段查询服务
 */
@Schema(description = "管理后台 - 聚合结果响应")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregateResultVO {

    /**
     * 聚合值（非分组时）
     * 
     * <p>当没有分组时，返回单个聚合值。</p>
     */
    @Schema(description = "聚合值", example = "100")
    private Object value;

    /**
     * 分组结果（分组时）
     * 
     * <p>当有分组时，返回分组结果列表。</p>
     */
    @Schema(description = "分组结果列表")
    private List<GroupResultVO> groups;

    /**
     * 分组结果 VO
     */
    @Schema(description = "分组结果")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupResultVO {

        /**
         * 分组键
         * 
         * <p>分组字段的值。</p>
         */
        @Schema(description = "分组键", example = "海尔")
        private Object groupKey;

        /**
         * 聚合值
         * 
         * <p>该分组的聚合结果。</p>
         */
        @Schema(description = "聚合值", example = "50")
        private Object value;

        /**
         * 记录数
         * 
         * <p>该分组的记录数量。</p>
         */
        @Schema(description = "记录数", example = "10")
        private Long count;
    }
}
