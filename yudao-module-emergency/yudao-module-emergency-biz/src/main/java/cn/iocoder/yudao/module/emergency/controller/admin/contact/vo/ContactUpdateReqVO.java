package cn.iocoder.yudao.module.emergency.controller.admin.contact.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 应急联络通讯录更新 Request VO")
@Data
public class ContactUpdateReqVO {

    @Schema(description = "联系人ID。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "联系人ID不能为空")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long id;

    @Schema(description = "联系人编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "CON001")
    @NotBlank(message = "联系人编号不能为空")
    private String contactCode;

    @Schema(description = "联系人姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    @Schema(description = "联系人类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "internal")
    @NotBlank(message = "联系人类型不能为空")
    private String contactType;

    @Schema(description = "所属组织/机构", example = "XX公司")
    private String organization;

    @Schema(description = "部门", example = "安全部")
    private String department;

    @Schema(description = "职位", example = "安全主管")
    private String position;

    @Schema(description = "电话", example = "010-12345678")
    private String phone;

    @Schema(description = "手机", example = "13800138000")
    private String mobile;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "地址", example = "XX市XX区XX路XX号")
    private String address;

    @Schema(description = "应急级别", example = "primary")
    private String emergencyLevel;

    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled;

    @Schema(description = "备注", example = "主要负责应急指挥")
    private String remark;
}



