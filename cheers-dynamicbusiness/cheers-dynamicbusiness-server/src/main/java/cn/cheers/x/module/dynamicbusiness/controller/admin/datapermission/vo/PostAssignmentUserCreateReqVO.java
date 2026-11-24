package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "数据资源管理「人员的数据范围」：给一个人在一个组织上任几个职务")
@Data
public class PostAssignmentUserCreateReqVO {

    @Schema(description = "人员（登录账号）编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "请选择人员")
    private Long userId;

    @Schema(description = "管理单元所在目录。空则按组织目录。")
    private String managementUnitRegistry;

    @Schema(description = "组织：管理单元目录里的记录编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "请选择组织")
    private Long managementUnitId;

    @Schema(description = "职务记录编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "请至少勾一个职务")
    private List<Long> dutyPostIds;

    @Schema(description = "起始日期；空表示立即")
    private LocalDate validFrom;

    @Schema(description = "截止日期（含当天）；空表示长期")
    private LocalDate validTo;
}
