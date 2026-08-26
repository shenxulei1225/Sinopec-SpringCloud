package cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 字段响应 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class FieldRespVO extends FieldBaseVO {

    @Schema(description = "字段ID", example = "1024")
    private Long id;

    @Schema(description = "字段编码", example = "F-abc123")
    private String code;

    @Schema(description = "治理状态：LOCAL-站场本地字段，COMPANY-公司字段", example = "COMPANY")
    private String governanceStatus;

    @Schema(description = "本地字段发起设施 ID；公司字段为空", example = "1001")
    private Long originFacilityId;

    @Schema(description = "创建用户 ID", example = "100")
    private Long creatorUserId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

