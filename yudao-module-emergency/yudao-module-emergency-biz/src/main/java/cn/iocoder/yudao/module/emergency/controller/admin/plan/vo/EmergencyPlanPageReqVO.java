package cn.iocoder.yudao.module.emergency.controller.admin.plan.vo;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 应急预案分页查询 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - 应急预案分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmergencyPlanPageReqVO extends PageParam {

    @Schema(description = "预案编号", example = "PLAN-2023001")
    private String planNo;

    @Schema(description = "预案名称", example = "火灾应急预案")
    private String planName;

    @Schema(description = "预案类型", example = "1")
    private Integer planType;

    @Schema(description = "预案分组ID（-1表示查询未分组预案）", example = "1024")
    private Long planGroupId;

    @Schema(description = "状态", example = "published")
    private String status;
}

