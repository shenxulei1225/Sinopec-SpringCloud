package cn.cheers.x.module.dynamicbusiness.controller.admin.page.vo;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 页面管理分页请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class PagePageReqVO extends PageParam {

    @Schema(description = "页面名称", example = "设备")
    private String pageName;

    @Schema(description = "页面代码", example = "region-management-default")
    private String pageCode;

    @Schema(description = "页面类型", example = "data_management")
    private String pageType;

    @Schema(description = "页面状态（1-发布，0-停用）", example = "1")
    private Integer status;

    @Schema(description = "业务归属父菜单ID", example = "100")
    private Long parentMenuId;
}


