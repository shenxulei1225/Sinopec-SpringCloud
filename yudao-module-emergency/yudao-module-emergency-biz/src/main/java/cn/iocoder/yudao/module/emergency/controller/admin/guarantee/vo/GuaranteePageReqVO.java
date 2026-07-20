package cn.iocoder.yudao.module.emergency.controller.admin.guarantee.vo;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 应急保障分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GuaranteePageReqVO extends PageParam {

    @Schema(description = "保障名称", example = "医疗救护保障")
    private String guaranteeName;

    @Schema(description = "保障类型", example = "medical")
    private String guaranteeType;

    @Schema(description = "保障状态", example = "available")
    private String status;

    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled;
}



