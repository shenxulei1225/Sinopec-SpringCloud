package cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ManagementUnitBusinessCatalogSaveReqVO {

    @NotBlank(message = "缺少管理单元目录编码")
    private String registryCode;

    @NotNull(message = "缺少管理点")
    private Long managementUnitId;

    @Schema(description = "勾选的业务数据目录注册编码，整份替换")
    private List<String> catalogCodes;
}