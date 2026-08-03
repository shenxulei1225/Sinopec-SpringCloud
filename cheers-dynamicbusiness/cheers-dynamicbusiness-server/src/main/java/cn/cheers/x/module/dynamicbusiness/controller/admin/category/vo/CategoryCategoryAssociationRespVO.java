package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 分类—分类跨种类关联操作响应。
 */
@Schema(description = "管理后台 - 分类—分类关联操作响应")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCategoryAssociationRespVO {

    @Schema(description = "操作类型 ASSOCIATE / DISASSOCIATE / BATCH_ASSOCIATE / BATCH_DISASSOCIATE")
    private String operationType;

    @Schema(description = "宿主分类 ID")
    private Long hostCategoryId;

    @Schema(description = "成员分类 ID（单成员操作时）")
    private Long memberCategoryId;

    @Schema(description = "成功数")
    private Integer successCount;

    @Schema(description = "失败数")
    private Integer failCount;

    @Schema(description = "总数")
    private Integer totalCount;

    @Schema(description = "成功的成员分类 ID")
    @Builder.Default
    private List<Long> successMemberCategoryIds = Collections.emptyList();

    @Schema(description = "失败说明")
    @Builder.Default
    private List<String> failMessages = Collections.emptyList();

    @Schema(description = "耗时毫秒")
    private Long executionTime;
}
