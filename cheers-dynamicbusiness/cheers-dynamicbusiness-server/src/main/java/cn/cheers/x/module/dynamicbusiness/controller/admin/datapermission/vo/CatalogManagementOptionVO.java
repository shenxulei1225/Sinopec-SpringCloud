package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogManagementQueryService.ManagementOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "一种可选的管理方式：按哪本目录管理")
@Data
public class CatalogManagementOptionVO {

    @Schema(description = "数据目录注册编码", example = "facility")
    private String registryCode;

    @Schema(description = "数据目录名称，页面显示为「按X管理」", example = "场站")
    private String name;

    @Schema(description = "是不是单位目录本身（排在最前）")
    private Boolean managementUnitCatalog;

    public static CatalogManagementOptionVO from(ManagementOption option) {
        CatalogManagementOptionVO vo = new CatalogManagementOptionVO();
        vo.setRegistryCode(option.registryCode());
        vo.setName(option.name());
        vo.setManagementUnitCatalog(option.managementUnitCatalog());
        return vo;
    }
}
