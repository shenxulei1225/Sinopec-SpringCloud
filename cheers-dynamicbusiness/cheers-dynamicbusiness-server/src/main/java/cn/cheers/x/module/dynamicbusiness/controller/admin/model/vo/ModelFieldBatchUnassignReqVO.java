package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 模型字段批量解除关联请求 VO
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 模型字段批量解除关联请求")
@Data
public class ModelFieldBatchUnassignReqVO {

    @Schema(description = "模型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模型ID不能为空")
    private Long modelId;

    @Schema(description = "字段ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    @NotEmpty(message = "字段ID列表不能为空")
    private List<Long> fieldIds;

}












































