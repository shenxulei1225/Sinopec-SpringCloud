package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import cn.cheers.x.module.dynamicbusiness.controller.admin.common.vo.CategoryAssociationBaseRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 实体-分类关联操作响应 VO。
 */
@Schema(description = "管理后台 - 实体-分类关联操作响应")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EntityCategoryAssociationRespVO extends CategoryAssociationBaseRespVO {

    @Schema(description = "实体ID", example = "1001")
    private Long entityId;

    @Schema(description = "实体是否存在", example = "true")
    private Boolean entityExists;
}
