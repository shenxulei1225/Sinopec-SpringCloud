package cn.cheers.x.maintenance.controller.admin.vo.corrective;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class CorrectiveCaseCreateReqVO {
    @NotBlank @Size(max=256) private String title;
    @Size(max=2000) private String description;
    private Long assetId;
    @Size(max=64) private String assetTypeCode;
    @Size(max=16) private String priority;
    private Long fieldStandardId;
}
