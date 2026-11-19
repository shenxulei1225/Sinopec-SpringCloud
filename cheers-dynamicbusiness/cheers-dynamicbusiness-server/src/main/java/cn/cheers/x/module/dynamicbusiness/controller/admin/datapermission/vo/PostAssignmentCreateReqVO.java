package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "组织架构页：往一个管理单元按职务加一批人")
@Data
public class PostAssignmentCreateReqVO {

    @Schema(description = "管理单元：管理单元目录里的记录编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "请选择管理单元")
    private Long managementUnitId;

    @Schema(description = "职务记录编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "请选择职务")
    private Long dutyPostId;

    @Schema(description = "人员（登录账号）编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "请选择人员")
    private List<Long> userIds;

    @Schema(description = "起始日期；空表示立即")
    private LocalDate validFrom;

    @Schema(description = "截止日期（含当天）；空表示长期")
    private LocalDate validTo;
}
