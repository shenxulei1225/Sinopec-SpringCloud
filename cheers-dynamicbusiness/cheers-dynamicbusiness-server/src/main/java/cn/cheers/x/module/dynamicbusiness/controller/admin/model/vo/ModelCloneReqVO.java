package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 业务模型复制请求：基于源模型复制字段分配、字段分组与分类挂接。
 */
@Schema(description = "管理后台 - 业务模型复制请求")
@Data
public class ModelCloneReqVO {

    @Schema(description = "源模型 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1736")
    @NotNull(message = "源模型不能为空")
    private Long sourceModelId;

    @Schema(description = "新模型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "10万立方储罐-副本")
    @NotBlank(message = "模型名称不能为空")
    private String name;

    @Schema(description = "模型描述；不传则沿用源模型", example = "基于现有型号微调")
    private String description;

    @Schema(description = "模型状态；不传则沿用源模型", example = "1")
    private Integer status;

    @Schema(description = "额外挂接的分类 ID 列表（与源模型分类合并去重）；不传则只拷源模型已有分类")
    private List<Long> categoryIds;

    @Schema(description = "新型号治理状态；无公司规格创建能力时服务端强制按 LOCAL 创建", example = "LOCAL")
    private String governanceStatus;

    @Schema(description = "当前有效站场 ID；复制为本地型号时必填", example = "1001")
    private Long effectiveFacilityId;
}
