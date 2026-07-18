package cn.cheers.x.module.dynamicbusiness.controller.admin.group.vo;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "管理后台 - 通用分组分页 Request VO")
public class GroupPageReqVO extends PageParam {

    @Schema(description = "分组类型", example = "FIELD")
    private String groupType;

    @Schema(description = "分组名称", example = "单位")
    private String name;

    @Schema(description = "状态", example = "1")
    private Integer status;
}
