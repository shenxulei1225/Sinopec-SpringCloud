package cn.cheers.x.maintenance.controller.admin.vo.standard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class FieldWorkStandardUpdateReqVO {
    @NotBlank @Size(max = 128) private String name;
    @NotBlank @Size(max = 32) private String scope;
    @NotEmpty @Valid private List<FieldWorkStandardStepVO> steps;
}
