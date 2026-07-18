package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量实体-分类关联请求 VO
 * 
 * 支持批量将多个实体关联到多个分类
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 批量实体-分类关联请求")
@Data
public class BatchEntityCategoryAssociationReqVO {

    @Schema(description = "实体ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1001, 1002, 1003]")
    @NotEmpty(message = "实体ID列表不能为空")
    private List<Long> entityIds;

    @Schema(description = "分类ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[64, 65]")
    @NotEmpty(message = "分类ID列表不能为空")
    private List<Long> categoryIds;

    @Schema(description = "业务类型编码，用于路由到正确的存储表验证实体存在性", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotNull(message = "业务类型编码不能为空")
    private String entityTypeCode;
}
