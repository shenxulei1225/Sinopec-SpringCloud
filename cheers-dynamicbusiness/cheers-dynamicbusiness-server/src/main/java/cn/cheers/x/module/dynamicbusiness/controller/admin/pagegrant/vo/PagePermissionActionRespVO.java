package cn.cheers.x.module.dynamicbusiness.controller.admin.pagegrant.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "角色页可勾动作")
@Data
public class PagePermissionActionRespVO {

    @Schema(description = "动作编码")
    private String code;

    @Schema(description = "动作中文名")
    private String label;
}
