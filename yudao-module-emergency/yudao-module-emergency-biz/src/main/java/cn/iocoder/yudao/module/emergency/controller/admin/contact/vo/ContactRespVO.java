package cn.iocoder.yudao.module.emergency.controller.admin.contact.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 应急联络通讯录 Response VO")
@Data
public class ContactRespVO {

    @Schema(description = "联系人ID", example = "1")
    private Long id;

    @Schema(description = "联系人编号", example = "CON001")
    private String contactCode;

    @Schema(description = "联系人姓名", example = "张三")
    private String contactName;

    @Schema(description = "联系人类型", example = "internal")
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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}



