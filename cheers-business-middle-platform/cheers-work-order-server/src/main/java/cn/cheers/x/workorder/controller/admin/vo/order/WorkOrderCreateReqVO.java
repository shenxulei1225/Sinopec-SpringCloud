package cn.cheers.x.workorder.controller.admin.vo.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 工单创建 Request VO
 */
@Schema(description = "管理后台 - 工单创建 Request VO")
@Data
public class WorkOrderCreateReqVO {

    @Schema(description = "业务域范围", requiredMode = Schema.RequiredMode.REQUIRED, example = "inspection")
    @NotBlank(message = "业务域范围不能为空")
    @Size(max = 32, message = "业务域范围长度不能超过32个字符")
    private String scope;

    @Schema(description = "工单标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "离心泵月检工单")
    @NotBlank(message = "工单标题不能为空")
    @Size(max = 256, message = "工单标题长度不能超过256个字符")
    private String title;

    @Schema(description = "工作内容。告诉接单人要处理什么")
    @Size(max = 8000, message = "工作内容长度不能超过8000个字符")
    private String workContent;

    @Schema(description = "关联资产 ID", example = "1001")
    private Long assetId;

    @Schema(description = "资产类型编码", example = "pump")
    @Size(max = 64, message = "资产类型编码长度不能超过64个字符")
    private String assetTypeCode;

    @Schema(description = "频率编码", example = "MONTHLY")
    @Size(max = 32, message = "频率编码长度不能超过32个字符")
    private String frequencyCode;

    @Schema(description = "已挂在处理内容上的标准流程 ID。没挂上则不传，工单不写步骤快照", example = "20")
    private Long standardId;

    @Schema(description = "现场作业标准编码（取最新已发布版本）", example = "pump-monthly")
    @Size(max = 64, message = "标准编码长度不能超过64个字符")
    private String standardCode;

    @Schema(description = "运行时作业 ID", example = "job-001")
    @Size(max = 64, message = "运行时作业 ID 长度不能超过64个字符")
    private String runtimeJobId;

    @Schema(description = "排程槽位 ID", example = "slot-001")
    @Size(max = 64, message = "排程槽位 ID 长度不能超过64个字符")
    private String scheduleSlotId;

    @Schema(description = "业务键", example = "insp-20260719-001")
    @Size(max = 128, message = "业务键长度不能超过128个字符")
    private String businessKey;

    @Schema(description = "派单班组 ID（人员编组分类 id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "12")
    @NotNull(message = "派单班组不能为空")
    private Long assigneeTeamId;

    @Schema(description = "执行人用户 ID（可空表示班组待领）", example = "1")
    private Long assigneeUserId;

    @Schema(description = "处理时刻。报修派工填写")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handlingTime;

    @Schema(description = "计划开始。与计划结束成对")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime plannedStartTime;

    @Schema(description = "计划结束。与计划开始成对")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime plannedFinishTime;

}
