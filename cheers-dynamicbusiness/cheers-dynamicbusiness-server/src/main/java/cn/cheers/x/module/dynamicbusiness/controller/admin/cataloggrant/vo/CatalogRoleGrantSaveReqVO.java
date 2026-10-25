package cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "保存角色的数据目录授权。整份替换该角色已有授权。")
@Data
public class CatalogRoleGrantSaveReqVO {

    @Schema(description = "角色编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "请选择角色")
    private Long roleId;

    @Schema(description = "各数据目录的勾选。空列表表示清空该角色的目录授权。")
    @Valid
    private List<CatalogRoleGrantItemReqVO> items;

    @Schema(description = "各数据目录的场站范围。不传则不改已保存的范围。空列表表示全部改回全部场站。")
    @Valid
    private List<CatalogRoleStationScopeReqVO> stationScopes;
}
