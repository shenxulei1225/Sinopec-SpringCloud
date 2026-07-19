package cn.cheers.x.maintenance.controller.admin.vo.handbook;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HandbookCreateReqVO {
    @NotBlank @Size(max = 64) private String code;
    @NotBlank @Size(max = 128) private String name;
    @NotBlank @Size(max = 32) private String scope;
    @Size(max = 64) private String assetTypeCode;
    @Size(max = 32) private String frequencyCode;
    @NotNull private Long fieldStandardId;
    @Size(max = 256) private String crewHint;
    @Size(max = 512) private String materialHint;
}
