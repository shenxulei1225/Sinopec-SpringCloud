package cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BusinessBaseVO {

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    @Schema(description = "业务名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "设备管理")
    @NotBlank(message = "名称不能为空")
    @Size(max = 200, message = "名称长度不能超过200")
    private String name;

    @Schema(description = "业务编码 businessCode", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment-mgmt")
    @NotBlank(message = "编码不能为空")
    @Size(max = 64, message = "编码长度不能超过64")
    private String code;

    @Schema(description = "父级业务编号", example = "1024")
    private Long parentId;

    @Schema(description = "节点类型：GROUP=分组，LEAF=叶子", example = "LEAF")
    private String nodeKind;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "active")
    @NotNull(message = "状态不能为空")
    private String status;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "图标", example = "device")
    private String icon;

    @Schema(description = "别名", example = "设备")
    private String alias;
}
