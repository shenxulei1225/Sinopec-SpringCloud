package cn.cheers.x.module.dynamicbusiness.controller.admin.pagegrant.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "角色页可勾分组")
@Data
public class PagePermissionGroupRespVO {

    @Schema(description = "分组标题")
    private String title;

    @Schema(description = "组内页面")
    private List<PagePermissionPageRespVO> pages = new ArrayList<>();
}
