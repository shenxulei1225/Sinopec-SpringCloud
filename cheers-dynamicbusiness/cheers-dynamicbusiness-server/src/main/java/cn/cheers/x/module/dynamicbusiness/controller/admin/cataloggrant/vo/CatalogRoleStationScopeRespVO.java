package cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "一个数据目录已保存的场站范围")
@Data
public class CatalogRoleStationScopeRespVO {

    @Schema(description = "数据目录注册编码")
    private String registryCode;

    @Schema(description = "查看范围")
    private String viewScope;

    @Schema(description = "写入范围")
    private String writeScope;
}
