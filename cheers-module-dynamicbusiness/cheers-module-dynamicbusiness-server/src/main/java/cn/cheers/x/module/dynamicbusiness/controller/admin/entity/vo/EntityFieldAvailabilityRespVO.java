package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 实体字段可用性校验响应")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityFieldAvailabilityRespVO {

    @Schema(description = "是否可用（true=通过）", example = "true")
    private Boolean available;

    @Schema(description = "不可用时的提示", example = "名称已存在")
    private String message;
}
