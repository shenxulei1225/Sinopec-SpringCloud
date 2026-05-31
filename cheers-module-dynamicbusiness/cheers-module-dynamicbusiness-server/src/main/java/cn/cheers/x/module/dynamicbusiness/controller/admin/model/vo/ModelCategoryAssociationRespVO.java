package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import cn.cheers.x.module.dynamicbusiness.controller.admin.common.vo.CategoryAssociationBaseRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 模型-分类关联操作响应 VO。
 */
@Schema(description = "管理后台 - 模型-分类关联操作响应")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ModelCategoryAssociationRespVO extends CategoryAssociationBaseRespVO {

    @Schema(description = "模型ID", example = "1001")
    private Long modelId;

    @Schema(description = "模型是否存在", example = "true")
    private Boolean modelExists;
}
