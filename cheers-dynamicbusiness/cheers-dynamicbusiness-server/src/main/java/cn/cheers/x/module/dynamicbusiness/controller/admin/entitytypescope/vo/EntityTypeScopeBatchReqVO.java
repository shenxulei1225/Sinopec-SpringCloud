package cn.cheers.x.module.dynamicbusiness.controller.admin.entitytypescope.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Schema(description = "划分数据批量加入/移出 Request VO")
@Data
public class EntityTypeScopeBatchReqVO {

    @Schema(description = "划分数据入口编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment_patrol")
    @NotBlank(message = "entityTypeCode 不能为空")
    private String entityTypeCode;

    @Schema(description = "实体 id 列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    @NotEmpty(message = "entityIds 不能为空")
    private List<Long> entityIds;
}
