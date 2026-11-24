package cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "业务能力包定义")
@Data
public class TenantCapabilityPackDefRespVO {

    private String code;
    private String name;
    private String description;
    private List<String> pageCodes;
    private List<PageItem> pages;
    private List<String> catalogCodes;

    @Schema(description = "非空时须在对应目录的锚点记录上登记启用")
    private List<String> unitEnablementRegistryCodes;

    @Schema(description = "这一包下可单独开通的页面")
    @Data
    public static class PageItem {
        private String code;
        private String name;
    }
}
