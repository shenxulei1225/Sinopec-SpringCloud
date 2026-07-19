package cn.cheers.x.maintenance.controller.admin.vo.standard;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 现场作业标准步骤")
@Data
public class FieldWorkStandardStepVO {
    @NotBlank @Size(max = 64) private String code;
    @NotBlank @Size(max = 128) private String title;
    @NotNull private Boolean required;
    @NotBlank @Size(max = 32) private String controlType;
}
