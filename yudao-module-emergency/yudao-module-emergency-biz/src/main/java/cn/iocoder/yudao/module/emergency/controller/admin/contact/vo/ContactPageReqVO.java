package cn.iocoder.yudao.module.emergency.controller.admin.contact.vo;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 应急联络通讯录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ContactPageReqVO extends PageParam {

    @Schema(description = "联系人姓名", example = "张三")
    private String contactName;

    @Schema(description = "联系人类型", example = "internal")
    private String contactType;

    @Schema(description = "所属组织/机构", example = "XX公司")
    private String organization;

    @Schema(description = "应急级别", example = "primary")
    private String emergencyLevel;

    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled;
}



