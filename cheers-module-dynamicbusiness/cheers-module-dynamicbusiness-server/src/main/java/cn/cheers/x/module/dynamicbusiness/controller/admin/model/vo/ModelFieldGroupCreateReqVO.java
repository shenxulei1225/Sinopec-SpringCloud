package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型字段分组创建请求 VO
 */
@Schema(description = "管理后台 - 模型字段分组创建请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ModelFieldGroupCreateReqVO extends ModelFieldGroupBaseVO {

    @Schema(description = "模型ID", example = "1024", required = true)
    @NotNull(message = "模型ID不能为空")
    private Long modelId;

    @Schema(description = "分组名称", example = "基础字段", required = true)
    @NotBlank(message = "分组名称不能为空")
    private String name;
}
