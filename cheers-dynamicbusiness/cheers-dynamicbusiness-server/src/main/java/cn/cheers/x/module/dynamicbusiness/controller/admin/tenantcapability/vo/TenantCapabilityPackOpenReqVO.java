package cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "整份保存业务租户已开通的能力包")
@Data
public class TenantCapabilityPackOpenReqVO {

    @NotNull(message = "业务租户编号不能为空")
    private Long tenantId;

    @Schema(description = "要开通的包编码列表；每一包按全部页面开通。与 selections 同时出现时以 selections 为准")
    private List<String> packCodes;

    @Schema(description = "按页面开通。未出现的业务会关闭。某一业务的 pageCodes 为空且该业务有页面时，视为不开通")
    private List<Selection> selections;

    @Data
    public static class Selection {
        private String packCode;
        private List<String> pageCodes;
    }
}
