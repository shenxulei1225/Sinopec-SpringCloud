package cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 门户业务响应 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessRespVO extends BusinessBaseVO {

    @Schema(description = "业务编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "子业务列表")
    private List<BusinessRespVO> children;

    @Schema(description = "业务入口列表")
    private List<BusinessEntryRespVO> entries;
}
