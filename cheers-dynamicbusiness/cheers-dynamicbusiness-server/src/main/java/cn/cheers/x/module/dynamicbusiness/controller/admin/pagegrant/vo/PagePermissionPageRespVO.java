package cn.cheers.x.module.dynamicbusiness.controller.admin.pagegrant.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "角色页可勾页面")
@Data
public class PagePermissionPageRespVO {

    @Schema(description = "页面编码")
    private String code;

    @Schema(description = "页面中文名")
    private String name;

    @Schema(description = "可勾动作")
    private List<PagePermissionActionRespVO> actions = new ArrayList<>();
}
