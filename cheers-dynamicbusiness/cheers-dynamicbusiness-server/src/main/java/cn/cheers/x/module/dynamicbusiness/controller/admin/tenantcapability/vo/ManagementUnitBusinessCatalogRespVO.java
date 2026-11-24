package cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability.vo;

import lombok.Data;

import java.util.List;

@Data
public class ManagementUnitBusinessCatalogRespVO {

    private String registryCode;
    private Long managementUnitId;
    private List<String> catalogCodes;
}