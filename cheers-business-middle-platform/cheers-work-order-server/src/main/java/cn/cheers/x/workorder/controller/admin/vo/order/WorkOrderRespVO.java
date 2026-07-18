package cn.cheers.x.workorder.controller.admin.vo.order;

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

    @Schema(description = "指派人用户 ID")
    private Long assigneeUserId;

    @Schema(description = "步骤执行结果")
    private List<WorkOrderStepResultRespVO> steps;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
