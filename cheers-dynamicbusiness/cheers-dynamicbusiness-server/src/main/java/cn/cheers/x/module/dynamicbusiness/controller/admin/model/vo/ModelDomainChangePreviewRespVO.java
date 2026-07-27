package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 型号跨业务域迁移的影响预览。
 *
 * <p>用于把型号拖到另一个业务域分组时，先告诉用户会连带改动多少实体和分类关联，
 * 有存量数据时要求二次确认。</p>
 */
@Schema(description = "管理后台 - 型号业务域迁移影响预览")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelDomainChangePreviewRespVO {

    @Schema(description = "型号编号")
    private Long modelId;

    @Schema(description = "型号名称")
    private String modelName;

    @Schema(description = "数据类型编码（实际存储类型）")
    private String entityTypeCode;

    @Schema(description = "当前业务域；为空表示未划域")
    private String currentDomain;

    @Schema(description = "目标业务域；为空表示移出业务域")
    private String targetDomain;

    @Schema(description = "业务域是否确实发生变化")
    private Boolean changed;

    @Schema(description = "受影响的实体数量")
    private Integer entityCount;

    @Schema(description = "受影响的分类关联数量")
    private Integer relationCount;
}
