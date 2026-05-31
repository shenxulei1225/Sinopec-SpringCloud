package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 实体-分类取消关联请求 VO
 *
 * 用于取消实体与分类的关联关系
 *
 * @author yudao
 */
@Schema(description = "管理后台 - 实体-分类取消关联请求")
@Data
public class EntityCategoryDisassociationReqVO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    @NotNull(message = "实体ID不能为空")
    private Long entityId;

    @Schema(description = "分类ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[64, 65]")
    @NotEmpty(message = "分类ID列表不能为空")
    private List<Long> categoryIds;
}
