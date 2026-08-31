package cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "SOP 实例绑定行")
@Data
public class SopInstanceBindingRespVO {

    private Long id;
    private String hostType;
    private Long hostId;
    private String subjectType;
    private Long subjectId;
    private String dimensionKey;
    private String dimensionValue;
    private Long sopInstanceId;
}
