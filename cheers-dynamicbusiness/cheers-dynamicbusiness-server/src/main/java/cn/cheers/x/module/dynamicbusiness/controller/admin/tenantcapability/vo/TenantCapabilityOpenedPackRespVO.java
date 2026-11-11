package cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "业务租户已开通的一个业务，以及勾上的页面")
@Data
public class TenantCapabilityOpenedPackRespVO {

    private String packCode;

    private List<String> pageCodes = new ArrayList<>();
}
