package cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "SOP 方法选用行")
@Data
public class SopMethodBindingRespVO {

    private Long id;
    private String subjectType;
    private Long subjectId;
    private String dimensionKey;
    private String dimensionValue;
    private Long sopId;
    private String sopName;
}
