package cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo;

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
@Schema(description = "管理后台 - BusinessType 关联创建 Request VO")
@Data
public class BusinessTypeRelationCreateReqVO {

    @Schema(description = "源业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "task_management")
    @NotBlank(message = "源业务类型编码不能为空")
    private String sourceBusinessTypeCode;

    @Schema(description = "目标业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "production_plan")
    @NotBlank(message = "目标业务类型编码不能为空")
    private String targetBusinessTypeCode;
}
