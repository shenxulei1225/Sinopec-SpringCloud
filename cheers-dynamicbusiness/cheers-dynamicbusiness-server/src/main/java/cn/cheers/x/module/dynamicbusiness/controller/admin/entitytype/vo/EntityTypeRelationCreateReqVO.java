package cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 业务类型关联创建请求 VO
 * 
 * 用于创建两个业务类型之间的门禁许可关系。
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - EntityType 关联创建 Request VO")
@Data
public class EntityTypeRelationCreateReqVO {

    @Schema(description = "源业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "task_management")
    @NotBlank(message = "源业务类型编码不能为空")
    private String sourceEntityTypeCode;

    @Schema(description = "目标业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "production_plan")
    @NotBlank(message = "目标业务类型编码不能为空")
    private String targetEntityTypeCode;
}
