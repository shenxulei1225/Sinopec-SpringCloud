package cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - Ref 约束器库分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class RefConstraintLibraryPageReqVO extends PageParam {

    @Schema(description = "业务类型编码", example = "personnel")
    private String entityTypeCode;

    @Schema(description = "Ref 目标类型", example = "personnel")
    private String refTargetType;

    @Schema(description = "约束器类型", example = "ROLE_DEPT")
    private String constraintType;

    @Schema(description = "约束器名称", example = "角色+部门")
    private String constraintName;

    @Schema(description = "状态：0-开启，1-关闭", example = "0")
    private Integer status;
}
