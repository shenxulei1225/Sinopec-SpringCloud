package cn.iocoder.yudao.module.emergency.controller.admin.dispatch.vo;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 资源池分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ResourcePoolPageReqVO extends PageParam {

    @Schema(description = "资源名称", example = "消防车")
    private String name;

    @Schema(description = "资源类型", example = "vehicle")
    private String type;

    @Schema(description = "状态", example = "available")
    private String status;

    @Schema(description = "所属组织ID", example = "1")
    private Long organizationId;

}



