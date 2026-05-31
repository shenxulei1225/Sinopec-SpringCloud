package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 按单个分类分页查询 Entity Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EntityByCategoryPageReqVO extends PageParam {

    @Schema(description = "分类 ID（必填，会自动包含所有子分类）", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "分类 ID 不能为空")
    private Long categoryId;

    @Schema(description = "内容业务类型编码（必填，用于跨业务类型查询）", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "内容业务类型编码不能为空")
    private String contentBusinessTypeCode;

    @Schema(description = "Model ID（可选，用于进一步过滤实体）", example = "1")
    private Long modelId;

}

