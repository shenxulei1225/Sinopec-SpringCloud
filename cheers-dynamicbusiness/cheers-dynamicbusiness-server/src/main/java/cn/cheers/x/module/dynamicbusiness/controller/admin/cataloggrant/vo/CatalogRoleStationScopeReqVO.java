package cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "一个数据目录的场站范围")
@Data
public class CatalogRoleStationScopeReqVO {

    @Schema(description = "数据目录注册编码")
    @NotBlank(message = "请选择数据目录")
    private String registryCode;

    @Schema(description = "查看范围：UNRESTRICTED 不限制，ALL 全部场站，CURRENT 仅当前站")
    @NotBlank(message = "请选择查看范围")
    private String viewScope;

    @Schema(description = "写入范围：UNRESTRICTED 不限制，ALL 全部场站，CURRENT 仅当前站")
    @NotBlank(message = "请选择写入范围")
    private String writeScope;
}
