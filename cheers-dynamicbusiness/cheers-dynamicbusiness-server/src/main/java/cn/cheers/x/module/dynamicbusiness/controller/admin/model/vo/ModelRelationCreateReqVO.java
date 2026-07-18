package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * Model 关联创建请求 VO
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - Model 关联创建请求 VO")
@Data
public class ModelRelationCreateReqVO {

    @Schema(description = "目标 Model 编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "plan")
    @NotBlank(message = "目标 Model 编码不能为空")
    private String targetModelCode;
}
