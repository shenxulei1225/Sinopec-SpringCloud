package cn.cheers.x.module.dynamicbusiness.controller.admin.pagegrant.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "整份保存角色的页面勾选")
@Data
public class PageRoleGrantSaveReqVO {

    @Schema(description = "角色编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "请选择角色")
    private Long roleId;

    @Valid
    private List<PageRoleGrantItemReqVO> items;
}
