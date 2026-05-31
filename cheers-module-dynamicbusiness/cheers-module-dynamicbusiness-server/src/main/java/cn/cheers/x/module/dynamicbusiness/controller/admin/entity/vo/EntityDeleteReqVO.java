package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "管理后台 - 实体删除 Request VO")
public class EntityDeleteReqVO {

    @Schema(description = "实体编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "实体编号不能为空")
    private Long id;

    @Schema(description = "业务类型编码（用于路由到对应存储策略）", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "业务类型编码不能为空")
    private String businessTypeCode;

    @Schema(description = "是否强制删除（同时删除所有关联关系）", example = "false")
    private Boolean forceDelete = false;
}
