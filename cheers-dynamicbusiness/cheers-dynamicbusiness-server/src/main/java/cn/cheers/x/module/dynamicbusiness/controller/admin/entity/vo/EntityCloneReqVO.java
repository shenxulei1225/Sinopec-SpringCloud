package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 实体复制请求：基于源实体复制字段值与分类挂接，沿用源型号。
 */
@Schema(description = "管理后台 - 实体复制请求")
@Data
public class EntityCloneReqVO {

    @Schema(description = "源实体 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    @NotNull(message = "源实体不能为空")
    private Long sourceEntityId;

    @Schema(description = "业务类型编码（存储类型）", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "业务类型编码不能为空")
    private String entityTypeCode;

    @Schema(description = "新实体名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "控制箱-01 副本")
    @NotBlank(message = "实体名称不能为空")
    private String name;

    @Schema(description = "描述；不传则沿用源实体 description（若有）")
    private String description;

    @Schema(description = "额外挂接的分类 ID（与源实体分类合并去重）；不传则只拷源实体已有分类")
    private List<Long> categoryIds;
}
