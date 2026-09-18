package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 通用实体：按版本发布请求。
 */
@Data
public class EntityVersionPublishReqVO {

    @Schema(description = "数据类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "sop")
    @NotBlank(message = "entityTypeCode 不能为空")
    private String entityTypeCode;

    @Schema(description = "当前实体 id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    @NotNull(message = "entityId 不能为空")
    private Long entityId;

    @Schema(description = "发布版本号（version_no）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "versionNo 不能为空")
    @Min(value = 1, message = "versionNo 必须大于 0")
    private Integer versionNo;
}

