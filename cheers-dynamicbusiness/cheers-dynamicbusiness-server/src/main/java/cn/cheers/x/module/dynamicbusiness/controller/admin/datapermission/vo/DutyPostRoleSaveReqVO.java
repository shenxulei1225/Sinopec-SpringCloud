package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "角色页整份保存这个角色绑定的职务")
@Data
public class DutyPostRoleSaveReqVO {

    @Schema(description = "角色编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "请选择角色")
    private Long roleId;

    @Schema(description = "职务记录编号；空列表表示解除全部绑定")
    @NotNull(message = "职务列表不能为空，没有就传空列表")
    private List<Long> dutyPostIds;
}
