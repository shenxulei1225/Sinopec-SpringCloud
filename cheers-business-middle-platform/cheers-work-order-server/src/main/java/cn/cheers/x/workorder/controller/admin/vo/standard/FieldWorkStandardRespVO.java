package cn.cheers.x.workorder.controller.admin.vo.standard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理后台 - 现场作业标准 Response VO
 */
@Schema(description = "管理后台 - 现场作业标准 Response VO")
@Data
public class FieldWorkStandardRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "标准编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "pump-monthly")
    private String code;

    @Schema(description = "标准名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "离心泵月检")
    private String name;

    @Schema(description = "版本号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer versionNo;

    @Schema(description = "业务域范围", requiredMode = Schema.RequiredMode.REQUIRED, example = "inspection")
    private String scope;

    @Schema(description = "步骤列表（有序）", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<FieldWorkStandardStepVO> steps;

    @Schema(description = "状态：0=草稿，1=已发布", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
