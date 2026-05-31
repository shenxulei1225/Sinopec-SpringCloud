package cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 关联来源能力 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationSourceCapabilityRespVO {

    @Schema(description = "来源类型", example = "PROVIDER")
    private String targetKind;

    @Schema(description = "来源编码（业务编码或 providerCode）", example = "dynamic_USER")
    private String targetCode;

    @Schema(description = "是否支持候选查询", example = "true")
    private Boolean search;

    @Schema(description = "是否支持有效性校验", example = "true")
    private Boolean validate;

    @Schema(description = "是否支持详情拉取", example = "false")
    private Boolean detailFetch;

    @Schema(description = "是否支持反向查询", example = "false")
    private Boolean reverseQuery;

    @Schema(description = "是否允许写入 association 事实", example = "false")
    private Boolean associationWritable;

    @Schema(description = "是否允许参与聚合统计", example = "false")
    private Boolean aggregationEnabled;
}
