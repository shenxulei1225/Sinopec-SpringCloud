package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 实体-分类查询请求 VO
 * 
 * 用于查询实体关联的分类信息
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 实体-分类查询请求")
@Data
public class EntityCategoryQueryReqVO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    @NotNull(message = "实体ID不能为空")
    private Long entityId;
}
