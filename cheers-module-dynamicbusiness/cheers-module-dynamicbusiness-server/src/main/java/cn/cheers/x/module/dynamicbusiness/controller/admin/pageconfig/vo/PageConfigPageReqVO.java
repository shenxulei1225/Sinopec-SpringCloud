package cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 管理后台 - 页面配置分页 Request VO
 */
@Schema(description = "管理后台 - 页面配置分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PageConfigPageReqVO extends PageParam {

    @Schema(description = "关联菜单ID", example = "100")
    private Long menuId;

    @Schema(description = "页面类型：data_management-数据管理, dashboard-驾驶舱, statistics-统计, monitor-实时监控", 
            example = "data_management")
    private String pageType;
}
