package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "实体变更模型 - 模型摘要")
@Data
public class EntityChangeModelModelSummaryVO {

    @Schema(description = "模型 ID")
    private Long id;

    @Schema(description = "模型编码")
    private String code;

    @Schema(description = "模型名称")
    private String name;
}
