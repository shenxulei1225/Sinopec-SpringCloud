package cn.cheers.x.maintenance.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BindingResolveRespDTO {
    private Long handbookId;
    private Long fieldStandardId;
    private Integer standardVersionNo;
    private String orchestrationTemplateCode;
}
