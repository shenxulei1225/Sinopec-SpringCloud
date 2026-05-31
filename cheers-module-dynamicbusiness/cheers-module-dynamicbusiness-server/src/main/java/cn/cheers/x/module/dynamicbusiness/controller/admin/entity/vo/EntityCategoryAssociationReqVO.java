package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 实体-分类关联请求 VO
 * 
 * 用于单个实体的分类关联操作（关联、取消关联、替换）
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 实体-分类关联请求")
@Data
public class EntityCategoryAssociationReqVO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    @NotNull(message = "实体ID不能为空")
    private Long entityId;

    @Schema(description = "业务类型编码，用于路由到正确的存储表验证实体存在性", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotNull(message = "业务类型编码不能为空")
    private String businessTypeCode;

    @Schema(description = "分类ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[64, 65]")
    @NotEmpty(message = "分类ID列表不能为空")
    private List<Long> categoryIds;
}
