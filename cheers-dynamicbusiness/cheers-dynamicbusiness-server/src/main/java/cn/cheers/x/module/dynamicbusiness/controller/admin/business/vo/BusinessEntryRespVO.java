package cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 业务入口响应 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessEntryRespVO extends BusinessEntryBaseVO {

    @Schema(description = "入口编号")
    private Long id;

    @Schema(description = "所属业务编号")
    private Long businessId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
