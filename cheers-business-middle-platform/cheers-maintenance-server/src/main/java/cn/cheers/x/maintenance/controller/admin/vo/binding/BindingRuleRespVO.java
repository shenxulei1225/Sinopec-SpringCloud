package cn.cheers.x.maintenance.controller.admin.vo.binding;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class BindingRuleRespVO {
    private Long id; private String code; private String name; private String scope;
    private Long assetId; private String assetTypeCode; private String frequencyCode;
    private Long handbookId; private Long fieldStandardId; private String orchestrationTemplateCode;
    private Integer priority; private Integer status;
    private LocalDateTime createTime; private LocalDateTime updateTime;
}
