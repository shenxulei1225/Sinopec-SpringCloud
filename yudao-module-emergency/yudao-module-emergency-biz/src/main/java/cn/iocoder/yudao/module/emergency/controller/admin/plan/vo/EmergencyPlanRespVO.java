package cn.iocoder.yudao.module.emergency.controller.admin.plan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 应急预案 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmergencyPlanRespVO extends EmergencyPlanBaseVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "审核信息")
    private Map<String, Object> reviewInfo;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "预案步骤列表 (树形结构)")
    private List<EmergencyPlanStepRespVO> steps;

    @Schema(description = "预案类型名称")
    private String planTypeName;

    @Schema(description = "预案附件列表")
    private List<EmergencyPlanAttachmentRespVO> attachments;

}

















