package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "改一条任职的起止日期。换人、换单元、换职务要撤销后重新加")
@Data
public class PostAssignmentUpdateReqVO {

    @Schema(description = "任职编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "请选择任职")
    private Long id;

    @Schema(description = "起始日期；空表示立即")
    private LocalDate validFrom;

    @Schema(description = "截止日期（含当天）；空表示长期")
    private LocalDate validTo;
}
