package cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "通用宿主-对象-维度-目标绑定行")
@Data
public class RelationInstanceBindingRespVO {

    private Long id;
    private String hostType;
    private Long hostId;
    private String subjectType;
    private Long subjectId;
    private String dimensionKey;
    private String dimensionValue;
    private String targetType;
    private Long targetId;
}
