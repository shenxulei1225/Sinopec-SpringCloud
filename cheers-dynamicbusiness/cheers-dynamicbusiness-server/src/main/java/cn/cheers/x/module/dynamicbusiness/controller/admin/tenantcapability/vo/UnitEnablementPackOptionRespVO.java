package cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UnitEnablementPackOptionRespVO {

    @Schema(description = "业务能力包编码")
    private String code;

    @Schema(description = "中文名")
    private String name;

    @Schema(description = "说明")
    private String description;
}