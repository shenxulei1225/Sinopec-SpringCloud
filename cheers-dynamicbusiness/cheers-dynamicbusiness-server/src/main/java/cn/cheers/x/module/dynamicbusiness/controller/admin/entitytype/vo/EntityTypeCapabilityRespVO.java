package cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 数据目录能力开关响应 VO")
@Data
public class EntityTypeCapabilityRespVO {

    @Schema(description = "数据目录编码", example = "sop")
    private String entityTypeCode;

    @Schema(description = "能力项")
    private List<EntityTypeCapabilityItemRespVO> capabilityItems;

    @Schema(description = "步骤树允许挂哪些数据目录")
    private List<String> stepTreeHangableTypeCodes;

    @Schema(description = "步骤树是否允许同一棵树混挂")
    private Boolean stepTreeAllowMixed;
}
