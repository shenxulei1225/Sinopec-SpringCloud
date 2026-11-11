package cn.cheers.x.workorder.controller.admin.vo.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理后台 - 工单 Response VO
 */
@Schema(description = "管理后台 - 工单 Response VO")
@Data
public class WorkOrderRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "工单编号", example = "WO-20260719-0001")
    private String woNo;

    @Schema(description = "业务域范围", example = "inspection")
    private String scope;

    @Schema(description = "工单状态", example = "DISPATCHED")
    private String status;

    @Schema(description = "工单标题", example = "离心泵月检工单")
    private String title;

    @Schema(description = "关联资产 ID", example = "1001")
    private Long assetId;

    @Schema(description = "资产类型编码", example = "pump")
    private String assetTypeCode;

    @Schema(description = "频率编码", example = "MONTHLY")
    private String frequencyCode;

    @Schema(description = "现场作业标准 ID", example = "20")
    private Long standardId;

    @Schema(description = "现场作业标准版本号", example = "2")
    private Integer standardVersionNo;

    @Schema(description = "现场作业标准快照 JSON")
    private String standardSnapshotJson;

    @Schema(description = "运行时作业 ID")
    private String runtimeJobId;

    @Schema(description = "排程槽位 ID")
    private String scheduleSlotId;

    @Schema(description = "业务键")
    private String businessKey;

    @Schema(description = "派单班组 ID（人员编组分类 id）")
    private Long assigneeTeamId;

    @Schema(description = "执行人用户 ID（可空 = 班组待领）")
    private Long assigneeUserId;

    @Schema(description = "工作内容。接单人要处理的事")
    private String workContent;

    @Schema(description = "处理时刻。报修派工写入")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handlingTime;

    @Schema(description = "计划开始。与计划结束成对")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime plannedStartTime;

    @Schema(description = "计划结束。与计划开始成对")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime plannedFinishTime;

    @Schema(description = "步骤执行结果")
    private List<WorkOrderStepResultRespVO> steps;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
