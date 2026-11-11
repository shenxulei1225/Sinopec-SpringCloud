package cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 当前业务租户已开通能力包推导出的可见业务页与目录范围。
 * 顶栏与角色可勾列表按这里裁剪；不代替角色上的查看类动作授权。
 */
@Schema(description = "租户已开通能力包范围")
@Data
public class TenantCapabilityScopeRespVO {

    @Schema(description = "已开通包编码")
    private List<String> packCodes = new ArrayList<>();

    @Schema(description = "已开通包声明的业务页编码并集")
    private List<String> pageCodes = new ArrayList<>();

    @Schema(description = "已开通包声明的数据目录编码并集")
    private List<String> catalogCodes = new ArrayList<>();
}
