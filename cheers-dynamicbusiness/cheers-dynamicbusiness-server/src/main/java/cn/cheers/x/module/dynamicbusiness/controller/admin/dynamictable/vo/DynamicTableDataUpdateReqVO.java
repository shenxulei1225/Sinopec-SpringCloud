package cn.cheers.x.module.dynamicbusiness.controller.admin.dynamictable.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 动态表数据更新请求 VO
 */
@Schema(description = "管理后台 - 动态表数据更新请求")
@Data
public class DynamicTableDataUpdateReqVO {

    @Schema(description = "模型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模型ID不能为空")
    private Long modelId;

    @Schema(description = "记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "记录ID不能为空")
    private Long recordId;

    @Schema(description = "数据（key为字段ID，value为字段值）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据不能为空")
    private Map<String, Object> data;
}
