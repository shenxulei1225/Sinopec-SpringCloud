package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 管理后台 - 实体搜索响应 VO
 * 
 * 包含搜索结果和聚合统计信息
 */
@Schema(description = "管理后台 - 实体搜索响应 VO")
@Data
public class EntitySearchRespVO {

    @Schema(description = "搜索结果列表")
    private List<EntityRespVO> list;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "当前页码")
    private Integer pageNo;

    @Schema(description = "每页条数")
    private Integer pageSize;

    @Schema(description = "总页数")
    private Integer totalPages;

    @Schema(description = "搜索耗时（毫秒）")
    private Long searchTime;

    @Schema(description = "聚合统计信息（可选）")
    private AggregationInfo aggregation;

    /**
     * 聚合统计信息
     */
    @Schema(description = "聚合统计信息")
    @Data
    public static class AggregationInfo {
        
        @Schema(description = "按状态分组统计", example = "{\"1\": 100, \"0\": 20}")
        private Map<Integer, Long> statusCount;

        @Schema(description = "按模型分组统计", example = "{\"1\": 50, \"2\": 70}")
        private Map<Long, Long> modelCount;

        @Schema(description = "按业务类型分组统计", example = "{\"equipment\": 80, \"task\": 40}")
        private Map<String, Long> businessTypeCount;
    }
}
