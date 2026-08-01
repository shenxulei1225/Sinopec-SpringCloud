package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 型号—实体关联操作响应。
 */
@Schema(description = "管理后台 - 型号—实体关联操作响应")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelEntityAssociationRespVO {

    @Schema(description = "操作类型 ASSOCIATE / DISASSOCIATE / BATCH_ASSOCIATE / BATCH_DISASSOCIATE")
    private String operationType;

    @Schema(description = "型号 ID")
    private Long modelId;

    @Schema(description = "实体 ID（单实体操作时）")
    private Long entityId;

    @Schema(description = "成功数")
    private Integer successCount;

    @Schema(description = "失败数")
    private Integer failCount;

    @Schema(description = "总数")
    private Integer totalCount;

    @Schema(description = "成功的实体 ID")
    @Builder.Default
    private List<Long> successEntityIds = Collections.emptyList();

    @Schema(description = "失败说明")
    @Builder.Default
    private List<String> failMessages = Collections.emptyList();

    @Schema(description = "耗时毫秒")
    private Long executionTime;
}
